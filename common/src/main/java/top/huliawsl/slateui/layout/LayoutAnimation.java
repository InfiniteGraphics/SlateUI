package top.huliawsl.slateui.layout;

public record LayoutAnimation(long durationMillis, boolean enabled) {

    public static LayoutAnimation disabled() {
        return new LayoutAnimation(0L, false);
    }
}
