package top.huliawsl.slateui.authoring;

public record SlateReloadOptions(
    boolean developmentOnly,
    boolean resourceReload,
    boolean watchSlateFiles,
    boolean reloadTheme,
    boolean reloadOverrides,
    boolean preserveState,
    boolean showErrorsInOverlay
) {

    public static SlateReloadOptions development() {
        return new SlateReloadOptions(true, true, true, true, true, true, true);
    }
}
