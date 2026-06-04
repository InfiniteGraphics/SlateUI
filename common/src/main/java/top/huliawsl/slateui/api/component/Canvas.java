package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.SlateBorder;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.layout.Insets;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.runtime.InvalidationType;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public final class Canvas extends SlateComponent {

    private static final SlateStyle DEFAULT_STYLE = SlateStyle.builder()
        .minWidth(32)
        .minHeight(32)
        .backgroundColor(0x00000000)
        .border(SlateBorder.NONE)
        .padding(Insets.ZERO)
        .clipContent(true)
        .build();

    private final CanvasPainter painter;
    private final CanvasInputHandler inputHandler;
    private final CanvasViewport viewport;
    private final int preferredWidth;
    private final int preferredHeight;

    public Canvas(CanvasPainter painter, CanvasInputHandler inputHandler, int preferredWidth, int preferredHeight, SlateStyle style) {
        this(painter, inputHandler, null, preferredWidth, preferredHeight, style);
    }

    public Canvas(CanvasPainter painter, CanvasInputHandler inputHandler, CanvasViewport viewport, int preferredWidth, int preferredHeight, SlateStyle style) {
        super(SlateStyle.withDefaults(DEFAULT_STYLE, style));
        this.painter = painter == null ? ignored -> {} : painter;
        this.inputHandler = inputHandler;
        this.viewport = viewport;
        this.preferredWidth = Math.max(0, preferredWidth);
        this.preferredHeight = Math.max(0, preferredHeight);
    }

    public Canvas(CanvasPainter painter, int preferredWidth, int preferredHeight, SlateStyle style) {
        this(painter, null, null, preferredWidth, preferredHeight, style);
    }

    public CanvasViewport viewport() {
        return viewport;
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        int width = style().width() != null ? style().width() : (preferredWidth > 0 ? preferredWidth : available.width());
        int height = style().height() != null ? style().height() : (preferredHeight > 0 ? preferredHeight : available.height());
        Size measured = applyStyleSize(new Size(Math.max(0, width), Math.max(0, height)));
        setMeasuredSize(measured);
        return measured;
    }

    @Override
    public void layout(SlateLayoutContext context, Rect bounds) {
        setBounds(bounds);
    }

    @Override
    public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
        emitBoxChrome(context, commands);
        Rect content = contentRect(bounds());
        pushClip(context, commands, content);
        painter.paint(new CanvasDrawContext(context, content, commands));
        popClip(commands);
    }

    @Override
    public boolean mouseClicked(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        if (!canHandle(mouseX, mouseY)) {
            return false;
        }
        boolean handled = dispatch(context, CanvasPointerEvent.click(mouseX, mouseY, button, contentRect(bounds()), viewport));
        if (handled) {
            setPressed(true);
            context.requestFocus(this);
            context.requestInvalidation(InvalidationType.INTERACTION, "canvas-click");
        }
        return handled;
    }

    @Override
    public boolean mouseReleased(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        boolean handled = dispatch(context, CanvasPointerEvent.release(mouseX, mouseY, button, contentRect(bounds()), viewport));
        if (isPressed()) {
            setPressed(false);
            context.requestInvalidation(InvalidationType.INTERACTION, "canvas-release");
            return true;
        }
        return handled;
    }

    @Override
    public boolean mouseMoved(SlateInteractionContext context, double mouseX, double mouseY) {
        boolean inside = canHandle(mouseX, mouseY);
        setHovered(inside);
        if (!inside) {
            return false;
        }
        return dispatch(context, CanvasPointerEvent.move(mouseX, mouseY, contentRect(bounds()), viewport));
    }

    @Override
    public boolean mouseDragged(SlateInteractionContext context, double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!isPressed() && !canHandle(mouseX, mouseY)) {
            return false;
        }
        return dispatch(context, CanvasPointerEvent.drag(mouseX, mouseY, button, dragX, dragY, contentRect(bounds()), viewport));
    }

    @Override
    public boolean mouseScrolled(SlateInteractionContext context, double mouseX, double mouseY, double delta) {
        if (!canHandle(mouseX, mouseY)) {
            return false;
        }
        return dispatch(context, CanvasPointerEvent.scroll(mouseX, mouseY, delta, contentRect(bounds()), viewport));
    }

    private boolean canHandle(double mouseX, double mouseY) {
        return !style().disabled() && contentRect(bounds()).contains(mouseX, mouseY);
    }

    private boolean dispatch(SlateInteractionContext context, CanvasPointerEvent event) {
        return inputHandler != null && inputHandler.handle(context, event);
    }
}
