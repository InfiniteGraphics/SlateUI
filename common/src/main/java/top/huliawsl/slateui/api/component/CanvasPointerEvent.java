package top.huliawsl.slateui.api.component;

import top.huliawsl.slateui.layout.Rect;

public record CanvasPointerEvent(
    Type type,
    double screenX,
    double screenY,
    double localX,
    double localY,
    int button,
    double dragX,
    double dragY,
    double scrollDelta,
    Rect bounds
) {

    public enum Type {
        CLICK,
        RELEASE,
        MOVE,
        DRAG,
        SCROLL
    }

    public static CanvasPointerEvent click(double screenX, double screenY, int button, Rect bounds) {
        return of(Type.CLICK, screenX, screenY, button, 0, 0, 0, bounds);
    }

    public static CanvasPointerEvent release(double screenX, double screenY, int button, Rect bounds) {
        return of(Type.RELEASE, screenX, screenY, button, 0, 0, 0, bounds);
    }

    public static CanvasPointerEvent move(double screenX, double screenY, Rect bounds) {
        return of(Type.MOVE, screenX, screenY, -1, 0, 0, 0, bounds);
    }

    public static CanvasPointerEvent drag(double screenX, double screenY, int button, double dragX, double dragY, Rect bounds) {
        return of(Type.DRAG, screenX, screenY, button, dragX, dragY, 0, bounds);
    }

    public static CanvasPointerEvent scroll(double screenX, double screenY, double scrollDelta, Rect bounds) {
        return of(Type.SCROLL, screenX, screenY, -1, 0, 0, scrollDelta, bounds);
    }

    private static CanvasPointerEvent of(Type type, double screenX, double screenY, int button, double dragX, double dragY, double scrollDelta, Rect bounds) {
        Rect safeBounds = bounds == null ? Rect.ZERO : bounds;
        return new CanvasPointerEvent(
            type,
            screenX,
            screenY,
            screenX - safeBounds.x(),
            screenY - safeBounds.y(),
            button,
            dragX,
            dragY,
            scrollDelta,
            safeBounds
        );
    }
}
