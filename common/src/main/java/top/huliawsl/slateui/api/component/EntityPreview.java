package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.DrawEntityPreviewCommand;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public final class EntityPreview extends SlateComponent {

    private final String entityType;
    private final float yaw;
    private final float pitch;

    public EntityPreview(String entityType, float yaw, float pitch, SlateStyle style) {
        super(SlateStyle.withDefaults(SlateStyle.builder().width(48).height(48).build(), style));
        this.entityType = entityType == null ? "minecraft:pig" : entityType;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        Size measured = applyStyleSize(new Size(48, 48));
        setMeasuredSize(measured);
        return measured;
    }

    @Override
    public void layout(SlateLayoutContext context, Rect bounds) {
        setBounds(bounds);
    }

    @Override
    public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
        commands.add(new DrawEntityPreviewCommand(bounds(), entityType, yaw, pitch));
    }
}
