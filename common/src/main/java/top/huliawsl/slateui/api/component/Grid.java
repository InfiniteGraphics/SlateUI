package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public final class Grid extends SlateComponent {

    private final int columns;
    private final List<SlateComponent> children;
    private int cellWidth;
    private int cellHeight;

    public Grid(int columns, List<SlateComponent> children, SlateStyle style) {
        super(style);
        this.columns = Math.max(1, columns);
        this.children = List.copyOf(children == null ? List.of() : children);
    }

    @Override
    public List<SlateComponent> children() {
        return children;
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        int gap = resolveGap(context.theme());
        cellWidth = 0;
        cellHeight = 0;
        Size childAvailable = contentAvailable(available);
        for (SlateComponent child : children) {
            Size size = measureChild(context, child, childAvailable);
            cellWidth = Math.max(cellWidth, size.width());
            cellHeight = Math.max(cellHeight, size.height());
        }
        int rows = children.isEmpty() ? 0 : (int) Math.ceil(children.size() / (double) columns);
        Size measured = applyStyleSize(addInsets(new Size(columns * cellWidth + Math.max(0, columns - 1) * gap, rows * cellHeight + Math.max(0, rows - 1) * gap), style().padding()));
        setMeasuredSize(measured);
        return measured;
    }

    @Override
    public void layout(SlateLayoutContext context, Rect bounds) {
        setBounds(bounds);
        Rect content = contentRect(bounds);
        int gap = resolveGap(context.theme());
        for (int index = 0; index < children.size(); index++) {
            int row = index / columns;
            int column = index % columns;
            layoutChild(context, children.get(index), new Rect(content.x() + column * (cellWidth + gap), content.y() + row * (cellHeight + gap), cellWidth, cellHeight));
        }
    }

    @Override
    public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
        emitBoxChrome(context, commands);
        for (SlateComponent child : children) {
            collectChild(context, commands, child);
        }
    }
}
