package top.huliawsl.slateui.api;

import java.util.List;
import top.huliawsl.slateui.layout.Insets;
import top.huliawsl.slateui.layout.LayoutNode;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawBorderCommand;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.DrawDebugRectCommand;
import top.huliawsl.slateui.render.DrawRectCommand;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public abstract class SlateComponent {

    private final SlateStyle style;
    private final LayoutNode layoutNode;
    private Rect bounds = Rect.ZERO;

    protected SlateComponent() {
        this(SlateStyle.EMPTY);
    }

    protected SlateComponent(SlateStyle style) {
        this.style = style == null ? SlateStyle.EMPTY : style;
        this.layoutNode = new LayoutNode(debugName());
    }

    public final SlateStyle style() {
        return style;
    }

    public final LayoutNode layoutNode() {
        return layoutNode;
    }

    public final Rect bounds() {
        return bounds;
    }

    protected final void setBounds(Rect bounds) {
        this.bounds = bounds;
        this.layoutNode.setBounds(bounds);
    }

    protected final void setMeasuredSize(Size measuredSize) {
        this.layoutNode.setMeasuredSize(measuredSize);
    }

    protected final void resetLayoutChildren() {
        List.copyOf(layoutNode.children()).forEach(child -> {
        });
    }

    public String debugName() {
        return getClass().getSimpleName();
    }

    public List<SlateComponent> children() {
        return List.of();
    }

    public abstract Size measure(SlateLayoutContext context, Size available);

    public abstract void layout(SlateLayoutContext context, Rect bounds);

    public abstract void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands);

    public boolean mouseClicked(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        List<SlateComponent> children = children();
        for (int index = children.size() - 1; index >= 0; index--) {
            if (children.get(index).mouseClicked(context, mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    protected final Size applyStyleSize(Size base) {
        int width = style.width() != null ? style.width() : base.width();
        int height = style.height() != null ? style.height() : base.height();
        return new Size(width, height);
    }

    protected final Size addInsets(Size size, Insets insets) {
        return size.expand(insets.horizontal(), insets.vertical());
    }

    protected final Rect contentRect(Rect bounds) {
        return bounds.inset(style.padding());
    }

    protected final Size contentAvailable(Size available) {
        return new Size(
            Math.max(0, available.width() - style.padding().horizontal()),
            Math.max(0, available.height() - style.padding().vertical())
        );
    }

    protected final Rect alignChild(Rect container, Size childSize) {
        int x = switch (style.horizontalAlign()) {
            case CENTER -> container.x() + Math.max(0, (container.width() - childSize.width()) / 2);
            case END -> container.right() - childSize.width();
            default -> container.x();
        };
        int y = switch (style.verticalAlign()) {
            case CENTER -> container.y() + Math.max(0, (container.height() - childSize.height()) / 2);
            case END -> container.bottom() - childSize.height();
            default -> container.y();
        };
        int width = style.horizontalAlign() == HorizontalAlign.STRETCH ? container.width() : Math.min(childSize.width(), container.width());
        int height = style.verticalAlign() == VerticalAlign.STRETCH ? container.height() : Math.min(childSize.height(), container.height());
        return new Rect(x, y, Math.max(0, width), Math.max(0, height));
    }

    protected final void emitBoxChrome(SlateRenderContext context, List<DrawCommand> commands) {
        if (style.backgroundColor() != null) {
            commands.add(new DrawRectCommand(bounds, style.backgroundColor()));
        }
        if (style.border().thickness() > 0) {
            commands.add(new DrawBorderCommand(bounds, style.border().color(), style.border().thickness()));
        }
        if (context.debugEnabled()) {
            commands.add(new DrawDebugRectCommand(bounds, 0x66FF00FF));
        }
    }

    public final String dumpComponentTree() {
        StringBuilder builder = new StringBuilder();
        appendComponentTree(builder, 0);
        return builder.toString();
    }

    private void appendComponentTree(StringBuilder builder, int depth) {
        builder.append("  ".repeat(depth))
            .append(debugName())
            .append(" rect=")
            .append(bounds)
            .append('\n');
        for (SlateComponent child : children()) {
            child.appendComponentTree(builder, depth + 1);
        }
    }
}
