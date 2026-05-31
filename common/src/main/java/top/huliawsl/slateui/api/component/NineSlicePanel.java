package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.layout.Insets;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.DrawNineSliceTextureCommand;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public final class NineSlicePanel extends Panel {

    private final String texture;
    private final Insets slices;
    private final int textureWidth;
    private final int textureHeight;

    public NineSlicePanel(String texture, Insets slices, int textureWidth, int textureHeight, List<SlateComponent> children, SlateStyle style) {
        super("", children, style);
        this.texture = texture == null ? "" : texture;
        this.slices = slices == null ? Insets.ZERO : slices;
        this.textureWidth = Math.max(1, textureWidth);
        this.textureHeight = Math.max(1, textureHeight);
    }

    @Override
    public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
        commands.add(new DrawNineSliceTextureCommand(bounds(), texture, slices, textureWidth, textureHeight));
        super.collectDrawCommands(context, commands);
    }
}
