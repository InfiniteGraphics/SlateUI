package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;

public class ItemStackView extends Stack {

    public ItemStackView(String itemId, int count, SlateStyle style) {
        super(StackDirection.ROW, List.of(
            new ItemIcon(itemId, count, SlateStyle.EMPTY),
            new Text(itemId + " x" + count)
        ), style);
    }
}
