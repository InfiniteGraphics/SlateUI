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
        int gap = resolveGap(context.theme());
        for (int index = 0; index < children.size(); index++) {
            Size childSize = measureChild(context, children.get(index), contentAvailable);
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
        int gap = resolveGap(context.theme());
        int cursorX = startX(contentRect, gap);
        int cursorY = startY(contentRect, gap);
        for (SlateComponent child : children) {
            Size childSize = clampToContent(child.layoutNode().measuredSize(), contentRect);
            Rect childRect;
            if (direction == StackDirection.ROW) {
                int childWidth = childSize.width();
                int childHeight = style().verticalAlign() == VerticalAlign.STRETCH ? contentRect.height() : childSize.height();
                childHeight = Math.min(childHeight, contentRect.height());
                int childY = crossAxisY(contentRect, childHeight);
                childRect = new Rect(cursorX, childY, childWidth, childHeight);
                cursorX += childWidth + gap;
            } else {
                int childWidth = style().horizontalAlign() == HorizontalAlign.STRETCH ? contentRect.width() : childSize.width();
                childWidth = Math.min(childWidth, contentRect.width());
                int childHeight = childSize.height();
                int childX = crossAxisX(contentRect, childWidth);
                childRect = new Rect(childX, cursorY, childWidth, childHeight);
                cursorY += childHeight + gap;
            }
            layoutChild(context, child, childRect);
        }
    }

    @Override
    public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
        emitBoxChrome(context, commands);
        Rect contentRect = contentRect(bounds());
        pushClip(context, commands, contentRect);
        for (SlateComponent child : children) {
            collectChild(context, commands, child);
        }
        popClip(commands);
    }

    private int startX(Rect contentRect, int gap) {
        if (direction != StackDirection.ROW) {
            return contentRect.x();
        }
        int contentWidth = mainAxisSize(gap);
        if (contentWidth >= contentRect.width()) {
            return contentRect.x();
        }
        return switch (style().horizontalAlign()) {
            case CENTER -> contentRect.x() + (contentRect.width() - contentWidth) / 2;
            case END -> contentRect.right() - contentWidth;
            default -> contentRect.x();
        };
    }

    private int startY(Rect contentRect, int gap) {
        if (direction != StackDirection.COLUMN) {
            return contentRect.y();
        }
        int contentHeight = mainAxisSize(gap);
        if (contentHeight >= contentRect.height()) {
            return contentRect.y();
        }
        return switch (style().verticalAlign()) {
            case CENTER -> contentRect.y() + (contentRect.height() - contentHeight) / 2;
            case END -> contentRect.bottom() - contentHeight;
            default -> contentRect.y();
        };
    }

    private int crossAxisX(Rect contentRect, int childWidth) {
        return switch (style().horizontalAlign()) {
            case CENTER -> contentRect.x() + Math.max(0, (contentRect.width() - childWidth) / 2);
            case END -> contentRect.right() - childWidth;
            default -> contentRect.x();
        };
    }

    private int crossAxisY(Rect contentRect, int childHeight) {
        return switch (style().verticalAlign()) {
            case CENTER -> contentRect.y() + Math.max(0, (contentRect.height() - childHeight) / 2);
            case END -> contentRect.bottom() - childHeight;
            default -> contentRect.y();
        };
    }

    private int mainAxisSize(int gap) {
        int total = 0;
        for (int index = 0; index < children.size(); index++) {
            Size size = children.get(index).layoutNode().measuredSize();
            total += direction == StackDirection.ROW ? size.width() : size.height();
            if (index < children.size() - 1) {
                total += gap;
            }
        }
        return total;
    }
}
