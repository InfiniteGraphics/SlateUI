package top.huliawsl.slateui.platform;

public record LoaderVersionSupport(LoaderId loader, MinecraftVersionRange minecraftRange, SupportLevel level, String note) {

    public boolean matches(LoaderId requestedLoader, String minecraftVersion) {
        return loader == requestedLoader && minecraftRange.contains(minecraftVersion);
    }
}
