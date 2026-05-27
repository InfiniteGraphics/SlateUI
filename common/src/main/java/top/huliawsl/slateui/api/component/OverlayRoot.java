package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public class OverlayRoot extends SlateComponent {

    private final List<SlateComponent> children;

    public OverlayRoot(List<SlateComponent> children, SlateStyle style) {
        super(style);
        this.children = List.copyOf(children);
    }

    @Override
    public List<SlateComponent> children() {
        return children;
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        for (SlateComponent child : children) {
            measureChild(context, child, contentAvailable(available));
        }
        Size measured = applyStyleSize(available);
        setMeasuredSize(measured);
        return measured;
    }

    @Override
    public void layout(SlateLayoutContext context, Rect bounds) {
        setBounds(bounds);
        Rect contentRect = contentRect(bounds);
        for (SlateComponent child : children) {
            layoutChild(context, child, alignChild(contentRect, child.layoutNode().measuredSize()));
        }
    }

    @Override
    public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
        emitBoxChrome(context, commands);
        for (SlateComponent child : children) {
            collectChild(context, commands, child);
        }
    }
}
