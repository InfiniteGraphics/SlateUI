package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.DrawTextureCommand;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public class Image extends SlateComponent {

    private static final String MISSING_TEXTURE = "minecraft:missingno";

    private final String resourceId;
    private final int u;
    private final int v;
    private final int textureWidth;
    private final int textureHeight;
    private final int regionWidth;
    private final int regionHeight;
    private final boolean missing;

    public Image(String resourceId, SlateStyle style) {
        this(resourceId, 0, 0, 256, 256, 0, 0, style);
    }

    public Image(String resourceId, int u, int v, int textureWidth, int textureHeight, int regionWidth, int regionHeight, SlateStyle style) {
        super(style);
        this.resourceId = validResourceId(resourceId) ? resourceId : MISSING_TEXTURE;
        this.u = Math.max(0, u);
        this.v = Math.max(0, v);
        this.textureWidth = Math.max(1, textureWidth);
        this.textureHeight = Math.max(1, textureHeight);
        this.regionWidth = Math.max(0, regionWidth);
        this.regionHeight = Math.max(0, regionHeight);
        this.missing = !validResourceId(resourceId);
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
        Rect rect = contentRect(bounds());
        int resolvedRegionWidth = regionWidth > 0 ? regionWidth : rect.width();
        int resolvedRegionHeight = regionHeight > 0 ? regionHeight : rect.height();
        commands.add(new DrawTextureCommand(rect, resourceId, u, v, textureWidth, textureHeight, resolvedRegionWidth, resolvedRegionHeight, missing));
    }

    private static boolean validResourceId(String resourceId) {
        if (resourceId == null || resourceId.isBlank()) {
            return false;
        }
        int separator = resourceId.indexOf(':');
        return separator > 0 && separator < resourceId.length() - 1;
    }
}
