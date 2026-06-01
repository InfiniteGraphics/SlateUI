package top.huliawsl.slateui.api.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;

public final class Tabs extends Stack {

    public record Tab(String label, SlateComponent content) {}

    public Tabs(List<Tab> tabs, int selectedIndex, String selectCommand, SlateStyle style) {
        super(StackDirection.COLUMN, createChildren(tabs, selectedIndex, selectCommand), style);
    }

    private static List<SlateComponent> createChildren(List<Tab> tabs, int selectedIndex, String selectCommand) {
        List<Tab> safeTabs = tabs == null ? List.of() : tabs;
        List<SlateComponent> headers = new ArrayList<>();
        for (int index = 0; index < safeTabs.size(); index++) {
            headers.add(new Button(
                (index == selectedIndex ? "* " : "") + safeTabs.get(index).label(),
                selectCommand,
                Map.of("selectedIndex", index, "selectedLabel", safeTabs.get(index).label()),
                SlateStyle.EMPTY
            ).componentKey(String.valueOf(index)));
        }
        List<SlateComponent> children = new ArrayList<>();
        children.add(new Toolbar(headers));
        if (!safeTabs.isEmpty()) {
            int index = Math.max(0, Math.min(safeTabs.size() - 1, selectedIndex));
            children.add(safeTabs.get(index).content());
        }
        return children;
    }
}
