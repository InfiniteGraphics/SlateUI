package top.huliawsl.slateui.animation;

public record SlateTween(float from, float to, long durationMillis, SlateEasing easing) {

    public float valueAt(long elapsedMillis) {
        if (durationMillis <= 0) {
            return to;
        }
        float t = Math.max(0F, Math.min(1F, elapsedMillis / (float) durationMillis));
        SlateEasing resolvedEasing = easing == null ? SlateEasing.LINEAR : easing;
        return from + (to - from) * resolvedEasing.apply(t);
    }
}
