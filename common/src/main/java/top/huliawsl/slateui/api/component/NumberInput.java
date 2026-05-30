package top.huliawsl.slateui.api.component;

import top.huliawsl.slateui.api.SlateStyle;

public final class NumberInput extends Input {

    public NumberInput(String placeholder, Number initialValue, String changeCommand, SlateStyle style) {
        super(placeholder, initialValue == null ? "" : String.valueOf(initialValue), changeCommand, style);
        validator(value -> {
            if (value == null || value.isBlank()) {
                return "";
            }
            try {
                Double.parseDouble(value);
                return "";
            } catch (NumberFormatException ignored) {
                return "Expected number";
            }
        });
    }
}
