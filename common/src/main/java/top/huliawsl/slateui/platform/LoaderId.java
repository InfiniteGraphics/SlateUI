package top.huliawsl.slateui.platform;

import java.util.Locale;

public enum LoaderId {
    FABRIC,
    NEOFORGE,
    FORGE,
    UNKNOWN;

    public static LoaderId fromPlatformName(String platformName) {
        if (platformName == null || platformName.isBlank()) {
            return UNKNOWN;
        }
        String normalized = platformName.trim().toLowerCase(Locale.ROOT);
        if (normalized.contains("fabric")) {
            return FABRIC;
        }
        if (normalized.contains("neoforge") || normalized.contains("neo forge")) {
            return NEOFORGE;
        }
        if (normalized.contains("forge")) {
            return FORGE;
        }
        return UNKNOWN;
    }

    public String artifactSuffix() {
        return switch (this) {
            case FABRIC -> "fabric";
            case NEOFORGE -> "neoforge";
            case FORGE -> "forge";
            case UNKNOWN -> "unknown";
        };
    }
}
