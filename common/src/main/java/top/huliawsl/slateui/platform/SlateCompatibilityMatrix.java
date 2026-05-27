package top.huliawsl.slateui.platform;

import java.util.List;

public final class SlateCompatibilityMatrix {

    private final List<LoaderVersionSupport> entries;

    public SlateCompatibilityMatrix(List<LoaderVersionSupport> entries) {
        this.entries = entries == null ? List.of() : List.copyOf(entries);
    }

    public static SlateCompatibilityMatrix mvp4() {
        return new SlateCompatibilityMatrix(List.of(
            new LoaderVersionSupport(LoaderId.FABRIC, new MinecraftVersionRange("1.21", "1.22"), SupportLevel.SUPPORTED, "Fabric 1.21.x remains a first-class Screen target."),
            new LoaderVersionSupport(LoaderId.NEOFORGE, new MinecraftVersionRange("1.21", "1.22"), SupportLevel.SUPPORTED, "NeoForge 1.21.x remains a first-class Screen target."),
            new LoaderVersionSupport(LoaderId.FORGE, new MinecraftVersionRange("1.20.1", "1.20.2"), SupportLevel.CONSIDERED, "Forge 1.20.1 is tracked as an MVP4 compatibility consideration, not a promised runtime target."),
            new LoaderVersionSupport(LoaderId.FORGE, new MinecraftVersionRange("1.21", "1.22"), SupportLevel.EXPERIMENTAL, "Forge 1.21.x can compile against common abstractions when loader glue is available.")
        ));
    }

    public List<LoaderVersionSupport> entries() { return entries; }

    public LoaderVersionSupport resolve(LoaderId loader, String minecraftVersion) {
        return entries.stream()
            .filter(entry -> entry.matches(loader, minecraftVersion))
            .findFirst()
            .orElse(new LoaderVersionSupport(loader, new MinecraftVersionRange("0", "999"), SupportLevel.UNSUPPORTED, "No matching SlateUI support entry."));
    }
}
