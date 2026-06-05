package top.huliawsl.slateui.platform;

import java.nio.file.Path;
import java.util.Optional;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import top.huliawsl.slateui.platform.services.IPlatformHelper;
import top.huliawsl.slateui.platform.services.SlatePlatformFeature;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public LoaderId loaderId() {
        return LoaderId.FORGE;
    }

    @Override
    public String minecraftVersion() {
        return FMLLoader.versionInfo().mcVersion();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public Optional<String> modVersion(String modId) {
        return Optional.ofNullable(ModList.get().getModContainerById(modId).orElse(null))
            .map(container -> container.getModInfo().getVersion().toString());
    }

    @Override
    public Path gameDir() {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public Path configDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean supports(SlatePlatformFeature feature) {
        return switch (feature) {
            case CONFIG_SCREEN, KEYBIND, CLIENT_TICK, CLIENT_RENDER, RELOAD_LISTENER, NETWORKING, DATA_DRIVEN_SCREENS, RESOURCE_OVERRIDE -> true;
            case MOD_MENU -> false;
        };
    }
}
