package top.huliawsl.slateui.hud;

public record SlateHudDiagnostics(int drawCommandCount, long rebuildNanos, boolean overBudget) {

    public static SlateHudDiagnostics empty() {
        return new SlateHudDiagnostics(0, 0L, false);
    }
}
