package top.huliawsl.slateui.api.component;

import top.huliawsl.slateui.api.SlateStyle;

public final class Spinner extends Text {

    public Spinner() {
        this(SlateStyle.EMPTY);
    }

    public Spinner(SlateStyle style) {
        super("Loading...", style);
    }
}
