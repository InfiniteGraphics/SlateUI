package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.DrawItemIconCommand;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public class ItemIcon extends SlateComponent {

    private final String itemId;
    private final int count;

    public ItemIcon(String itemId, int count, SlateStyle style) {
        super(SlateStyle.withDefaults(SlateStyle.builder().width(16).height(16).build(), style));
        this.itemId = itemId == null ? "minecraft:air" : itemId;
        this.count = Math.max(0, count);
    }

    public String itemId() {
        return itemId;
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        Size measured = applyStyleSize(new Size(16, 16));
        setMeasuredSize(measured);
        return measured;
    }

    @Override
    public void layout(SlateLayoutContext context, Rect bounds) {
        setBounds(bounds);
    }

    @Override
    public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
        commands.add(new DrawItemIconCommand(bounds(), itemId, count));
    }
}
