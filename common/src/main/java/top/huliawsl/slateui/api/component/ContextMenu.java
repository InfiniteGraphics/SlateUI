package top.huliawsl.slateui.api.component;

import java.util.ArrayList;
import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;

public final class ContextMenu extends Popup {

    public record MenuItem(String label, String command) {}

    public ContextMenu(SlateComponent anchor, List<MenuItem> items, boolean open, SlateStyle style) {
        super(anchor, new SlateList(createItems(items), SlateStyle.EMPTY), () -> open, style);
    }

    private static List<SlateComponent> createItems(List<MenuItem> items) {
        List<SlateComponent> children = new ArrayList<>();
        for (MenuItem item : items == null ? List.<MenuItem>of() : items) {
            children.add(new Button(item.label(), item.command(), SlateStyle.EMPTY));
        }
        return children;
    }
}
