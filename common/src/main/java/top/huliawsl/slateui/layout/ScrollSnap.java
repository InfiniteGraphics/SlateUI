package top.huliawsl.slateui.layout;

public record ScrollSnap(int interval) {

    public int snap(int offset) {
        if (interval <= 0) {
            return Math.max(0, offset);
        }
        return Math.max(0, Math.round(offset / (float) interval) * interval);
    }
}
