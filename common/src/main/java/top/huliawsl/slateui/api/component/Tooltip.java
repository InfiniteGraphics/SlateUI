package top.huliawsl.slateui.api.component;

import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;

public final class Tooltip extends Popup {

    public Tooltip(SlateComponent anchor, SlateComponent tooltip, SlateStyle style) {
        super(anchor, tooltip, anchor::isHovered, style);
    }
}
