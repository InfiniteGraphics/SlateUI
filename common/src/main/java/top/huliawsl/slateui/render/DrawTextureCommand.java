package top.huliawsl.slateui.render;

import top.huliawsl.slateui.layout.Rect;

public record DrawTextureCommand(
    Rect rect,
    String texture,
    int u,
    int v,
    int textureWidth,
    int textureHeight,
    int regionWidth,
    int regionHeight,
    boolean missing
) implements DrawCommand {

    public DrawTextureCommand(Rect rect, String texture, boolean missing) {
        this(rect, texture, 0, 0, 256, 256, rect.width(), rect.height(), missing);
    }

    @Override
    public String describe() {
        return "DrawTexture rect=" + rect
            + " texture=" + texture
            + " uv=" + u + "," + v
            + " region=" + regionWidth + "x" + regionHeight
            + " atlas=" + textureWidth + "x" + textureHeight
            + (missing ? " missing" : "");
    }
}
