package top.huliawsl.slateui.demo;

public final class SlateDemoEntrypoint {

    private static boolean titleScreenHookAvailable;

    private SlateDemoEntrypoint() {
    }

    public static void markTitleScreenHookAvailable() {
        titleScreenHookAvailable = true;
    }

    public static boolean isTitleScreenHookAvailable() {
        return titleScreenHookAvailable;
    }
}
