package top.huliawsl.slateui.platform.services;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import top.huliawsl.slateui.platform.LoaderId;
import top.huliawsl.slateui.platform.SlateCompatibilityMatrix;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform.
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    default LoaderId loaderId() {
        return LoaderId.fromPlatformName(getPlatformName());
    }

    default String minecraftVersion() {
        return System.getProperty("slateui.minecraftVersion", "unknown");
    }

    default boolean isModLoaded(String modId) {
        return false;
    }

    default Optional<String> modVersion(String modId) {
        return Optional.empty();
    }

    default Path gameDir() {
        return Path.of(".").toAbsolutePath().normalize();
    }

    default Path configDir() {
        return gameDir().resolve("config");
    }

    default boolean supports(SlatePlatformFeature feature) {
        return switch (feature) {
            case CONFIG_SCREEN, DATA_DRIVEN_SCREENS, RESOURCE_OVERRIDE -> true;
            default -> false;
        };
    }

    default void registerConfigScreen(String modId, SlateConfigScreenProvider provider) {
        SlatePlatformRegistry.global().registerConfigScreen(modId, provider);
    }

    default void openConfigScreen(String modId, Object parentScreen) {
        SlatePlatformRegistry.global().configScreen(modId).ifPresent(provider -> provider.create(parentScreen));
    }

    default void registerKeybind(SlateKeybindRegistration registration) {
        SlatePlatformRegistry.global().registerKeybind(registration);
    }

    default void registerClientReloadListener(String id, SlateReloadListener listener) {
        SlatePlatformRegistry.global().registerReloadListener(id, listener);
    }

    default void registerClientTick(String id, SlateTickListener listener) {
        SlatePlatformRegistry.global().registerTickListener(id, listener);
    }

    default void registerClientRenderHook(String id, SlateRenderHook hook) {
        SlatePlatformRegistry.global().registerRenderHook(id, hook);
    }

    default SlateNetworkingBridge networking() {
        return SlateNetworkingBridge.NOOP;
    }

    default void sendToServer(String channel, Map<String, Object> payload) {
        networking().sendToServer(channel, payload);
    }

    default SlatePlatformSnapshot snapshot() {
        return new SlatePlatformSnapshot(
            loaderId(),
            getPlatformName(),
            minecraftVersion(),
            isDevelopmentEnvironment(),
            gameDir(),
            configDir(),
            SlateCompatibilityMatrix.current().resolve(loaderId(), minecraftVersion())
        );
    }
}
