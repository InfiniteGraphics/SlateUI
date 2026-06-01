package top.huliawsl.slateui.api.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;

public final class TreeView extends Stack {

    public record Node(String label, List<Node> children, boolean expanded) {}

    public TreeView(List<Node> nodes, SlateStyle style) {
        this(nodes, null, style);
    }

    public TreeView(List<Node> nodes, String toggleCommand, SlateStyle style) {
        super(StackDirection.COLUMN, flatten(nodes, 0, "", toggleCommand), style);
    }

    private static List<SlateComponent> flatten(List<Node> nodes, int depth, String parentPath, String toggleCommand) {
        List<SlateComponent> children = new ArrayList<>();
        List<Node> safeNodes = nodes == null ? List.of() : nodes;
        for (int index = 0; index < safeNodes.size(); index++) {
            Node node = safeNodes.get(index);
            String path = parentPath.isBlank() ? String.valueOf(index) : parentPath + "." + index;
            String label = "  ".repeat(depth) + (node.expanded() ? "- " : "+ ") + node.label();
            if (toggleCommand == null || toggleCommand.isBlank()) {
                children.add(new Text(label));
            } else {
                children.add(new Button(
                    label,
                    toggleCommand,
                    Map.of("path", path, "label", node.label(), "expanded", node.expanded(), "nextExpanded", !node.expanded(), "depth", depth),
                    SlateStyle.EMPTY
                ).componentKey(path));
            }
            if (node.expanded()) {
                children.addAll(flatten(node.children(), depth + 1, path, toggleCommand));
            }
        }
        return children;
    }
}
