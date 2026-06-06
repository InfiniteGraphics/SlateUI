package top.huliawsl.slateui.api.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import top.huliawsl.slateui.api.HorizontalAlign;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;
import top.huliawsl.slateui.layout.Insets;

public final class ContextMenu extends Popup {

    public record MenuItem(
        String label,
        String command,
        String shortcut,
        boolean enabled,
        boolean checked,
        List<MenuItem> children,
        Map<String, Object> payload
    ) {
        public MenuItem(String label, String command) {
            this(label, command, "", true, false, List.of(), Map.of());
        }

        public MenuItem(String label, String command, String shortcut) {
            this(label, command, shortcut, true, false, List.of(), Map.of());
        }

        public MenuItem {
            label = label == null ? "" : label;
            command = command == null ? "" : command;
            shortcut = shortcut == null ? "" : shortcut;
            children = children == null ? List.of() : List.copyOf(children);
            payload = payload == null ? Map.of() : Map.copyOf(payload);
        }

        public static MenuItem separator() {
            return new MenuItem("---", "", "", false, false, List.of(), Map.of());
        }

        public boolean separatorItem() {
            return "---".equals(label);
        }
    }

    public ContextMenu(SlateComponent anchor, List<MenuItem> items, boolean open, SlateStyle style) {
        super(anchor, new SlateList(createItems(items), SlateStyle.EMPTY), () -> open, style);
    }

    private static List<SlateComponent> createItems(List<MenuItem> items) {
        List<SlateComponent> children = new ArrayList<>();
        for (MenuItem item : items == null ? List.<MenuItem>of() : items) {
            if (item.separatorItem()) {
                children.add(new Divider(false));
                continue;
            }
            String marker = item.checked() ? "[x] " : "";
            String childMarker = item.children().isEmpty() ? "" : " >";
            String label = marker + item.label() + childMarker + (item.shortcut().isBlank() ? "" : "    " + item.shortcut());
            SlateStyle buttonStyle = item.enabled()
                ? SlateStyle.EMPTY
                : SlateStyle.builder().disabled(true).backgroundColor(0x00000000).textColor(0xFF64748B).build();
            children.add(new Button(
                List.of(new Text(label, SlateStyle.builder().horizontalAlign(HorizontalAlign.START).build())),
                item.command(),
                item.payload(),
                buttonStyle
            ));
            if (!item.children().isEmpty()) {
                children.add(new Stack(StackDirection.COLUMN, createItems(item.children()), SlateStyle.builder().padding(new Insets(12, 0, 0, 0)).gap(3).build()));
            }
        }
        return children;
    }
}
