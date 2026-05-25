package top.huliawsl.slateui.layout;

import java.util.ArrayList;
import java.util.List;

public final class LayoutNode {

    private final String name;
    private final List<LayoutNode> children = new ArrayList<>();
    private Size measuredSize = Size.ZERO;
    private Rect bounds = Rect.ZERO;

    public LayoutNode(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public List<LayoutNode> children() {
        return List.copyOf(children);
    }

    public void clearChildren() {
        children.clear();
    }

    public void addChild(LayoutNode child) {
        children.add(child);
    }

    public Size measuredSize() {
        return measuredSize;
    }

    public void setMeasuredSize(Size measuredSize) {
        this.measuredSize = measuredSize;
    }

    public Rect bounds() {
        return bounds;
    }

    public void setBounds(Rect bounds) {
        this.bounds = bounds;
    }

    public String dump() {
        StringBuilder builder = new StringBuilder();
        appendDump(builder, 0);
        return builder.toString();
    }

    private void appendDump(StringBuilder builder, int depth) {
        builder.append("  ".repeat(depth))
            .append(name)
            .append(" rect=")
            .append(bounds)
            .append(" measured=")
            .append(measuredSize)
            .append('\n');
        for (LayoutNode child : children) {
            child.appendDump(builder, depth + 1);
        }
    }
}
