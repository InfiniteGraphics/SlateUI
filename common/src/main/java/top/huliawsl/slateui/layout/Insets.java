package top.huliawsl.slateui.layout;

public record Insets(int left, int top, int right, int bottom) {

    public static final Insets ZERO = new Insets(0, 0, 0, 0);

    public Insets {
        if (left < 0 || top < 0 || right < 0 || bottom < 0) {
            throw new IllegalArgumentException("Insets must be >= 0");
        }
    }

    public static Insets all(int value) {
        return new Insets(value, value, value, value);
    }

    public static Insets symmetric(int horizontal, int vertical) {
        return new Insets(horizontal, vertical, horizontal, vertical);
    }

    public int horizontal() {
        return left + right;
    }

    public int vertical() {
        return top + bottom;
    }
}
