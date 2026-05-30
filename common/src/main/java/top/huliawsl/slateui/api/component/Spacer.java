package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public final class Spacer extends SlateComponent {

    private final Size preferredSize;

    public Spacer(int width, int height) {
        this(width, height, SlateStyle.EMPTY);
    }

    public Spacer(int width, int height, SlateStyle style) {
        super(style);
        this.preferredSize = new Size(Math.max(0, width), Math.max(0, height));
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        Size measured = applyStyleSize(preferredSize);
        setMeasuredSize(measured);
        return measured;
    }

    @Override
    public void layout(SlateLayoutContext context, Rect bounds) {
        setBounds(bounds);
    }

    @Override
    public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
    }
}
