package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;

public final class VirtualList extends SlateList {

    public VirtualList(List<SlateComponent> visibleChildren, SlateStyle style) {
        super(visibleChildren, style);
    }
}
