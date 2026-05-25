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
import top.huliawsl.slateui.render.PopClipCommand;
import top.huliawsl.slateui.render.PushClipCommand;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public abstract class SlateComponent {

    private final SlateStyle style;
    private final LayoutNode layoutNode;
    private Rect bounds = Rect.ZERO;
    private boolean hovered;
    private boolean pressed;
    private boolean focused;

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

    public final boolean isHovered() {
        return hovered;
    }

    public final boolean isPressed() {
        return pressed;
    }

    public final boolean isFocused() {
        return focused;
    }

    protected final void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    protected final void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    public final void setFocused(boolean focused) {
        if (this.focused == focused) {
            return;
        }
        this.focused = focused;
        onFocusChanged(focused);
    }

    protected void onFocusChanged(boolean focused) {
    }

    public boolean focusable() {
        return false;
    }

    protected final void setBounds(Rect bounds) {
        this.bounds = bounds;
        this.layoutNode.setBounds(bounds);
    }

    protected final void setMeasuredSize(Size measuredSize) {
        this.layoutNode.setMeasuredSize(measuredSize);
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
        if (style.disabled()) {
            return false;
        }
        if (!bounds.contains(mouseX, mouseY)) {
            return false;
        }
        boolean changed = !pressed;
        setPressed(true);
        if (changed) {
            context.screen().requestRebuild("press:" + debugName());
        }
        if (focusable()) {
            context.requestFocus(this);
        }
        List<SlateComponent> children = children();
        for (int index = children.size() - 1; index >= 0; index--) {
            if (children.get(index).mouseClicked(context, mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    public boolean mouseReleased(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        boolean wasPressed = pressed;
        setPressed(false);
        if (wasPressed) {
            context.screen().requestRebuild("release:" + debugName());
        }
        List<SlateComponent> children = children();
        for (int index = children.size() - 1; index >= 0; index--) {
            if (children.get(index).mouseReleased(context, mouseX, mouseY, button)) {
                return true;
            }
        }
        return wasPressed && bounds.contains(mouseX, mouseY);
    }

    public boolean mouseMoved(SlateInteractionContext context, double mouseX, double mouseY) {
        boolean childHovered = false;
        List<SlateComponent> children = children();
        for (int index = children.size() - 1; index >= 0; index--) {
            childHovered |= children.get(index).mouseMoved(context, mouseX, mouseY);
        }
        boolean localHovered = bounds.contains(mouseX, mouseY);
        boolean changed = hovered != localHovered;
        setHovered(localHovered);
        if (changed) {
            context.screen().requestRebuild("hover:" + debugName());
        }
        return localHovered || childHovered;
    }

    public boolean mouseScrolled(SlateInteractionContext context, double mouseX, double mouseY, double delta) {
        List<SlateComponent> children = children();
        for (int index = children.size() - 1; index >= 0; index--) {
            if (children.get(index).mouseScrolled(context, mouseX, mouseY, delta)) {
                return true;
            }
        }
        return false;
    }

    public boolean keyPressed(SlateInteractionContext context, int keyCode, int scanCode, int modifiers) {
        List<SlateComponent> children = children();
        for (int index = children.size() - 1; index >= 0; index--) {
            if (children.get(index).keyPressed(context, keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    public boolean charTyped(SlateInteractionContext context, char codePoint, int modifiers) {
        List<SlateComponent> children = children();
        for (int index = children.size() - 1; index >= 0; index--) {
            if (children.get(index).charTyped(context, codePoint, modifiers)) {
                return true;
            }
        }
        return false;
    }

    protected final Size applyStyleSize(Size base) {
        int width = style.width() != null ? style.width() : base.width();
        int height = style.height() != null ? style.height() : base.height();
        int minWidth = style.minWidth() != null ? style.minWidth() : 0;
        int minHeight = style.minHeight() != null ? style.minHeight() : 0;
        int maxWidth = style.maxWidth() != null ? style.maxWidth() : Integer.MAX_VALUE;
        int maxHeight = style.maxHeight() != null ? style.maxHeight() : Integer.MAX_VALUE;
        return new Size(width, height).clamp(new Size(minWidth, minHeight), new Size(maxWidth, maxHeight));
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

    protected final int resolveGap(Theme theme) {
        return theme.resolveSpacing(style.gap(), style.gapToken(), style.gap());
    }

    protected final int resolveTextColor(Theme theme) {
        return theme.resolveColor(style.textColor(), style.textColorToken(), 0xFFFFFFFF);
    }

    protected final void pushClip(List<DrawCommand> commands, Rect rect) {
        if (style.clipContent()) {
            commands.add(new PushClipCommand(rect));
        }
    }

    protected final void popClip(List<DrawCommand> commands) {
        if (style.clipContent()) {
            commands.add(new PopClipCommand());
        }
    }

    protected final void emitBoxChrome(SlateRenderContext context, List<DrawCommand> commands) {
        Theme theme = context.theme();
        Integer backgroundOverride = pressed ? style.activeBackgroundColor() : hovered ? style.hoverBackgroundColor() : style.backgroundColor();
        String backgroundToken = pressed ? style.activeBackgroundToken() : hovered ? style.hoverBackgroundToken() : style.backgroundToken();
        int backgroundColor = theme.resolveColor(backgroundOverride, backgroundToken, Integer.MIN_VALUE);
        if (backgroundColor != Integer.MIN_VALUE) {
            commands.add(new DrawRectCommand(bounds, backgroundColor));
        }
        SlateBorder border = focused && style.focusBorder().thickness() > 0 ? style.focusBorder() : style.border();
        String borderToken = focused && style.focusBorder().thickness() > 0 ? style.focusBorderColorToken() : style.borderColorToken();
        if (border.thickness() > 0) {
            commands.add(new DrawBorderCommand(bounds, theme.resolveColor(border.color(), borderToken, border.color()), border.thickness()));
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

    public final String dumpLayoutTree() {
        refreshLayoutNodeTree();
        return layoutNode.dump();
    }

    private void appendComponentTree(StringBuilder builder, int depth) {
        builder.append("  ".repeat(depth))
            .append(debugName())
            .append(" rect=")
            .append(bounds)
            .append(" hovered=")
            .append(hovered)
            .append(" pressed=")
            .append(pressed)
            .append(" focused=")
            .append(focused)
            .append('\n');
        for (SlateComponent child : children()) {
            child.appendComponentTree(builder, depth + 1);
        }
    }

    private void refreshLayoutNodeTree() {
        layoutNode.clearChildren();
        for (SlateComponent child : children()) {
            child.refreshLayoutNodeTree();
            layoutNode.addChild(child.layoutNode());
        }
    }
}
