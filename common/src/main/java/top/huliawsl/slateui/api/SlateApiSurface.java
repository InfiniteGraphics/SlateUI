package top.huliawsl.slateui.api;

import java.util.List;
import java.util.Set;

@SlatePublicApi(since = "1.0")
public final class SlateApiSurface {

    public static final int AUTHORING_IR_VERSION = 1;
    public static final int COMMAND_MODEL_VERSION = 1;

    private SlateApiSurface() {
    }

    public static Set<String> publicPackages() {
        return Set.of(
            "top.huliawsl.slateui.api",
            "top.huliawsl.slateui.api.component",
            "top.huliawsl.slateui.api.overlay",
            "top.huliawsl.slateui.binding",
            "top.huliawsl.slateui.command",
            "top.huliawsl.slateui.layout",
            "top.huliawsl.slateui.render",
            "top.huliawsl.slateui.runtime"
        );
    }

    public static Set<String> internalPackages() {
        return Set.of(
            "top.huliawsl.slateui.mixin",
            "top.huliawsl.slateui.demo"
        );
    }

    public static Set<String> experimentalPackages() {
        return Set.of(
            "top.huliawsl.slateui.animation",
            "top.huliawsl.slateui.api.container",
            "top.huliawsl.slateui.ecosystem",
            "top.huliawsl.slateui.editor",
            "top.huliawsl.slateui.hud",
            "top.huliawsl.slateui.scripting",
            "top.huliawsl.slateui.server",
            "top.huliawsl.slateui.style",
            "top.huliawsl.slateui.world"
        );
    }

    public static List<String> stableComponents() {
        return List.of(
            "OverlayRoot",
            "Box",
            "Stack",
            "Panel",
            "Text",
            "Button",
            "Input",
            "Toggle",
            "Image",
            "ScrollView",
            "List",
            "SelectableList",
            "SelectableCardGrid",
            "ParameterForm",
            "ResizableSplitPane",
            "Canvas"
        );
    }

    public static Set<String> frozenThemeTokens() {
        return Set.of(
            "color.surface",
            "color.panel",
            "color.primary",
            "color.primaryHover",
            "color.primaryActive",
            "color.text",
            "color.muted",
            "color.border",
            "spacing.xs",
            "spacing.sm",
            "spacing.md",
            "spacing.lg",
            "radius.sm",
            "radius.md",
            "radius.lg"
        );
    }

    public static List<String> stableScope() {
        return List.of(
            "Screen runtime",
            "Core components",
            "Style/theme",
            "State/binding",
            "Commands",
            "Diagnostics",
            ".slate basic authoring",
            "Java API",
            "Texture/text rendering",
            "Input/focus",
            "Editor surface components",
            "Canvas/raw draw",
            "Overlay screens",
            "Loader support for selected MC versions"
        );
    }

    public static List<String> experimentalScope() {
        return List.of(
            "Container UI",
            "HUD",
            "World-space UI",
            "Visual editor",
            "Scripting integrations",
            "Advanced animation"
        );
    }
}
