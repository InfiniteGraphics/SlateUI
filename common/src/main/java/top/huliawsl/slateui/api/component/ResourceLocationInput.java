package top.huliawsl.slateui.api.component;

import top.huliawsl.slateui.api.SlateStyle;

public final class ResourceLocationInput extends Input {

    public ResourceLocationInput(String initialValue, String changeCommand, SlateStyle style) {
        super("namespace:path", initialValue, changeCommand, style);
        validator(value -> value == null || value.isBlank() || value.matches("[a-z0-9_.-]+:[a-z0-9_./-]+") ? "" : "Expected namespace:path");
    }
}
