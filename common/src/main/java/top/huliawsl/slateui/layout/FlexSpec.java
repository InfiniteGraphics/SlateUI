package top.huliawsl.slateui.layout;

public record FlexSpec(float grow, float shrink, int basis) {

    public FlexSpec {
        grow = Math.max(0F, grow);
        shrink = Math.max(0F, shrink);
        basis = Math.max(0, basis);
    }
}
