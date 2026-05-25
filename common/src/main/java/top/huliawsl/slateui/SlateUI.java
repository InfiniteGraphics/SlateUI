package top.huliawsl.slateui;

import top.huliawsl.slateui.demo.SlateDemoEntrypoint;
import top.huliawsl.slateui.platform.Services;

public final class SlateUI {

    private static boolean initialized;

    private SlateUI() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        Constants.LOG.info("Initializing SlateUI on {}", Services.PLATFORM.getPlatformName());
    }

    public static void registerTitleScreenHook() {
        if (!Services.PLATFORM.isDevelopmentEnvironment()) {
            return;
        }
        SlateDemoEntrypoint.markTitleScreenHookAvailable();
    }
}
