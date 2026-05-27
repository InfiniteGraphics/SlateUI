package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public class Box extends SlateComponent {

    private final List<SlateComponent> children;

    public Box(List<SlateComponent> children, SlateStyle style) {
        super(style);
        this.children = List.copyOf(children);
    }

    @Override
    public List<SlateComponent> children() {
        return children;
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        Size contentAvailable = contentAvailable(available);
        int maxWidth = 0;
        int maxHeight = 0;
        for (SlateComponent child : children) {
            Size childSize = measureChild(context, child, contentAvailable);
            maxWidth = Math.max(maxWidth, childSize.width());
            maxHeight = Math.max(maxHeight, childSize.height());
        }
        Size measured = applyStyleSize(addInsets(new Size(maxWidth, maxHeight), style().padding()));
        setMeasuredSize(measured);
        return measured;
    }

    @Override
    public void layout(SlateLayoutContext context, Rect bounds) {
        setBounds(bounds);
        Rect contentRect = contentRect(bounds);
        for (SlateComponent child : children) {
            Rect childBounds = alignChild(contentRect, child.layoutNode().measuredSize());
            layoutChild(context, child, childBounds);
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
}
