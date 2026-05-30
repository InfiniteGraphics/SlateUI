package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.DrawRectCommand;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public final class Divider extends SlateComponent {

    private final boolean vertical;
    private final int thickness;
    private final int color;

    public Divider(boolean vertical) {
        this(vertical, 1, 0xFF475569, SlateStyle.EMPTY);
    }

    public Divider(boolean vertical, int thickness, int color, SlateStyle style) {
        super(style);
        this.vertical = vertical;
        this.thickness = Math.max(1, thickness);
        this.color = color;
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        Size base = vertical ? new Size(thickness, Math.max(1, available.height())) : new Size(Math.max(1, available.width()), thickness);
        Size measured = applyStyleSize(base);
        setMeasuredSize(measured);
        return measured;
    }

    @Override
    public void layout(SlateLayoutContext context, Rect bounds) {
        setBounds(bounds);
    }

    @Override
    public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
        commands.add(new DrawRectCommand(bounds(), color));
    }
}
