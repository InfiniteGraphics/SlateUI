package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;

public final class StickyHeader extends Stack {

    public StickyHeader(SlateComponent header, SlateComponent body, SlateStyle style) {
        super(StackDirection.COLUMN, List.of(header, body), style);
    }
}
