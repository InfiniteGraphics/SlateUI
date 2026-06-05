package top.huliawsl.slateui.platform;

import java.util.Optional;
import top.huliawsl.slateui.platform.services.SlateConfigScreenProvider;
import top.huliawsl.slateui.platform.services.SlatePlatformRegistry;

public final class FabricConfigScreenIntegration {

    private FabricConfigScreenIntegration() {
    }

    public static String loaderId() {
        return "fabric";
    }

    public static void register(String modId, SlateConfigScreenProvider provider) {
        SlatePlatformRegistry.global().registerConfigScreen(modId, provider);
    }

    public static Optional<SlateConfigScreenProvider> find(String modId) {
        return SlatePlatformRegistry.global().configScreen(modId);
    }
}
