package top.huliawsl.slateui.layout;

public record AbsolutePlacement(int left, int top, int width, int height) {

    public Rect toRect(Rect container) {
        return new Rect(container.x() + left, container.y() + top, Math.max(0, width), Math.max(0, height));
    }
}
