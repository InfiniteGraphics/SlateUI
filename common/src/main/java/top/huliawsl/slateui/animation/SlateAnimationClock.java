package top.huliawsl.slateui.animation;

public final class SlateAnimationClock {

    private long nowMillis;
    private boolean enabled = true;

    public long nowMillis() {
        return nowMillis;
    }

    public void tick(long deltaMillis) {
        if (enabled) {
            nowMillis += Math.max(0L, deltaMillis);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean enabled() {
        return enabled;
    }
}
