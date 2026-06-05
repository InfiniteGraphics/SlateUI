package top.huliawsl.slateui.platform.services;

import java.nio.file.Path;
import top.huliawsl.slateui.platform.LoaderId;
import top.huliawsl.slateui.platform.LoaderVersionSupport;
import top.huliawsl.slateui.platform.SlateCompatibilityMatrix;

public record SlatePlatformSnapshot(
    LoaderId loader,
    String platformName,
    String minecraftVersion,
    boolean developmentEnvironment,
    Path gameDir,
    Path configDir,
    LoaderVersionSupport compatibility
) {
    public SlatePlatformSnapshot {
        loader = loader == null ? LoaderId.UNKNOWN : loader;
        platformName = platformName == null || platformName.isBlank() ? loader.name() : platformName;
        minecraftVersion = minecraftVersion == null || minecraftVersion.isBlank() ? "unknown" : minecraftVersion;
        gameDir = gameDir == null ? Path.of(".") : gameDir;
        configDir = configDir == null ? gameDir.resolve("config") : configDir;
        if (compatibility == null) {
            compatibility = SlateCompatibilityMatrix.current().resolve(loader, minecraftVersion);
        }
    }

    public boolean supported() {
        return compatibility.level().runtimeAllowed();
    }
}
