package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;

public final class SplitPane extends Stack {

    public SplitPane(SlateComponent first, SlateComponent second, boolean vertical, SlateStyle style) {
        super(vertical ? StackDirection.COLUMN : StackDirection.ROW, List.of(first, second), style);
    }
}
