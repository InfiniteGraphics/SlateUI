package top.huliawsl.slateui.authoring;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public final class SlateCompiler {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Pattern STYLE_PATTERN = Pattern.compile("<style\\s+scoped>(.*?)</style>", Pattern.DOTALL);
    private static final Pattern STYLE_RULE_PATTERN = Pattern.compile("\\.(?<name>[a-zA-Z0-9_-]+)\\s*\\{(?<body>.*?)\\}", Pattern.DOTALL);
    private static final Pattern SLOT_SHORTHAND_PATTERN = Pattern.compile("<template\\s+#([a-zA-Z0-9_-]+)([^>]*)>");
    private static final Set<String> BUILTIN_COMPONENTS = Set.of("OverlayRoot", "Box", "Stack", "Text", "Button", "Input", "ScrollView", "Image", "Tooltip", "Popup", "Modal");
    private static final Set<String> BUILTIN_ONLY_SLOTS = Set.of("tooltip", "popup", "modal");
    private static final Set<String> DIRECTIVES = Set.of("if", "for", "key");
    private static final Map<String, Set<String>> PROPS = Map.ofEntries(
        Map.entry("OverlayRoot", Set.of("class")),
        Map.entry("Box", Set.of("class")),
        Map.entry("Stack", Set.of("class", "direction")),
        Map.entry("Text", Set.of("class", "value")),
        Map.entry("Button", Set.of("class", "label", "command")),
        Map.entry("Input", Set.of("class", "placeholder", "value", "onChange")),
        Map.entry("ScrollView", Set.of("class")),
        Map.entry("Image", Set.of("class", "resource")),
        Map.entry("Tooltip", Set.of("class")),
        Map.entry("Popup", Set.of("class", "open")),
        Map.entry("Modal", Set.of("class", "open"))
    );

    public void compileDirectory(Path inputDir, Path outputDir) throws Exception {
        Files.createDirectories(outputDir);
        if (!Files.isDirectory(inputDir)) {
            return;
        }
        try (var paths = Files.walk(inputDir)) {
            for (Path input : paths.filter(path -> path.toString().endsWith(".slate")).toList()) {
                compileFile(input, outputDir.resolve(inputDir.relativize(input).toString().replace('\\', '/').replace(".slate", ".json")));
            }
        }
    }

    public void compileFile(Path inputFile, Path outputFile) throws Exception {
        String source = preprocess(Files.readString(inputFile, StandardCharsets.UTF_8));
        String template = extractTemplateBlock(source, inputFile);
        String styleBlock = extractOptionalBlock(STYLE_PATTERN, source);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);
        Document document = factory.newDocumentBuilder().parse(new InputSource(new StringReader("<root>" + template + "</root>")));
        Element rootElement = firstElement(document.getDocumentElement().getChildNodes(), inputFile);
        JsonArray sourceMap = new JsonArray();
        JsonObject root = compileElement(rootElement, source, inputFile, "0", sourceMap);
        JsonObject ir = new JsonObject();
        ir.addProperty("schemaVersion", 1);
        ir.addProperty("sourceFile", inputFile.getFileName().toString());
        ir.add("root", root);
        ir.add("scopedStyle", parseStyle(styleBlock, source, inputFile));
        ir.add("sourceMap", sourceMap);
        Files.createDirectories(outputFile.getParent());
        Files.writeString(outputFile, GSON.toJson(ir), StandardCharsets.UTF_8);
    }

    private static JsonObject compileElement(Element element, String source, Path inputFile, String path, JsonArray sourceMap) {
        String componentType = element.getTagName();
        if (!isComponentTag(componentType)) {
            throw error(inputFile, source, "<" + componentType + ">", "Unknown component '" + componentType + "'");
        }
        JsonObject node = new JsonObject();
        node.addProperty("componentType", componentType);
        JsonObject props = new JsonObject();
        JsonObject bindings = new JsonObject();
        JsonObject directives = new JsonObject();
        NamedNodeMap attributes = element.getAttributes();
        for (int index = 0; index < attributes.getLength(); index++) {
            Node attribute = attributes.item(index);
            String name = attribute.getNodeName();
            String value = attribute.getNodeValue().trim();
            if (name.startsWith("style-")) {
                props.addProperty(name, value);
                continue;
            }
            if (DIRECTIVES.contains(name)) {
                if ("for".equals(name)) {
                    parseForDirective(inputFile, source, value, directives);
                } else {
                    directives.addProperty(name, value);
                }
                continue;
            }
            if (isBuiltin(componentType) && !PROPS.getOrDefault(componentType, Set.of()).contains(name)) {
                throw error(inputFile, source, name + "=\"", "Unknown prop '" + name + "' on component '" + componentType + "'");
            }
            if (value.startsWith("{") && value.endsWith("}")) {
                bindings.addProperty(name, value);
            } else {
                props.addProperty(name, value);
            }
        }
        if (componentType.equals("Text") && !props.has("value") && !bindings.has("value")) {
            String textContent = element.getTextContent() == null ? "" : element.getTextContent().trim();
            if (!textContent.isEmpty()) {
                props.addProperty("value", textContent);
            }
        }
        node.add("props", props);
        node.add("bindings", bindings);
        if (directives.size() > 0) {
            node.add("directives", directives);
        }

        JsonArray children = new JsonArray();
        JsonObject slots = new JsonObject();
        NodeList childNodes = element.getChildNodes();
        int childIndex = 0;
        int slotIndex = 0;
        for (int index = 0; index < childNodes.getLength(); index++) {
            Node child = childNodes.item(index);
            if (!(child instanceof Element childElement)) {
                continue;
            }
            if ("template".equals(childElement.getTagName())) {
                String slotName = childElement.getAttribute("slot");
                if (slotName == null || slotName.isBlank()) {
                    throw error(inputFile, source, "<template", "Named slot template requires a slot name");
                }
                if (isBuiltin(componentType) && !"default".equals(slotName) && !BUILTIN_ONLY_SLOTS.contains(slotName)) {
                    throw error(inputFile, source, "<template", "Unknown slot '" + slotName + "' on component '" + componentType + "'");
                }
                JsonArray slotChildren = slots.has(slotName) ? slots.getAsJsonArray(slotName) : new JsonArray();
                NodeList templateChildren = childElement.getChildNodes();
                int templateIndex = 0;
                for (int templateNodeIndex = 0; templateNodeIndex < templateChildren.getLength(); templateNodeIndex++) {
                    Node templateChild = templateChildren.item(templateNodeIndex);
                    if (templateChild instanceof Element templateElement) {
                        slotChildren.add(compileElement(templateElement, source, inputFile, path + ".s" + slotIndex + '.' + templateIndex, sourceMap));
                        templateIndex++;
                    }
                }
                slots.add(slotName, slotChildren);
                slotIndex++;
                continue;
            }
            children.add(compileElement(childElement, source, inputFile, path + '.' + childIndex, sourceMap));
            childIndex++;
        }
        node.add("children", children);
        if (slots.size() > 0) {
            node.add("slots", slots);
        }
        sourceMap.add(sourceMapEntry(path, componentType, source, inputFile, "<" + componentType));
        return node;
    }

    private static void parseForDirective(Path inputFile, String source, String value, JsonObject directives) {
        String[] split = value.split("\\s+in\\s+", 2);
        if (split.length != 2 || split[0].isBlank() || split[1].isBlank()) {
            throw error(inputFile, source, "for=\"", "Invalid for directive '" + value + "'");
        }
        directives.addProperty("alias", split[0].trim());
        directives.addProperty("for", split[1].trim());
    }

    private static JsonObject parseStyle(String styleBlock, String source, Path inputFile) {
        JsonObject scopedStyle = new JsonObject();
        if (styleBlock == null || styleBlock.isBlank()) {
            return scopedStyle;
        }
        Matcher matcher = STYLE_RULE_PATTERN.matcher(styleBlock);
        while (matcher.find()) {
            JsonObject rule = new JsonObject();
            for (String declaration : matcher.group("body").split(";")) {
                String trimmed = declaration.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                String[] parts = trimmed.split(":", 2);
                if (parts.length != 2) {
                    throw error(inputFile, source, trimmed, "Invalid style declaration '" + trimmed + "'");
                }
                rule.addProperty(parts[0].trim(), parts[1].trim());
            }
            scopedStyle.add(matcher.group("name"), rule);
        }
        return scopedStyle;
    }

    private static JsonObject sourceMapEntry(String path, String componentType, String source, Path inputFile, String needle) {
        int index = source.indexOf(needle);
        if (index < 0) {
            index = 0;
        }
        int line = 1;
        int column = 1;
        for (int i = 0; i < index; i++) {
            if (source.charAt(i) == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
        }
        JsonObject entry = new JsonObject();
        entry.addProperty("path", path);
        entry.addProperty("componentType", componentType);
        entry.addProperty("line", line);
        entry.addProperty("column", column);
        entry.addProperty("file", inputFile.getFileName().toString());
        return entry;
    }

    private static Element firstElement(NodeList nodeList, Path inputFile) {
        for (int index = 0; index < nodeList.getLength(); index++) {
            Node node = nodeList.item(index);
            if (node instanceof Element element) {
                return element;
            }
        }
        throw new SlateCompileException("No root component found in template: " + inputFile.getFileName());
    }

    private static String extractBlock(Pattern pattern, String source, Path inputFile, String message) {
        Matcher matcher = pattern.matcher(source);
        if (!matcher.find()) {
            throw new SlateCompileException(message + " in " + inputFile.getFileName());
        }
        return matcher.group(1);
    }

    private static String extractTemplateBlock(String source, Path inputFile) {
        String openTag = "<template>";
        String closeTag = "</template>";
        int start = source.indexOf(openTag);
        int end = source.lastIndexOf(closeTag);
        if (start < 0 || end < 0 || end <= start) {
            throw new SlateCompileException("Missing <template> block in " + inputFile.getFileName());
        }
        return source.substring(start + openTag.length(), end);
    }

    private static String extractOptionalBlock(Pattern pattern, String source) {
        Matcher matcher = pattern.matcher(source);
        return matcher.find() ? matcher.group(1) : null;
    }

    private static String preprocess(String source) {
        return SLOT_SHORTHAND_PATTERN.matcher(source).replaceAll("<template slot=\"$1\"$2>");
    }

    private static boolean isBuiltin(String componentType) {
        return BUILTIN_COMPONENTS.contains(componentType);
    }

    private static boolean isComponentTag(String componentType) {
        return isBuiltin(componentType) || (!componentType.isBlank() && Character.isUpperCase(componentType.charAt(0)));
    }

    private static SlateCompileException error(Path inputFile, String source, String needle, String message) {
        int index = source.indexOf(needle);
        int line = 1;
        int column = 1;
        for (int i = 0; i < index; i++) {
            if (source.charAt(i) == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
        }
        return new SlateCompileException(inputFile.getFileName() + ":" + line + ":" + column + " " + message);
    }
}
