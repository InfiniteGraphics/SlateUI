package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.SlateBorder;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.DrawRectCommand;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public final class ProgressBar extends SlateComponent {

    private static final SlateStyle DEFAULT_STYLE = SlateStyle.builder()
        .width(120)
        .height(12)
        .backgroundColor(0xFF1E293B)
        .border(new SlateBorder(0xFF475569, 1))
        .borderRadiusToken("radius.sm")
        .build();

    private final double progress;

    public ProgressBar(double progress, SlateStyle style) {
        super(SlateStyle.withDefaults(DEFAULT_STYLE, style));
        this.progress = Math.max(0D, Math.min(1D, progress));
    }

    public double progress() {
        return progress;
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        Size measured = applyStyleSize(new Size(120, 12));
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
        commands.add(new DrawRectCommand(new Rect(content.x(), content.y(), (int) Math.round(content.width() * progress), content.height()), 0xFF22C55E, resolveBorderRadius(context.theme())));
    }
}
