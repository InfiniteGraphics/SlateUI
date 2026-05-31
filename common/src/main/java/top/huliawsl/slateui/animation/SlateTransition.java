package top.huliawsl.slateui.animation;

public record SlateTransition(String name, SlateTween tween, boolean enabled) {

    public static SlateTransition disabled(String name) {
        return new SlateTransition(name, new SlateTween(0F, 0F, 0L, SlateEasing.LINEAR), false);
    }
}
