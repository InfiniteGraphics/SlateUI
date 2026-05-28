package top.huliawsl.slateui.runtime;

import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.api.SlateText;

public interface SlateRenderer {

    record Capabilities(
        boolean supportsRoundedRect,
        boolean supportsScissor,
        boolean supportsTextureRegion,
        boolean supportsItemIcon,
        boolean supportsTextComponent
    ) {

        public static final Capabilities BASIC = new Capabilities(false, false, false, false, false);
    }

    default Capabilities capabilities() {
        return Capabilities.BASIC;
    }

    void fill(Rect rect, int color, int radius);

    void drawBorder(Rect rect, int color, int thickness, int radius);

    void drawText(int x, int y, SlateText text, int color);

    void drawTexture(Rect rect, String texture, int u, int v, int textureWidth, int textureHeight, int regionWidth, int regionHeight);

    void pushClip(Rect rect, int radius);

    void popClip();
}
