package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.layout.AbsolutePlacement;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public final class AbsoluteOverlay extends SlateComponent {

    public record Child(SlateComponent component, AbsolutePlacement placement) {}

    private final List<Child> children;

    public AbsoluteOverlay(List<Child> children, SlateStyle style) {
        super(style);
        this.children = List.copyOf(children == null ? List.of() : children);
    }

    @Override
    public List<SlateComponent> children() {
        return children.stream().map(Child::component).toList();
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        for (Child child : children) {
            measureChild(context, child.component(), new Size(child.placement().width(), child.placement().height()));
        }
        Size measured = applyStyleSize(available);
        setMeasuredSize(measured);
        return measured;
    }

    @Override
    public void layout(SlateLayoutContext context, Rect bounds) {
        setBounds(bounds);
        for (Child child : children) {
            layoutChild(context, child.component(), child.placement().toRect(bounds));
        }
    }

    @Override
    public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
        for (Child child : children) {
            collectChild(context, commands, child.component());
        }
    }
}
