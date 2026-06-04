package top.huliawsl.slateui.runtime;

import java.util.List;
import top.huliawsl.slateui.layout.Point;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.api.SlateText;
import top.huliawsl.slateui.render.RawDrawCallback;

public interface SlateRenderer {

    record Capabilities(
        boolean supportsRoundedRect,
        boolean supportsScissor,
        boolean supportsTextureRegion,
        boolean supportsItemIcon,
        boolean supportsTextComponent,
        boolean supportsLinePrimitive,
        boolean supportsRawDraw
    ) {

        public static final Capabilities BASIC = new Capabilities(false, false, false, false, false, false, false);
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

    default void drawLine(Point start, Point end, int color, int thickness) {
        if (start == null || end == null) {
            return;
        }
        int left = Math.min(start.x(), end.x());
        int top = Math.min(start.y(), end.y());
        int width = Math.max(1, Math.abs(end.x() - start.x()) + thickness);
        int height = Math.max(1, Math.abs(end.y() - start.y()) + thickness);
        fill(new Rect(left, top, width, height), color, 0);
    }

    default void drawPolyline(List<Point> points, int color, int thickness) {
        if (points == null || points.size() < 2) {
            return;
        }
        for (int index = 0; index < points.size() - 1; index++) {
            drawLine(points.get(index), points.get(index + 1), color, thickness);
        }
    }

    default void drawRaw(Rect bounds, RawDrawCallback callback) {
        if (callback != null) {
            callback.draw(this, bounds == null ? Rect.ZERO : bounds);
        }
    }

    default Object nativeSurface() {
        return null;
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
