package top.huliawsl.slateui.demo;

import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public final class FaultyComponent extends SlateComponent {

    public FaultyComponent() {
        super(SlateStyle.EMPTY);
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        throw new IllegalStateException("Intentional MVP0 error demo");
    }

    @Override
    public void layout(SlateLayoutContext context, Rect bounds) {
        throw new UnsupportedOperationException("FaultyComponent does not lay out");
    }

    @Override
    public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
        throw new UnsupportedOperationException("FaultyComponent does not render");
    }
}
