package top.huliawsl.slateui.api.container;

public record SlateContainerPolicy(
    boolean quickMoveEnabled,
    boolean recipeBookCompatible,
    boolean vanillaSlotInterop,
    String ecosystemLayer
) {

    public static SlateContainerPolicy serverAuthoritative() {
        return new SlateContainerPolicy(true, false, true, "JEI/REI/EMI/XEI adapter point");
    }
}
