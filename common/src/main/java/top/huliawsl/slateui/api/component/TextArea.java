package top.huliawsl.slateui.api.component;

import top.huliawsl.slateui.api.SlateStyle;

public final class TextArea extends Input {

    public TextArea(String placeholder, String initialValue, String changeCommand, SlateStyle style) {
        super(placeholder, initialValue, changeCommand, SlateStyle.withDefaults(SlateStyle.builder().height(72).build(), style));
    }
}
