package top.huliawsl.slateui.api;

public record SlateBorder(int color, int thickness) {

    public static final SlateBorder NONE = new SlateBorder(0, 0);

    public SlateBorder {
        if (thickness < 0) {
            throw new IllegalArgumentException("Border thickness must be >= 0");
        }
    }
}
