package top.huliawsl.slateui.hud;

import top.huliawsl.slateui.layout.Insets;

public record SlateHudConfig(
    SlateHudAnchor anchor,
    Insets safeArea,
    float scale,
    boolean visible,
    int maxDrawCommands,
    long maxRebuildNanos
) {

    public SlateHudConfig {
        anchor = anchor == null ? SlateHudAnchor.TOP_LEFT : anchor;
        safeArea = safeArea == null ? Insets.ZERO : safeArea;
        scale = Math.max(0.1F, scale);
        maxDrawCommands = Math.max(1, maxDrawCommands);
        maxRebuildNanos = Math.max(1L, maxRebuildNanos);
    }

    public static SlateHudConfig defaultConfig() {
        return new SlateHudConfig(SlateHudAnchor.TOP_LEFT, Insets.ZERO, 1F, true, 512, 2_000_000L);
    }
}
