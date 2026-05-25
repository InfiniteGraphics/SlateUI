package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.HorizontalAlign;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;
import top.huliawsl.slateui.api.VerticalAlign;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public class Stack extends SlateComponent {

    private final StackDirection direction;
    private final List<SlateComponent> children;

    public Stack(StackDirection direction, List<SlateComponent> children, SlateStyle style) {
        super(style);
        this.direction = direction;
        this.children = List.copyOf(children);
    }

    @Override
    public List<SlateComponent> children() {
        return children;
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        Size contentAvailable = contentAvailable(available);
        int width = 0;
        int height = 0;
        int gap = style().gap();
        for (int index = 0; index < children.size(); index++) {
            Size childSize = children.get(index).measure(context, contentAvailable);
            if (direction == StackDirection.ROW) {
                width += childSize.width();
                height = Math.max(height, childSize.height());
            } else {
                width = Math.max(width, childSize.width());
                height += childSize.height();
            }
            if (index < children.size() - 1) {
                if (direction == StackDirection.ROW) {
                    width += gap;
                } else {
                    height += gap;
                }
            }
        }
        Size measured = applyStyleSize(addInsets(new Size(width, height), style().padding()));
        setMeasuredSize(measured);
        return measured;
    }

    @Override
    public void layout(SlateLayoutContext context, Rect bounds) {
        setBounds(bounds);
        Rect contentRect = contentRect(bounds);
        int cursorX = contentRect.x();
        int cursorY = contentRect.y();
        for (SlateComponent child : children) {
            Size childSize = child.layoutNode().measuredSize();
            Rect childRect;
            if (direction == StackDirection.ROW) {
                int childHeight = style().verticalAlign() == VerticalAlign.STRETCH ? contentRect.height() : Math.min(childSize.height(), contentRect.height());
                int childY = switch (style().verticalAlign()) {
                    case CENTER -> contentRect.y() + Math.max(0, (contentRect.height() - childHeight) / 2);
                    case END -> contentRect.bottom() - childHeight;
                    default -> contentRect.y();
                };
                childRect = new Rect(cursorX, childY, childSize.width(), childHeight);
                cursorX += childSize.width() + style().gap();
            } else {
                int childWidth = style().horizontalAlign() == HorizontalAlign.STRETCH ? contentRect.width() : Math.min(childSize.width(), contentRect.width());
                int childX = switch (style().horizontalAlign()) {
                    case CENTER -> contentRect.x() + Math.max(0, (contentRect.width() - childWidth) / 2);
                    case END -> contentRect.right() - childWidth;
                    default -> contentRect.x();
                };
                childRect = new Rect(childX, cursorY, childWidth, childSize.height());
                cursorY += childSize.height() + style().gap();
            }
            child.layout(context, childRect);
        }
    }

    @Override
    public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
        emitBoxChrome(context, commands);
        Rect contentRect = contentRect(bounds());
        pushClip(commands, contentRect);
        for (SlateComponent child : children) {
            child.collectDrawCommands(context, commands);
        }
        popClip(commands);
    }
}
