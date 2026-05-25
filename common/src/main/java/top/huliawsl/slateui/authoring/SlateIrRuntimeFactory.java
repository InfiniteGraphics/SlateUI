package top.huliawsl.slateui.authoring;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
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
import top.huliawsl.slateui.api.component.Image;
import top.huliawsl.slateui.api.component.Input;
import top.huliawsl.slateui.api.component.OverlayRoot;
import top.huliawsl.slateui.api.component.ScrollView;
import top.huliawsl.slateui.api.component.Stack;
import top.huliawsl.slateui.api.component.Text;
import top.huliawsl.slateui.binding.BindingEvaluator;
import top.huliawsl.slateui.command.SlateCommandRegistry;
import top.huliawsl.slateui.layout.Insets;

public final class SlateIrRuntimeFactory {

    private static final int SUPPORTED_SCHEMA = 1;

    private final SlateComponentRegistry registry;

    public SlateIrRuntimeFactory() {
        this.registry = createDefaultRegistry();
    }

    public SlateScreen createScreen(Component title, String resourcePath, SlateCommandRegistry commands, StateProvider provider, Theme theme, boolean debugEnabled) {
        JsonObject ir = SlateIrLoader.load(resourcePath);
        int schemaVersion = ir.get("schemaVersion").getAsInt();
        if (schemaVersion != SUPPORTED_SCHEMA) {
            throw new IllegalStateException("Unsupported Slate IR schema: " + schemaVersion);
        }
        RuntimeBuildContext context = new RuntimeBuildContext(resourcePath, provider == null ? StateProvider.EMPTY : provider, theme == null ? Theme.DEFAULT : theme, ir.getAsJsonObject("scopedStyle"));
        SlateComponent root = buildComponent(ir.getAsJsonObject("root"), context);
        return new SlateScreen(title, root, commands, provider, theme, debugEnabled);
    }

    private SlateComponent buildComponent(JsonObject node, RuntimeBuildContext context) {
        String type = node.get("componentType").getAsString();
        List<SlateComponent> children = new ArrayList<>();
        for (JsonElement child : node.getAsJsonArray("children")) {
            children.add(buildComponent(child.getAsJsonObject(), context));
        }
        return registry.require(type).create(node, children, context);
    }

    private SlateComponentRegistry createDefaultRegistry() {
        return new SlateComponentRegistry()
            .register("OverlayRoot", (node, children, context) -> new OverlayRoot(children, resolveStyle(node, context)))
            .register("Box", (node, children, context) -> new Box(children, resolveStyle(node, context)))
            .register("Stack", (node, children, context) -> new Stack(
                "row".equalsIgnoreCase(prop(node, "direction", "column")) ? StackDirection.ROW : StackDirection.COLUMN,
                children,
                resolveStyle(node, context)
            ))
            .register("Text", (node, children, context) -> new Text(
                ignored -> resolveString(node, "value", context),
                resolveStyle(node, context)
            ))
            .register("Button", (node, children, context) -> new top.huliawsl.slateui.api.component.Button(
                resolveString(node, "label", context),
                prop(node, "command", "screen.close"),
                resolveStyle(node, context)
            ))
            .register("Input", (node, children, context) -> new Input(
                prop(node, "placeholder", ""),
                ignored -> resolveString(node, "value", context),
                prop(node, "onChange", null),
                createInputHandler(node, context),
                resolveStyle(node, context)
            ))
            .register("ScrollView", (node, children, context) -> new ScrollView(singleChild(children), resolveStyle(node, context)))
            .register("Image", (node, children, context) -> new Image(prop(node, "resource", "minecraft:textures/gui/options_background.png"), resolveStyle(node, context)));
    }

    private static InputValueHandler createInputHandler(JsonObject node, RuntimeBuildContext context) {
        String binding = binding(node, "value");
        if (binding == null || !(context.provider() instanceof MutableStateProvider mutableProvider)) {
            return null;
        }
        String path = binding.substring(1, binding.length() - 1).trim();
        return (interactionContext, value) -> mutableProvider.set(path, value);
    }

    private static SlateComponent singleChild(List<SlateComponent> children) {
        if (children.isEmpty()) {
            return new Box(List.of(), SlateStyle.EMPTY);
        }
        if (children.size() == 1) {
            return children.get(0);
        }
        return new Stack(StackDirection.COLUMN, children, SlateStyle.builder().gap(8).build());
    }

    private static String resolveString(JsonObject node, String name, RuntimeBuildContext context) {
        String binding = binding(node, name);
        if (binding != null) {
            Object value = BindingEvaluator.evaluate(binding, context.provider());
            return value == null ? "" : String.valueOf(value);
        }
        return prop(node, name, "");
    }

    private static String binding(JsonObject node, String name) {
        JsonObject bindings = node.getAsJsonObject("bindings");
        return bindings != null && bindings.has(name) ? bindings.get(name).getAsString() : null;
    }

    private static String prop(JsonObject node, String name, String fallback) {
        JsonObject props = node.getAsJsonObject("props");
        return props != null && props.has(name) ? props.get(name).getAsString() : fallback;
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
    }
}