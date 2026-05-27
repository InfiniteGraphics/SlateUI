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
    private final boolean missing;

    public Image(String resourceId, SlateStyle style) {
        super(style);
        this.resourceId = validResourceId(resourceId) ? resourceId : MISSING_TEXTURE;
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
        commands.add(new DrawTextureCommand(contentRect(bounds()), resourceId, missing));
    }

    private static boolean validResourceId(String resourceId) {
        if (resourceId == null || resourceId.isBlank()) {
            return false;
        }
        int separator = resourceId.indexOf(':');
        return separator > 0 && separator < resourceId.length() - 1;
    }
}
