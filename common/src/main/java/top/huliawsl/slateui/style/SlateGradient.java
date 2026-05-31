package top.huliawsl.slateui.style;

public record SlateGradient(int startColor, int endColor, Direction direction) {

    public enum Direction {
        HORIZONTAL,
        VERTICAL
    }
}
