package top.huliawsl.slateui.authoring;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.network.chat.Component;
import top.huliawsl.slateui.api.InputValueHandler;
import top.huliawsl.slateui.api.MutableStateProvider;
import top.huliawsl.slateui.api.SlateBorder;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateScreen;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;
import top.huliawsl.slateui.api.StateProvider;
import top.huliawsl.slateui.api.Theme;
import top.huliawsl.slateui.api.component.Box;
import top.huliawsl.slateui.api.component.Conditional;
import top.huliawsl.slateui.api.component.Image;
import top.huliawsl.slateui.api.component.Input;
import top.huliawsl.slateui.api.component.Modal;
import top.huliawsl.slateui.api.component.OverlayRoot;
import top.huliawsl.slateui.api.component.Popup;
import top.huliawsl.slateui.api.component.ScrollView;
import top.huliawsl.slateui.api.component.Stack;
import top.huliawsl.slateui.api.component.Text;
import top.huliawsl.slateui.api.component.Tooltip;
import top.huliawsl.slateui.binding.BindingEvaluator;
import top.huliawsl.slateui.binding.BindingParser;
import top.huliawsl.slateui.command.SlateCommandRegistry;
import top.huliawsl.slateui.layout.Insets;

public final class SlateIrRuntimeFactory {

    private static final int SUPPORTED_SCHEMA = 1;

    private final SlateComponentRegistry registry;

    public SlateIrRuntimeFactory() {
        this(null);
    }

    public SlateIrRuntimeFactory(SlateComponentRegistry registry) {
        this.registry = createDefaultRegistry(registry);
    }

    public SlateScreen createScreen(Component title, String resourcePath, SlateCommandRegistry commands, StateProvider provider, Theme theme, boolean debugEnabled) {
        JsonObject ir = SlateIrLoader.load(resourcePath);
        int schemaVersion = ir.get("schemaVersion").getAsInt();
        if (schemaVersion != SUPPORTED_SCHEMA) {
            throw new IllegalStateException("Unsupported Slate IR schema: " + schemaVersion);
        }
        RuntimeBuildContext context = new RuntimeBuildContext(
            resourcePath,
            provider == null ? StateProvider.EMPTY : provider,
            theme == null ? Theme.DEFAULT : theme,
            ir.getAsJsonObject("scopedStyle")
        );
        SlateComponent root = buildComponentNode(ir.getAsJsonObject("root"), context);
        return new SlateScreen(title, root, commands, provider, theme, debugEnabled);
    }

    public SlateComponent buildComponentTree(JsonObject root, StateProvider provider, Theme theme) {
        return buildComponentNode(root, new RuntimeBuildContext("<memory>", provider == null ? StateProvider.EMPTY : provider, theme == null ? Theme.DEFAULT : theme, new JsonObject()));
    }

    SlateComponent buildComponentNode(JsonObject node, RuntimeBuildContext context) {
        String forExpression = directive(node, "for");
        if (forExpression != null) {
            return new SlateForEachComponent(this, stripForDirectives(node), context, directive(node, "alias"), forExpression, directive(node, "key"));
        }

        String type = node.get("componentType").getAsString();
        List<SlateComponent> children = buildChildren(node, context);
        Map<String, List<SlateComponent>> namedSlots = buildNamedSlots(node, context);
        SlateComponent component = registry.require(type).create(node, children, namedSlots, context);

        String ifExpression = directive(node, "if");
        if (ifExpression != null) {
            return new Conditional(() -> BindingEvaluator.evaluate(ifExpression, context.provider()), component);
        }
        return component;
    }

    private List<SlateComponent> buildChildren(JsonObject node, RuntimeBuildContext context) {
        List<SlateComponent> children = new ArrayList<>();
        JsonArray childNodes = node.getAsJsonArray("children");
        if (childNodes == null) {
            return children;
        }
        for (JsonElement child : childNodes) {
            children.add(buildComponentNode(child.getAsJsonObject(), context));
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
            for (JsonElement child : slotObject.getAsJsonArray(slotName)) {
                components.add(buildComponentNode(child.getAsJsonObject(), context));
            }
            slots.put(slotName, List.copyOf(components));
        }
        return slots;
    }

    private SlateComponentRegistry createDefaultRegistry(SlateComponentRegistry extraRegistry) {
        SlateComponentRegistry defaults = new SlateComponentRegistry()
            .register("OverlayRoot", (node, children, namedSlots, context) -> new OverlayRoot(children, resolveStyle(node, context)))
            .register("Box", (node, children, namedSlots, context) -> new Box(children, resolveStyle(node, context)))
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
                ? new top.huliawsl.slateui.api.component.Button(resolveString(node, "label", context), prop(node, "command", "screen.close"), resolveStyle(node, context))
                : new top.huliawsl.slateui.api.component.Button(children, prop(node, "command", "screen.close"), resolveStyle(node, context)))
            .register("Input", (node, children, namedSlots, context) -> new Input(
                context.provider(),
                prop(node, "placeholder", ""),
                ignored -> resolveString(node, "value", context),
                prop(node, "onChange", null),
                createInputHandler(node, context),
                resolveStyle(node, context)
            ))
            .register("ScrollView", (node, children, namedSlots, context) -> new ScrollView(singleChild(children), resolveStyle(node, context)))
            .register("Image", (node, children, namedSlots, context) -> new Image(prop(node, "resource", "minecraft:textures/gui/options_background.png"), resolveStyle(node, context)))
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

    private static InputValueHandler createInputHandler(JsonObject node, RuntimeBuildContext context) {
        String binding = binding(node, "value");
        if (binding == null || !(context.provider() instanceof MutableStateProvider mutableProvider)) {
            return null;
        }
        String path = BindingParser.normalize(binding);
        return (interactionContext, value) -> mutableProvider.set(path, value);
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
            return BindingEvaluator.evaluate(binding, context.provider());
        }
        return prop(node, name, "");
    }

    static String resolveString(JsonObject node, String name, RuntimeBuildContext context) {
        Object value = resolveObject(node, name, context);
        return value == null ? "" : String.valueOf(value);
    }

    static String binding(JsonObject node, String name) {
        JsonObject bindings = node.getAsJsonObject("bindings");
        return bindings != null && bindings.has(name) ? bindings.get(name).getAsString() : null;
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
        String className = props != null && props.has("class") ? props.get("class").getAsString() : null;
        if (className != null && context.scopedStyle().has(className)) {
            applyStyleObject(builder, context.scopedStyle().getAsJsonObject(className));
        }
        if (props != null) {
            JsonObject inline = new JsonObject();
            for (String key : props.keySet()) {
                if (key.startsWith("style-")) {
                    inline.add(key.substring("style-".length()), props.get(key));
                }
            }
            applyStyleObject(builder, inline);
        }
        return builder.build();
    }

    private static void applyStyleObject(SlateStyle.Builder builder, JsonObject styleObject) {
        for (String key : styleObject.keySet()) {
            String value = styleObject.get(key).getAsString();
            switch (key) {
                case "width" -> builder.width(Integer.parseInt(value));
                case "height" -> builder.height(Integer.parseInt(value));
                case "gap" -> builder.gap(Integer.parseInt(value));
                case "padding" -> builder.padding(Insets.all(Integer.parseInt(value)));
                case "backgroundToken", "background-token" -> builder.backgroundToken(value);
                case "borderColorToken", "border-color-token" -> builder.borderColorToken(value);
                case "borderThickness", "border-thickness" -> builder.border(new SlateBorder(0xFF334155, Integer.parseInt(value)));
                case "clipContent", "clip-content" -> builder.clipContent(Boolean.parseBoolean(value));
                default -> {
                }
            }
        }
    }

    public record RuntimeBuildContext(String resourcePath, StateProvider provider, Theme theme, JsonObject scopedStyle) {

        public RuntimeBuildContext {
            Objects.requireNonNull(resourcePath, "resourcePath");
            Objects.requireNonNull(provider, "provider");
            Objects.requireNonNull(theme, "theme");
            scopedStyle = scopedStyle == null ? new JsonObject() : scopedStyle;
        }

        public RuntimeBuildContext withProvider(StateProvider nextProvider) {
            return new RuntimeBuildContext(resourcePath, nextProvider, theme, scopedStyle);
        }
    }
}
