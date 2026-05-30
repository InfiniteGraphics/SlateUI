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

public final class FluidView extends SlateComponent {

    private final String fluidId;
    private final int amount;
    private final int capacity;

    public FluidView(String fluidId, int amount, int capacity, SlateStyle style) {
        super(SlateStyle.withDefaults(SlateStyle.builder().width(18).height(48).backgroundColor(0xFF0F172A).build(), style));
        this.fluidId = fluidId == null ? "" : fluidId;
        this.amount = Math.max(0, amount);
        this.capacity = Math.max(0, capacity);
    }

    public String fluidId() {
        return fluidId;
    }

    public int amount() {
        return amount;
    }

    public int capacity() {
        return capacity;
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        Size measured = applyStyleSize(new Size(18, 48));
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
        double fill = capacity <= 0 ? 0D : Math.max(0D, Math.min(1D, amount / (double) capacity));
        int fillHeight = (int) Math.round(content.height() * fill);
        commands.add(new DrawRectCommand(new Rect(content.x(), content.bottom() - fillHeight, content.width(), fillHeight), 0xFF38BDF8, resolveBorderRadius(context.theme())));
    }
}
