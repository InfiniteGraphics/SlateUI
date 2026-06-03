package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.SlateBorder;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;
import top.huliawsl.slateui.layout.Insets;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.DrawRectCommand;
import top.huliawsl.slateui.runtime.InvalidationType;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public final class ResizableSplitPane extends SlateComponent {

    private static final SlateStyle DEFAULT_STYLE = SlateStyle.builder()
        .padding(Insets.ZERO)
        .clipContent(true)
        .build();

    private final SlateComponent first;
    private final SlateComponent second;
    private final StackDirection direction;
    private final int minFirstPixels;
    private final int minSecondPixels;
    private final int handleThickness;
    private final SplitResizeHandler resizeHandler;
    private float ratio;
    private Rect handleBounds = Rect.ZERO;
    private boolean resizing;

    public ResizableSplitPane(
        SlateComponent first,
        SlateComponent second,
        StackDirection direction,
        float ratio,
        int minFirstPixels,
        int minSecondPixels,
        int handleThickness,
        SplitResizeHandler resizeHandler,
        SlateStyle style
    ) {
        super(SlateStyle.withDefaults(DEFAULT_STYLE, style));
        this.first = first == null ? new Spacer(0, 0) : first;
        this.second = second == null ? new Spacer(0, 0) : second;
        this.direction = direction == null ? StackDirection.ROW : direction;
        this.ratio = clampRatio(ratio);
        this.minFirstPixels = Math.max(0, minFirstPixels);
        this.minSecondPixels = Math.max(0, minSecondPixels);
        this.handleThickness = Math.max(1, handleThickness);
        this.resizeHandler = resizeHandler;
    }

    public ResizableSplitPane(SlateComponent first, SlateComponent second, StackDirection direction, float ratio, SlateStyle style) {
        this(first, second, direction, ratio, 48, 48, 5, null, style);
    }

    public float ratio() {
        return ratio;
    }

    public Rect handleBounds() {
        return handleBounds;
    }

    @Override
    public List<SlateComponent> children() {
        return List.of(first, second);
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        Size contentAvailable = contentAvailable(available);
        int main = direction == StackDirection.ROW ? contentAvailable.width() : contentAvailable.height();
        int cross = direction == StackDirection.ROW ? contentAvailable.height() : contentAvailable.width();
        int availableMain = Math.max(0, main - handleThickness);
        int firstMain = resolveFirstMain(availableMain);
        int secondMain = Math.max(0, availableMain - firstMain);
        Size firstAvailable = direction == StackDirection.ROW ? new Size(firstMain, cross) : new Size(cross, firstMain);
        Size secondAvailable = direction == StackDirection.ROW ? new Size(secondMain, cross) : new Size(cross, secondMain);
        measureChild(context, first, firstAvailable);
        measureChild(context, second, secondAvailable);
        Size measured = applyStyleSize(available);
        setMeasuredSize(measured);
        return measured;
    }

    @Override
    public void layout(SlateLayoutContext context, Rect bounds) {
        setBounds(bounds);
        Rect content = contentRect(bounds);
        if (direction == StackDirection.ROW) {
            int availableMain = Math.max(0, content.width() - handleThickness);
            int firstWidth = resolveFirstMain(availableMain);
            int secondWidth = Math.max(0, availableMain - firstWidth);
            layoutChild(context, first, new Rect(content.x(), content.y(), firstWidth, content.height()));
            handleBounds = new Rect(content.x() + firstWidth, content.y(), handleThickness, content.height());
            layoutChild(context, second, new Rect(handleBounds.right(), content.y(), secondWidth, content.height()));
        } else {
            int availableMain = Math.max(0, content.height() - handleThickness);
            int firstHeight = resolveFirstMain(availableMain);
            int secondHeight = Math.max(0, availableMain - firstHeight);
            layoutChild(context, first, new Rect(content.x(), content.y(), content.width(), firstHeight));
            handleBounds = new Rect(content.x(), content.y() + firstHeight, content.width(), handleThickness);
            layoutChild(context, second, new Rect(content.x(), handleBounds.bottom(), content.width(), secondHeight));
        }
    }

    @Override
    public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
        emitBoxChrome(context, commands);
        pushClip(context, commands, contentRect(bounds()));
        collectChild(context, commands, first);
        int handleColor = resizing || isHovered() ? 0xAA94A3B8 : 0x665E6B7E;
        commands.add(new DrawRectCommand(handleBounds, handleColor, 1));
        collectChild(context, commands, second);
        popClip(commands);
    }

    @Override
    public boolean mouseClicked(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        if (style().disabled() || !bounds().contains(mouseX, mouseY)) {
            return false;
        }
        if (handleBounds.contains(mouseX, mouseY)) {
            resizing = true;
            setPressed(true);
            context.host().requestPointerCapture("split-pane-resize");
            context.requestInvalidation(InvalidationType.INTERACTION, "split-pane-resize-start");
            return true;
        }
        if (second.mouseClicked(context, mouseX, mouseY, button)) {
            return true;
        }
        return first.mouseClicked(context, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        if (resizing) {
            resizing = false;
            setPressed(false);
            context.host().releasePointerCapture("split-pane-resize");
            emitResize(context);
            context.requestInvalidation(InvalidationType.LAYOUT, "split-pane-resize-end");
            return true;
        }
        return second.mouseReleased(context, mouseX, mouseY, button) || first.mouseReleased(context, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseMoved(SlateInteractionContext context, double mouseX, double mouseY) {
        boolean hover = handleBounds.contains(mouseX, mouseY);
        if (isHovered() != hover) {
            setHovered(hover);
            context.requestInvalidation(InvalidationType.INTERACTION, "split-pane-hover");
        }
        return hover || second.mouseMoved(context, mouseX, mouseY) || first.mouseMoved(context, mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(SlateInteractionContext context, double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!resizing) {
            return second.mouseDragged(context, mouseX, mouseY, button, dragX, dragY) || first.mouseDragged(context, mouseX, mouseY, button, dragX, dragY);
        }
        Rect content = contentRect(bounds());
        int availableMain = direction == StackDirection.ROW
            ? Math.max(0, content.width() - handleThickness)
            : Math.max(0, content.height() - handleThickness);
        int requestedFirst = direction == StackDirection.ROW
            ? (int) Math.round(mouseX - content.x() - handleThickness / 2.0D)
            : (int) Math.round(mouseY - content.y() - handleThickness / 2.0D);
        int firstMain = clampFirstMain(requestedFirst, availableMain);
        ratio = availableMain <= 0 ? ratio : firstMain / (float) availableMain;
        emitResize(context);
        context.requestInvalidation(InvalidationType.LAYOUT, "split-pane-resize-drag");
        return true;
    }

    private int resolveFirstMain(int availableMain) {
        return clampFirstMain(Math.round(availableMain * ratio), availableMain);
    }

    private int clampFirstMain(int requested, int availableMain) {
        if (availableMain <= 0) {
            return 0;
        }
        int minSecond = Math.min(minSecondPixels, availableMain);
        int minFirst = Math.min(minFirstPixels, Math.max(0, availableMain - minSecond));
        int maxFirst = Math.max(minFirst, availableMain - minSecond);
        return Math.max(minFirst, Math.min(requested, maxFirst));
    }

    private void emitResize(SlateInteractionContext context) {
        if (resizeHandler == null) {
            return;
        }
        Rect content = contentRect(bounds());
        int availableMain = direction == StackDirection.ROW
            ? Math.max(0, content.width() - handleThickness)
            : Math.max(0, content.height() - handleThickness);
        int firstMain = resolveFirstMain(availableMain);
        resizeHandler.onResize(context, ratio, firstMain, Math.max(0, availableMain - firstMain));
    }

    private static float clampRatio(float ratio) {
        if (Float.isNaN(ratio) || Float.isInfinite(ratio)) {
            return 0.5F;
        }
        return Math.max(0.05F, Math.min(0.95F, ratio));
    }
}
