package top.huliawsl.slateui.api.component;

import java.util.List;
import java.util.Map;
import top.huliawsl.slateui.api.SlateBorder;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.debug.SlateRuntimeException;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.DrawRectCommand;
import top.huliawsl.slateui.runtime.InvalidationType;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public final class Slider extends SlateComponent {

    private static final SlateStyle DEFAULT_STYLE = SlateStyle.builder()
        .width(120)
        .height(16)
        .border(new SlateBorder(0xFF475569, 1))
        .borderRadiusToken("radius.sm")
        .build();

    private final double min;
    private final double max;
    private double value;
    private final String changeCommand;

    public Slider(double min, double max, double value, String changeCommand, SlateStyle style) {
        super(SlateStyle.withDefaults(DEFAULT_STYLE, style));
        this.min = Math.min(min, max);
        this.max = Math.max(min, max);
        this.value = clamp(value);
        this.changeCommand = changeCommand;
    }

    public double value() {
        return value;
    }

    @Override
    public boolean focusable() {
        return true;
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        Size measured = applyStyleSize(new Size(120, 16));
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
        Rect track = new Rect(bounds().x() + 2, bounds().y() + bounds().height() / 2 - 2, Math.max(0, bounds().width() - 4), 4);
        int fillWidth = (int) Math.round(track.width() * normalized());
        commands.add(new DrawRectCommand(track, 0xFF334155, 2));
        commands.add(new DrawRectCommand(new Rect(track.x(), track.y(), fillWidth, track.height()), 0xFF60A5FA, 2));
        int thumbX = track.x() + Math.max(0, fillWidth - 3);
        commands.add(new DrawRectCommand(new Rect(thumbX, bounds().y() + 2, 6, Math.max(1, bounds().height() - 4)), 0xFFFFFFFF, 3));
    }

    @Override
    public boolean mouseClicked(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        if (!bounds().contains(mouseX, mouseY) || style().disabled()) {
            return false;
        }
        setFromMouse(context, mouseX);
        context.requestFocus(this);
        return true;
    }

    @Override
    public boolean mouseMoved(SlateInteractionContext context, double mouseX, double mouseY) {
        if (isPressed()) {
            setFromMouse(context, mouseX);
            return true;
        }
        return super.mouseMoved(context, mouseX, mouseY);
    }

    private void setFromMouse(SlateInteractionContext context, double mouseX) {
        double next = min + (max - min) * Math.max(0D, Math.min(1D, (mouseX - bounds().x()) / Math.max(1D, bounds().width())));
        value = clamp(next);
        if (changeCommand != null && !changeCommand.isBlank()) {
            try {
                context.commands().execute(changeCommand, context, Map.of("value", value, "min", min, "max", max));
            } catch (Throwable throwable) {
                throw SlateRuntimeException.command(this, changeCommand, throwable);
            }
        }
        context.requestInvalidation(InvalidationType.LAYOUT, "slider-change");
    }

    private double normalized() {
        return max == min ? 0D : (value - min) / (max - min);
    }

    private double clamp(double next) {
        return Math.max(min, Math.min(max, next));
    }
}
