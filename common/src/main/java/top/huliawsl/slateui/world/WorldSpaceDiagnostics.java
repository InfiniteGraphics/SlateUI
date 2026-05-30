package top.huliawsl.slateui.world;

public record WorldSpaceDiagnostics(int drawCommandCount, boolean culled, boolean overBudget) {

    public static WorldSpaceDiagnostics empty() {
        return new WorldSpaceDiagnostics(0, false, false);
    }
}
