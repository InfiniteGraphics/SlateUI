package top.huliawsl.slateui.style;

public record SlateMediaRule(int minWidth, int maxWidth, float scale) {

    public boolean matches(int width) {
        return width >= minWidth && (maxWidth <= 0 || width <= maxWidth);
    }
}
