package top.huliawsl.slateui.render;

import top.huliawsl.slateui.layout.Insets;
import top.huliawsl.slateui.layout.Rect;

public record DrawNineSliceTextureCommand(
    Rect rect,
    String texture,
    Insets slices,
    int textureWidth,
    int textureHeight
) implements DrawCommand {

    @Override
    public String describe() {
        return "nine-slice texture " + texture + " " + rect + " slices=" + slices;
    }
}
