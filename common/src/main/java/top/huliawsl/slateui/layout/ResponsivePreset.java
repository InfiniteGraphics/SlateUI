package top.huliawsl.slateui.layout;

public enum ResponsivePreset {
    COMPACT,
    NORMAL,
    WIDE;

    public static ResponsivePreset fromWidth(int width) {
        if (width < 480) {
            return COMPACT;
        }
        if (width >= 960) {
            return WIDE;
        }
        return NORMAL;
    }
}
