package top.huliawsl.slateui.api.component;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.DrawImageCommand;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public class Image extends SlateComponent {

    private final ResourceLocation resourceLocation;
    private final boolean missing;

    public Image(String resourceId, SlateStyle style) {
        this(ResourceLocation.tryParse(resourceId), style);
    }

    public Image(ResourceLocation resourceLocation, SlateStyle style) {
        super(style);
        this.resourceLocation = resourceLocation == null ? ResourceLocation.withDefaultNamespace("missingno") : resourceLocation;
        this.missing = resourceLocation == null;
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        int width = style().width() != null ? style().width() : 64;
        int height = style().height() != null ? style().height() : 64;
        Size measured = applyStyleSize(new Size(width, height));
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
        commands.add(new DrawImageCommand(contentRect(bounds()), resourceLocation, missing));
    }
}
