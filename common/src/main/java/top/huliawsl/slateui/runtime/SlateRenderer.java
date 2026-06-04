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


    default void drawBezier(Point start, Point control1, Point control2, Point end, int color, int thickness) {
        if (start == null || control1 == null || control2 == null || end == null) {
            return;
        }
        java.util.ArrayList<Point> points = new java.util.ArrayList<>();
        for (int i = 0; i <= 32; i++) {
            double t = i / 32D;
            double u = 1D - t;
            int x = (int) Math.round(u * u * u * start.x() + 3D * u * u * t * control1.x() + 3D * u * t * t * control2.x() + t * t * t * end.x());
            int y = (int) Math.round(u * u * u * start.y() + 3D * u * u * t * control1.y() + 3D * u * t * t * control2.y() + t * t * t * end.y());
            points.add(new Point(x, y));
        }
        drawPolyline(points, color, thickness);
    }

    default void drawCircle(Point center, int radius, int color, boolean filled, int thickness) {
        if (center == null || radius <= 0) {
            return;
        }
        int lineThickness = Math.max(1, thickness);
        if (filled) {
            for (int dy = -radius; dy <= radius; dy++) {
                int halfWidth = (int) Math.floor(Math.sqrt(Math.max(0, radius * radius - dy * dy)));
                fill(new Rect(center.x() - halfWidth, center.y() + dy, halfWidth * 2 + 1, 1), color, 0);
            }
            return;
        }
        int outer = radius;
        int inner = Math.max(0, radius - lineThickness);
        for (int dy = -outer; dy <= outer; dy++) {
            int outerHalf = (int) Math.floor(Math.sqrt(Math.max(0, outer * outer - dy * dy)));
            int innerHalf = Math.abs(dy) >= inner ? -1 : (int) Math.floor(Math.sqrt(Math.max(0, inner * inner - dy * dy)));
            if (innerHalf < 0) {
                fill(new Rect(center.x() - outerHalf, center.y() + dy, outerHalf * 2 + 1, 1), color, 0);
            } else {
                fill(new Rect(center.x() - outerHalf, center.y() + dy, Math.max(1, outerHalf - innerHalf), 1), color, 0);
                fill(new Rect(center.x() + innerHalf + 1, center.y() + dy, Math.max(1, outerHalf - innerHalf), 1), color, 0);
            }
        }
    }

    default void drawDashedLine(Point start, Point end, int color, int thickness, int dashLength, int gapLength) {
        if (start == null || end == null) {
            return;
        }
        double dx = end.x() - start.x();
        double dy = end.y() - start.y();
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length <= 0D) {
            return;
        }
        int dash = Math.max(1, dashLength);
        int gap = Math.max(1, gapLength);
        double unitX = dx / length;
        double unitY = dy / length;
        for (double cursor = 0D; cursor < length; cursor += dash + gap) {
            double endCursor = Math.min(length, cursor + dash);
            Point a = new Point((int) Math.round(start.x() + unitX * cursor), (int) Math.round(start.y() + unitY * cursor));
            Point b = new Point((int) Math.round(start.x() + unitX * endCursor), (int) Math.round(start.y() + unitY * endCursor));
            drawLine(a, b, color, thickness);
        }
    }

    default void drawArrow(Point start, Point end, int color, int thickness, int headLength) {
        if (start == null || end == null) {
            return;
        }
        drawLine(start, end, color, thickness);
        double angle = Math.atan2(end.y() - start.y(), end.x() - start.x());
        double head = Math.max(4, headLength);
        double wing = Math.PI / 7D;
        Point left = new Point((int) Math.round(end.x() - Math.cos(angle - wing) * head), (int) Math.round(end.y() - Math.sin(angle - wing) * head));
        Point right = new Point((int) Math.round(end.x() - Math.cos(angle + wing) * head), (int) Math.round(end.y() - Math.sin(angle + wing) * head));
        drawLine(end, left, color, thickness);
        drawLine(end, right, color, thickness);
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
