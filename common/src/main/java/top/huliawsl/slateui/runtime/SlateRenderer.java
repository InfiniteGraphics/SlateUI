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

    default void drawNineSliceTexture(Rect rect, String texture, top.huliawsl.slateui.layout.Insets slices, int textureWidth, int textureHeight) {
        drawTexture(rect, texture, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
    }

    default void drawItemIcon(Rect rect, String itemId, int count) {
        drawBorder(rect, 0x66FFFFFF, 1, 0);
    }

    default void drawEntityPreview(Rect rect, String entityType, float yaw, float pitch) {
        drawBorder(rect, 0x66FFFFFF, 1, 0);
    }

    default void pushTransform(float translateX, float translateY, float scale, float rotationDegrees, float opacity) {
    }

    default void popTransform() {
    }

    void fill(Rect rect, int color, int radius);

    void drawBorder(Rect rect, int color, int thickness, int radius);

    void drawText(int x, int y, SlateText text, int color);

    void drawTexture(Rect rect, String texture, int u, int v, int textureWidth, int textureHeight, int regionWidth, int regionHeight);

    void pushClip(Rect rect, int radius);

    void popClip();
}
