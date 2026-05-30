package top.huliawsl.slateui.api.component;

import java.util.ArrayList;
import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;

public final class TreeView extends Stack {

    public record Node(String label, List<Node> children, boolean expanded) {}

    public TreeView(List<Node> nodes, SlateStyle style) {
        super(StackDirection.COLUMN, flatten(nodes, 0), style);
    }

    private static List<SlateComponent> flatten(List<Node> nodes, int depth) {
        List<SlateComponent> children = new ArrayList<>();
        for (Node node : nodes == null ? List.<Node>of() : nodes) {
            children.add(new Text("  ".repeat(depth) + (node.expanded() ? "- " : "+ ") + node.label()));
            if (node.expanded()) {
                children.addAll(flatten(node.children(), depth + 1));
            }
        }
        return children;
    }
}
