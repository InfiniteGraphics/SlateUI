package top.huliawsl.slateui.api.component;

import top.huliawsl.slateui.layout.Rect;

public record CanvasPointerEvent(
    Type type,
    double screenX,
    double screenY,
    double localX,
    double localY,
    double worldX,
    double worldY,
    int button,
    double dragX,
    double dragY,
    double scrollDelta,
    Rect bounds,
    CanvasViewport viewport
) {

    public enum Type {
        CLICK,
        RELEASE,
        MOVE,
        DRAG,
        SCROLL
    }

    public static CanvasPointerEvent click(double screenX, double screenY, int button, Rect bounds) {
        return click(screenX, screenY, button, bounds, null);
    }

    public static CanvasPointerEvent click(double screenX, double screenY, int button, Rect bounds, CanvasViewport viewport) {
        return of(Type.CLICK, screenX, screenY, button, 0, 0, 0, bounds, viewport);
    }

    public static CanvasPointerEvent release(double screenX, double screenY, int button, Rect bounds) {
        return release(screenX, screenY, button, bounds, null);
    }

    public static CanvasPointerEvent release(double screenX, double screenY, int button, Rect bounds, CanvasViewport viewport) {
        return of(Type.RELEASE, screenX, screenY, button, 0, 0, 0, bounds, viewport);
    }

    public static CanvasPointerEvent move(double screenX, double screenY, Rect bounds) {
        return move(screenX, screenY, bounds, null);
    }

    public static CanvasPointerEvent move(double screenX, double screenY, Rect bounds, CanvasViewport viewport) {
        return of(Type.MOVE, screenX, screenY, -1, 0, 0, 0, bounds, viewport);
    }

    public static CanvasPointerEvent drag(double screenX, double screenY, int button, double dragX, double dragY, Rect bounds) {
        return drag(screenX, screenY, button, dragX, dragY, bounds, null);
    }

    public static CanvasPointerEvent drag(double screenX, double screenY, int button, double dragX, double dragY, Rect bounds, CanvasViewport viewport) {
        return of(Type.DRAG, screenX, screenY, button, dragX, dragY, 0, bounds, viewport);
    }

    public static CanvasPointerEvent scroll(double screenX, double screenY, double scrollDelta, Rect bounds) {
        return scroll(screenX, screenY, scrollDelta, bounds, null);
    }

    public static CanvasPointerEvent scroll(double screenX, double screenY, double scrollDelta, Rect bounds, CanvasViewport viewport) {
        return of(Type.SCROLL, screenX, screenY, -1, 0, 0, scrollDelta, bounds, viewport);
    }

    private static CanvasPointerEvent of(Type type, double screenX, double screenY, int button, double dragX, double dragY, double scrollDelta, Rect bounds, CanvasViewport viewport) {
        Rect safeBounds = bounds == null ? Rect.ZERO : bounds;
        double localX = screenX - safeBounds.x();
        double localY = screenY - safeBounds.y();
        double worldX = viewport == null ? localX : viewport.screenToWorldX(localX);
        double worldY = viewport == null ? localY : viewport.screenToWorldY(localY);
        return new CanvasPointerEvent(
            type,
            screenX,
            screenY,
            localX,
            localY,
            worldX,
            worldY,
            button,
            dragX,
            dragY,
            scrollDelta,
            safeBounds,
            viewport
        );
    }
}
