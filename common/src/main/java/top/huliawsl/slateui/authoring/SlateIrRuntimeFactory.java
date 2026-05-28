package top.huliawsl.slateui.authoring;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringJoiner;
import java.util.Map;
import java.util.Set;
import java.util.Objects;
import net.minecraft.network.chat.Component;
import top.huliawsl.slateui.api.HorizontalAlign;
import top.huliawsl.slateui.api.InputValueHandler;
import top.huliawsl.slateui.api.container.ContainerSlot;
import top.huliawsl.slateui.api.container.ContainerSlotProvider;
import top.huliawsl.slateui.api.MutableStateProvider;
import top.huliawsl.slateui.api.SlateBorder;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateScreen;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;
import top.huliawsl.slateui.api.StateProvider;
import top.huliawsl.slateui.api.Theme;
import top.huliawsl.slateui.api.VerticalAlign;
import top.huliawsl.slateui.api.component.Box;
import top.huliawsl.slateui.api.component.Conditional;
import top.huliawsl.slateui.api.component.Image;
import top.huliawsl.slateui.api.component.Input;
import top.huliawsl.slateui.api.component.Modal;
import top.huliawsl.slateui.api.component.OverlayRoot;
import top.huliawsl.slateui.api.component.Panel;
import top.huliawsl.slateui.api.component.Popup;
import top.huliawsl.slateui.api.component.ScrollView;
import top.huliawsl.slateui.api.component.SlotGrid;
import top.huliawsl.slateui.api.component.Stack;
import top.huliawsl.slateui.api.component.Text;
import top.huliawsl.slateui.api.component.Toggle;
import top.huliawsl.slateui.api.component.Tooltip;
import top.huliawsl.slateui.binding.BindingEvaluator;
import top.huliawsl.slateui.debug.SlateRuntimeException;
import top.huliawsl.slateui.binding.BindingParser;
import top.huliawsl.slateui.command.SlateCommandRegistry;
import top.huliawsl.slateui.override.SlateOverrideRegistry;
import top.huliawsl.slateui.layout.Insets;

public final class SlateIrRuntimeFactory {

    private static final int SUPPORTED_SCHEMA = 1;

    private final SlateComponentRegistry registry;
    private final SlateOverrideRegistry overrideRegistry;

    public SlateIrRuntimeFactory() {
        this(null, SlateOverrideRegistry.EMPTY);
    }

    public SlateIrRuntimeFactory(SlateComponentRegistry registry) {
        this(registry, SlateOverrideRegistry.EMPTY);
    }

    public SlateIrRuntimeFactory(SlateComponentRegistry registry, SlateOverrideRegistry overrideRegistry) {
        this.registry = createDefaultRegistry(registry);
        this.overrideRegistry = overrideRegistry == null ? SlateOverrideRegistry.EMPTY : overrideRegistry;
    }

    public SlateScreen createScreen(Component title, String resourcePath, SlateCommandRegistry commands, StateProvider provider, Theme theme, boolean debugEnabled) {
        JsonObject ir = overrideRegistry.applyComponentOverride(resourcePath, SlateIrLoader.load(resourcePath));
        int schemaVersion = ir.get("schemaVersion").getAsInt();
        if (schemaVersion != SUPPORTED_SCHEMA) {
            throw new IllegalStateException("Unsupported Slate IR schema: " + schemaVersion);
        }
        RuntimeBuildContext context = RuntimeBuildContext.root(
            resourcePath,
            provider == null ? StateProvider.EMPTY : provider,
            overrideRegistry.applyThemeOverride(theme),
            ir.getAsJsonObject("scopedStyle")
        );
        SlateComponent root = buildComponentNode(ir.getAsJsonObject("root"), context);
        return new SlateScreen(title, root, commands, provider, context.theme(), debugEnabled, context.bindingDump());
    }

    public SlateComponent buildComponentTree(JsonObject root, StateProvider provider, Theme theme) {
        return buildComponentNode(root, RuntimeBuildContext.root("<memory>", provider == null ? StateProvider.EMPTY : provider, theme == null ? Theme.DEFAULT : theme, new JsonObject()));
    }

    SlateComponent buildComponentNode(JsonObject node, RuntimeBuildContext context) {
        String forExpression = directive(node, "for");
        if (forExpression != null) {
            return new SlateForEachComponent(this, stripForDirectives(node), context, directive(node, "alias"), forExpression, directive(node, "key"));
        }

        String type = node.get("componentType").getAsString();
        context.recordComponent(type);
        List<SlateComponent> children = buildChildren(node, context);
        Map<String, List<SlateComponent>> namedSlots = buildNamedSlots(node, context);
        SlateComponent component;
        try {
            component = registry.require(type).create(node, children, namedSlots, context);
        } catch (SlateRuntimeException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throw new SlateRuntimeException("build", context.nodePath(), "componentType=" + type, throwable);
        }

        String ifExpression = directive(node, "if");
        if (ifExpression != null) {
            context.recordBinding(type, "if", ifExpression, "<runtime predicate>");
            return new Conditional(() -> evaluateBinding(type, "if", ifExpression, context), component);
        }
        return component;
    }

    private List<SlateComponent> buildChildren(JsonObject node, RuntimeBuildContext context) {
        List<SlateComponent> children = new ArrayList<>();
        JsonArray childNodes = node.getAsJsonArray("children");
        if (childNodes == null) {
            return children;
        }
        for (int index = 0; index < childNodes.size(); index++) {
            JsonElement child = childNodes.get(index);
            children.add(buildComponentNode(child.getAsJsonObject(), context.withNode("children[" + index + "]")));
        }
        return children;
    }

    private Map<String, List<SlateComponent>> buildNamedSlots(JsonObject node, RuntimeBuildContext context) {
        Map<String, List<SlateComponent>> slots = new LinkedHashMap<>();
        JsonObject slotObject = node.getAsJsonObject("slots");
        if (slotObject == null) {
            return slots;
        }
        for (String slotName : slotObject.keySet()) {
            List<SlateComponent> components = new ArrayList<>();
            JsonArray slotChildren = slotObject.getAsJsonArray(slotName);
            for (int index = 0; index < slotChildren.size(); index++) {
                JsonElement child = slotChildren.get(index);
                components.add(buildComponentNode(child.getAsJsonObject(), context.withNode("slots." + slotName + "[" + index + "]")));
            }
            slots.put(slotName, List.copyOf(components));
        }
        return slots;
    }

    private SlateComponentRegistry createDefaultRegistry(SlateComponentRegistry extraRegistry) {
        SlateComponentRegistry defaults = new SlateComponentRegistry()
            .register("OverlayRoot", (node, children, namedSlots, context) -> new OverlayRoot(children, resolveStyle(node, context)))
            .register("Box", (node, children, namedSlots, context) -> new Box(children, resolveStyle(node, context)))
            .register("Panel", (node, children, namedSlots, context) -> new Panel(
                prop(node, "title", ""),
                children,
                namedSlots,
                resolveStyle(node, context),
                SlateStyle.builder().gap(propInt(node, "contentGap", 8)).build()
            ))
            .register("Stack", (node, children, namedSlots, context) -> new Stack(
                "row".equalsIgnoreCase(prop(node, "direction", "column")) ? StackDirection.ROW : StackDirection.COLUMN,
                children,
                resolveStyle(node, context)
            ))
            .register("Text", (node, children, namedSlots, context) -> new Text(
                context.provider(),
                ignored -> resolveString(node, "value", context),
                resolveStyle(node, context)
            ))
            .register("Button", (node, children, namedSlots, context) -> children.isEmpty()
                ? new top.huliawsl.slateui.api.component.Button(resolveString(node, "label", context), prop(node, "command", null), resolveStyle(node, context))
                : new top.huliawsl.slateui.api.component.Button(children, prop(node, "command", null), resolveStyle(node, context)))
            .register("Input", (node, children, namedSlots, context) -> new Input(
                context.provider(),
                prop(node, "placeholder", ""),
                ignored -> resolveString(node, "value", context),
                prop(node, "onInput", null),
                prop(node, "onChange", null),
                prop(node, "onCommit", prop(node, "onChange", null)),
                createInputHandler(node, context),
                propInt(node, "maxLength", 0),
                resolveStyle(node, context)
            ))
            .register("Toggle", (node, children, namedSlots, context) -> new Toggle(
                context.provider(),
                resolveString(node, "label", context),
                ignored -> resolveBoolean(node, "checked", context),
                prop(node, "onChange", prop(node, "command", null)),
                createToggleHandler(node, context),
                resolveStyle(node, context)
            ))
            .register("List", (node, children, namedSlots, context) -> new top.huliawsl.slateui.api.component.SlateList(
                children,
                namedSlots,
                resolveStyle(node, context),
                SlateStyle.builder().gap(propInt(node, "itemGap", 6)).build()
            ))
            .register("ScrollView", (node, children, namedSlots, context) -> new ScrollView(singleChild(children), resolveStyle(node, context)))
            .register("Image", (node, children, namedSlots, context) -> new Image(
                prop(node, "resource", "minecraft:textures/gui/options_background.png"),
                propInt(node, "u", 0),
                propInt(node, "v", 0),
                propInt(node, "textureWidth", 256),
                propInt(node, "textureHeight", 256),
                propInt(node, "regionWidth", 0),
                propInt(node, "regionHeight", 0),
                resolveStyle(node, context)
            ))
            .register("SlotGrid", (node, children, namedSlots, context) -> new SlotGrid(
                createSlotProvider(node, context),
                propInt(node, "columns", 9),
                propInt(node, "slotSize", 18),
                propInt(node, "slotGap", 2),
                prop(node, "command", null),
                resolveStyle(node, context)
            ))
            .register("Tooltip", (node, children, namedSlots, context) -> new Tooltip(singleChild(children), slotChild(namedSlots, "tooltip"), resolveStyle(node, context)))
            .register("Popup", (node, children, namedSlots, context) -> new Popup(
                singleChild(children),
                slotChild(namedSlots, "popup"),
                () -> resolveObject(node, "open", context),
                resolveStyle(node, context)
            ))
            .register("Modal", (node, children, namedSlots, context) -> new Modal(
                singleChild(children),
                slotChild(namedSlots, "modal"),
                () -> resolveObject(node, "open", context),
                resolveStyle(node, context)
            ));
        if (extraRegistry == null) {
            return defaults;
        }
        for (Map.Entry<String, SlateComponentRegistry.Factory> entry : extraRegistry.factories().entrySet()) {
            defaults.register(entry.getKey(), entry.getValue());
        }
        return defaults;
    }

    private static ContainerSlotProvider createSlotProvider(JsonObject node, RuntimeBuildContext context) {
        String binding = binding(node, "slots");
        String path = binding == null ? prop(node, "slots", null) : binding;
        if (path == null || path.isBlank()) {
            return ContainerSlotProvider.empty();
        }
        context.recordBinding("SlotGrid", "slots", path, "<runtime slots>");
        return () -> toSlots(evaluateBinding("SlotGrid", "slots", path, context));
    }

    private static List<ContainerSlot> toSlots(Object value) {
        if (value == null) {
            return List.of();
        }
        if (!(value instanceof Iterable<?> iterable)) {
            throw new IllegalArgumentException("expected iterable slots but got " + value.getClass().getSimpleName());
        }
        List<ContainerSlot> slots = new ArrayList<>();
        int fallbackIndex = 0;
        for (Object entry : iterable) {
            if (entry instanceof ContainerSlot slot) {
                slots.add(slot);
                fallbackIndex++;
                continue;
            }
            if (entry instanceof Map<?, ?> map) {
                int index = intValue(mapValue(map, "index", fallbackIndex), fallbackIndex);
                String itemId = stringValue(map.containsKey("itemId") ? map.get("itemId") : mapValue(map, "item", ""));
                int count = intValue(mapValue(map, "count", 0), 0);
                boolean enabled = boolValue(mapValue(map, "enabled", true));
                slots.add(new ContainerSlot(index, itemId, count, enabled));
                fallbackIndex++;
            }
        }
        return List.copyOf(slots);
    }

    private static Object mapValue(Map<?, ?> map, String key, Object fallback) {
        return map.containsKey(key) ? map.get(key) : fallback;
    }

    private static int intValue(Object value, int fallback) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static boolean boolValue(Object value) {
        if (value instanceof Boolean bool) {
            return bool;
        }
        return value == null || Boolean.parseBoolean(String.valueOf(value));
    }

    private static String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private static InputValueHandler createInputHandler(JsonObject node, RuntimeBuildContext context) {
        String binding = binding(node, "value");
        if (binding == null || !(context.provider() instanceof MutableStateProvider mutableProvider)) {
            return null;
        }
        String path = BindingParser.normalize(binding);
        return (interactionContext, value) -> mutableProvider.set(path, value);
    }

    private static top.huliawsl.slateui.api.ToggleValueHandler createToggleHandler(JsonObject node, RuntimeBuildContext context) {
        String binding = binding(node, "checked");
        if (binding == null || !(context.provider() instanceof MutableStateProvider mutableProvider)) {
            return null;
        }
        String path = BindingParser.normalize(binding);
        return (interactionContext, checked) -> mutableProvider.set(path, checked);
    }

    static SlateComponent singleChild(List<SlateComponent> children) {
        if (children.isEmpty()) {
            return new Box(List.of(), SlateStyle.EMPTY);
        }
        if (children.size() == 1) {
            return children.get(0);
        }
        return new Stack(StackDirection.COLUMN, children, SlateStyle.builder().gap(8).build());
    }

    private static SlateComponent slotChild(Map<String, List<SlateComponent>> namedSlots, String slotName) {
        return singleChild(namedSlots.getOrDefault(slotName, List.of()));
    }

    private static Object resolveObject(JsonObject node, String name, RuntimeBuildContext context) {
        String binding = binding(node, name);
        if (binding != null) {
            return evaluateBinding(componentType(node), name, binding, context);
        }
        return prop(node, name, "");
    }

    private static Object evaluateBinding(String componentType, String name, String binding, RuntimeBuildContext context) {
        String expression = BindingParser.normalize(binding);
        try {
            Object value = BindingEvaluator.evaluateStrict(expression, context.provider());
            context.recordBinding(componentType, name, expression, summarizeValue(value));
            return value;
        } catch (SlateRuntimeException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throw SlateRuntimeException.binding(context.nodePath() + " " + componentType + "." + name, expression, throwable);
        }
    }

    private static String componentType(JsonObject node) {
        return node != null && node.has("componentType") ? node.get("componentType").getAsString() : "<unknown>";
    }

    private static String summarizeValue(Object value) {
        String text = String.valueOf(value);
        text = text.replace("\r", "\\r").replace("\n", "\\n");
        return text.length() <= 64 ? text : text.substring(0, 64) + "...";
    }

    static boolean resolveBoolean(JsonObject node, String name, RuntimeBuildContext context) {
        Object value = resolveObject(node, name, context);
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof Number number) {
            return number.doubleValue() != 0D;
        }
        if (value == null) {
            return false;
        }
        String text = String.valueOf(value).trim();
        if ("true".equalsIgnoreCase(text)) {
            return true;
        }
        if ("false".equalsIgnoreCase(text) || text.isBlank()) {
            return false;
        }
        throw SlateRuntimeException.binding(context.nodePath() + " " + componentType(node) + "." + name, String.valueOf(value),
            new IllegalArgumentException("expected boolean but got " + value.getClass().getSimpleName()));
    }

    static String resolveString(JsonObject node, String name, RuntimeBuildContext context) {
        Object value = resolveObject(node, name, context);
        return value == null ? "" : String.valueOf(value);
    }

    static String binding(JsonObject node, String name) {
        JsonObject bindings = node.getAsJsonObject("bindings");
        return bindings != null && bindings.has(name) ? bindings.get(name).getAsString() : null;
    }

    private static int propInt(JsonObject node, String name, int fallback) {
        String value = prop(node, name, null);
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return Integer.parseInt(value);
    }

    private static String prop(JsonObject node, String name, String fallback) {
        JsonObject props = node.getAsJsonObject("props");
        return props != null && props.has(name) ? props.get(name).getAsString() : fallback;
    }

    static String directive(JsonObject node, String name) {
        JsonObject directives = node.getAsJsonObject("directives");
        return directives != null && directives.has(name) ? directives.get(name).getAsString() : null;
    }

    private static JsonObject stripForDirectives(JsonObject node) {
        JsonObject copy = node.deepCopy();
        JsonObject directives = copy.getAsJsonObject("directives");
        if (directives == null) {
            return copy;
        }
        directives.remove("for");
        directives.remove("alias");
        directives.remove("key");
        if (directives.size() == 0) {
            copy.remove("directives");
        }
        return copy;
    }

    private static SlateStyle resolveStyle(JsonObject node, RuntimeBuildContext context) {
        JsonObject props = node.getAsJsonObject("props");
        SlateStyle.Builder builder = SlateStyle.builder();
        applyScopedStyle(builder, context, componentType(node), null);
        applyScopedStyle(builder, context, componentType(node), "hover");
        applyScopedStyle(builder, context, componentType(node), "pressed");
        applyScopedStyle(builder, context, componentType(node), "active");
        applyScopedStyle(builder, context, componentType(node), "focus");
        applyScopedStyle(builder, context, componentType(node), "disabled");
        String className = props != null && props.has("class") ? props.get("class").getAsString() : null;
        if (className != null) {
            for (String singleClass : className.split("\\s+")) {
                if (singleClass.isBlank()) {
                    continue;
                }
                applyScopedStyle(builder, context, singleClass, null);
                applyScopedStyle(builder, context, singleClass, "hover");
                applyScopedStyle(builder, context, singleClass, "pressed");
                applyScopedStyle(builder, context, singleClass, "active");
                applyScopedStyle(builder, context, singleClass, "focus");
                applyScopedStyle(builder, context, singleClass, "disabled");
            }
        }
        String id = props != null && props.has("id") ? props.get("id").getAsString() : null;
        if (id != null && !id.isBlank()) {
            applyScopedStyle(builder, context, "#" + id, null);
            applyScopedStyle(builder, context, "#" + id, "hover");
            applyScopedStyle(builder, context, "#" + id, "pressed");
            applyScopedStyle(builder, context, "#" + id, "active");
            applyScopedStyle(builder, context, "#" + id, "focus");
            applyScopedStyle(builder, context, "#" + id, "disabled");
        }
        if (props != null) {
            JsonObject inline = new JsonObject();
            for (String key : props.keySet()) {
                if (key.startsWith("style-")) {
                    inline.add(key.substring("style-".length()), props.get(key));
                }
            }
            applyStyleObject(builder, inline, context, "inline", null);
        }
        return builder.build();
    }

    private static void applyScopedStyle(SlateStyle.Builder builder, RuntimeBuildContext context, String className, String state) {
        String key = state == null ? className : className + ":" + state;
        if (context.scopedStyle().has(key)) {
            applyStyleObject(builder, context.scopedStyle().getAsJsonObject(key), context, "." + key, state);
        }
    }

    private static void applyStyleObject(SlateStyle.Builder builder, JsonObject styleObject, RuntimeBuildContext context, String origin, String state) {
        for (String key : styleObject.keySet()) {
            String value = styleObject.get(key).getAsString();
            try {
                applyStyleProperty(builder, key, value, state);
                context.recordStyle(key, value, origin, state);
            } catch (SlateRuntimeException exception) {
                throw exception;
            } catch (Throwable throwable) {
                throw new SlateRuntimeException("style", context.nodePath(), "property=" + key + " value=" + value + " origin=" + origin, throwable);
            }
        }
    }

    private static void applyStyleProperty(SlateStyle.Builder builder, String key, String value, String state) {
        String normalized = normalizeStyleKey(key);
        if (state != null && !stateSupportsProperty(normalized, state)) {
            return;
        }
        if ("focus".equals(state) && Set.of("bordercolor", "border-color").contains(normalized)) {
            builder.focusBorder(new SlateBorder(parseColor(value), 1));
            return;
        }
        switch (normalized) {
            case "width" -> builder.width(parseInt(value, key));
            case "height" -> builder.height(parseInt(value, key));
            case "minwidth", "min-width" -> builder.minWidth(parseInt(value, key));
            case "minheight", "min-height" -> builder.minHeight(parseInt(value, key));
            case "maxwidth", "max-width" -> builder.maxWidth(parseInt(value, key));
            case "maxheight", "max-height" -> builder.maxHeight(parseInt(value, key));
            case "gap" -> builder.gap(parseInt(value, key));
            case "gaptoken", "gap-token" -> builder.gapToken(value);
            case "padding" -> builder.padding(Insets.all(parseInt(value, key)));
            case "margin" -> throw new IllegalArgumentException("margin is not supported in SlateUI v0.1");
            case "background", "backgroundcolor", "background-color" -> applyBackground(builder, parseColor(value), state);
            case "backgroundtoken", "background-token" -> applyBackgroundToken(builder, value, state);
            case "hoverbackground", "hover-background", "hoverbackgroundcolor", "hover-background-color" -> builder.hoverBackgroundColor(parseColor(value));
            case "hoverbackgroundtoken", "hover-background-token" -> builder.hoverBackgroundToken(value);
            case "activebackground", "active-background", "activebackgroundcolor", "active-background-color" -> builder.activeBackgroundColor(parseColor(value));
            case "activebackgroundtoken", "active-background-token" -> builder.activeBackgroundToken(value);
            case "bordercolor", "border-color" -> builder.border(new SlateBorder(parseColor(value), 1));
            case "bordercolortoken", "border-color-token" -> builder.borderColorToken(value);
            case "borderthickness", "border-thickness" -> builder.border(new SlateBorder(0xFF334155, parseInt(value, key)));
            case "borderradius", "border-radius", "radius" -> builder.borderRadius(parseInt(value, key));
            case "borderradiustoken", "border-radius-token", "radiustoken", "radius-token" -> builder.borderRadiusToken(value);
            case "focusbordercolor", "focus-border-color" -> builder.focusBorder(new SlateBorder(parseColor(value), 1));
            case "focusbordercolortoken", "focus-border-color-token" -> builder.focusBorderColorToken(value);
            case "focusborderthickness", "focus-border-thickness" -> builder.focusBorder(new SlateBorder(0xFF60A5FA, parseInt(value, key)));
            case "horizontalalign", "horizontal-align", "alignx", "align-x", "align" -> builder.horizontalAlign(HorizontalAlign.valueOf(value.trim().toUpperCase()));
            case "verticalalign", "vertical-align", "aligny", "align-y", "justify" -> builder.verticalAlign(VerticalAlign.valueOf(value.trim().toUpperCase()));
            case "textcolor", "text-color" -> builder.textColor(parseColor(value));
            case "textcolortoken", "text-color-token" -> builder.textColorToken(value);
            case "clipcontent", "clip-content" -> builder.clipContent(parseBoolean(value, key));
            case "disabled" -> builder.disabled(parseBoolean(value, key));
            default -> throw new IllegalArgumentException("unknown style property '" + key + "'");
        }
    }

    private static boolean stateSupportsProperty(String property, String state) {
        if ("hover".equals(state)) {
            return Set.of("background", "backgroundcolor", "background-color", "backgroundtoken", "background-token",
                "hoverbackground", "hover-background", "hoverbackgroundcolor", "hover-background-color", "hoverbackgroundtoken", "hover-background-token").contains(property);
        }
        if ("pressed".equals(state) || "active".equals(state)) {
            return Set.of("background", "backgroundcolor", "background-color", "backgroundtoken", "background-token",
                "activebackground", "active-background", "activebackgroundcolor", "active-background-color", "activebackgroundtoken", "active-background-token").contains(property);
        }
        if ("focus".equals(state)) {
            return Set.of("bordercolor", "border-color", "focusbordercolor", "focus-border-color",
                "focusbordercolortoken", "focus-border-color-token", "focusborderthickness", "focus-border-thickness").contains(property);
        }
        return false;
    }

    private static String normalizeStyleKey(String key) {
        return key == null ? "" : key.trim().replace("_", "-").toLowerCase();
    }

    private static void applyBackground(SlateStyle.Builder builder, int color, String state) {
        if ("hover".equals(state)) {
            builder.hoverBackgroundColor(color);
        } else if ("pressed".equals(state) || "active".equals(state)) {
            builder.activeBackgroundColor(color);
        } else {
            builder.backgroundColor(color);
        }
    }

    private static void applyBackgroundToken(SlateStyle.Builder builder, String token, String state) {
        if ("hover".equals(state)) {
            builder.hoverBackgroundToken(token);
        } else if ("pressed".equals(state) || "active".equals(state)) {
            builder.activeBackgroundToken(token);
        } else {
            builder.backgroundToken(token);
        }
    }

    private static int parseInt(String value, String key) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("expected integer for " + key, exception);
        }
    }

    private static boolean parseBoolean(String value, String key) {
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        throw new IllegalArgumentException("expected true | false for " + key);
    }

    private static int parseColor(String value) {
        String text = value.trim();
        if (text.startsWith("#")) {
            String hex = text.substring(1);
            if (hex.length() == 6) {
                return (int) (0xFF000000L | Long.parseLong(hex, 16));
            }
            if (hex.length() == 8) {
                return (int) Long.parseLong(hex, 16);
            }
            throw new IllegalArgumentException("expected #RRGGBB or #AARRGGBB");
        }
        return Integer.decode(text);
    }

    public record RuntimeBuildContext(
        String resourcePath,
        String nodePath,
        StateProvider provider,
        Theme theme,
        JsonObject scopedStyle,
        List<String> bindingTrace
    ) {

        public RuntimeBuildContext {
            Objects.requireNonNull(resourcePath, "resourcePath");
            nodePath = nodePath == null || nodePath.isBlank() ? "root" : nodePath;
            Objects.requireNonNull(provider, "provider");
            Objects.requireNonNull(theme, "theme");
            scopedStyle = scopedStyle == null ? new JsonObject() : scopedStyle;
            bindingTrace = bindingTrace == null ? new ArrayList<>() : bindingTrace;
        }

        public static RuntimeBuildContext root(String resourcePath, StateProvider provider, Theme theme, JsonObject scopedStyle) {
            return new RuntimeBuildContext(resourcePath, "root", provider, theme, scopedStyle, new ArrayList<>());
        }

        public RuntimeBuildContext withProvider(StateProvider nextProvider) {
            return new RuntimeBuildContext(resourcePath, nodePath, nextProvider, theme, scopedStyle, bindingTrace);
        }

        public RuntimeBuildContext withNode(String childPath) {
            return new RuntimeBuildContext(resourcePath, nodePath + "/" + childPath, provider, theme, scopedStyle, bindingTrace);
        }

        public void recordComponent(String componentType) {
            appendTrace("component " + nodePath + " type=" + componentType);
        }

        public void recordBinding(String componentType, String name, String expression, String value) {
            appendTrace("binding " + nodePath + " " + componentType + "." + name + " <- " + expression + " => " + value);
        }

        public void recordStyle(String property, String value, String origin, String state) {
            appendTrace("style " + nodePath + " " + property + "=" + value
                + " origin=" + origin
                + (state == null ? "" : " state=" + state));
        }

        public String bindingDump() {
            if (bindingTrace.isEmpty()) {
                return "<none>";
            }
            StringJoiner joiner = new StringJoiner("\n");
            for (String entry : bindingTrace) {
                joiner.add(entry);
            }
            return joiner.toString();
        }

        private void appendTrace(String entry) {
            if (bindingTrace.size() == 128) {
                bindingTrace.remove(0);
            }
            bindingTrace.add(entry);
        }
    }
}
