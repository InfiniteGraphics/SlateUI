package top.huliawsl.slateui.platform;

import java.nio.file.Path;
import java.util.Optional;
import net.fabricmc.loader.api.FabricLoader;
import top.huliawsl.slateui.platform.services.IPlatformHelper;
import top.huliawsl.slateui.platform.services.SlatePlatformFeature;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public LoaderId loaderId() {
        return LoaderId.FABRIC;
    }

    @Override
    public String minecraftVersion() {
        return FabricLoader.getInstance()
            .getModContainer("minecraft")
            .map(container -> container.getMetadata().getVersion().getFriendlyString())
            .orElse(IPlatformHelper.super.minecraftVersion());
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public Optional<String> modVersion(String modId) {
        return FabricLoader.getInstance()
            .getModContainer(modId)
            .map(container -> container.getMetadata().getVersion().getFriendlyString());
    }

    @Override
    public Path gameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public Path configDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public boolean supports(SlatePlatformFeature feature) {
        return switch (feature) {
            case CONFIG_SCREEN, KEYBIND, CLIENT_TICK, CLIENT_RENDER, RELOAD_LISTENER, NETWORKING, MOD_MENU, DATA_DRIVEN_SCREENS, RESOURCE_OVERRIDE -> true;
        };
    }
}
