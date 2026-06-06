package top.huliawsl.slateui.platform;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class SlateCompatibilityMatrix {

    private final List<LoaderVersionSupport> entries;

    public SlateCompatibilityMatrix(List<LoaderVersionSupport> entries) {
        this.entries = entries == null ? List.of() : List.copyOf(entries);
    }

    public static SlateCompatibilityMatrix current() {
        return new SlateCompatibilityMatrix(List.of(
            new LoaderVersionSupport(LoaderId.FABRIC, MinecraftVersionRange.of("1.21", "1.22"), SupportLevel.SUPPORTED, "Primary high-version Fabric target."),
            new LoaderVersionSupport(LoaderId.NEOFORGE, MinecraftVersionRange.of("1.21", "1.22"), SupportLevel.SUPPORTED, "Primary high-version NeoForge target."),
            new LoaderVersionSupport(LoaderId.FORGE, MinecraftVersionRange.of("1.21", "1.22"), SupportLevel.EXPERIMENTAL, "Forge 1.21.x is compile/runtime experimental."),
            new LoaderVersionSupport(LoaderId.FABRIC, MinecraftVersionRange.of("1.20.1", "1.20.2"), SupportLevel.EXPERIMENTAL, "Backport target for mods that need a 1.20.1 bridge."),
            new LoaderVersionSupport(LoaderId.FORGE, MinecraftVersionRange.of("1.20.1", "1.20.2"), SupportLevel.EXPERIMENTAL, "Backport target for legacy Forge ecosystems."),
            new LoaderVersionSupport(LoaderId.NEOFORGE, MinecraftVersionRange.of("1.20.1", "1.20.2"), SupportLevel.CONSIDERED, "Tracked only for API-contract checks."
            )
        ));
    }

    public static SlateCompatibilityMatrix mvp4() {
        return new SlateCompatibilityMatrix(List.of(
            new LoaderVersionSupport(LoaderId.FABRIC, MinecraftVersionRange.of("1.21", "1.22"), SupportLevel.SUPPORTED, "Primary high-version Fabric target."),
            new LoaderVersionSupport(LoaderId.NEOFORGE, MinecraftVersionRange.of("1.21", "1.22"), SupportLevel.SUPPORTED, "Primary high-version NeoForge target."),
            new LoaderVersionSupport(LoaderId.FORGE, MinecraftVersionRange.of("1.21", "1.22"), SupportLevel.EXPERIMENTAL, "Forge 1.21.x is tracked as experimental and is not part of the default stable CI lane."),
            new LoaderVersionSupport(LoaderId.FABRIC, MinecraftVersionRange.of("1.20.1", "1.20.2"), SupportLevel.EXPERIMENTAL, "Backport target for mods that need a 1.20.1 bridge."),
            new LoaderVersionSupport(LoaderId.FORGE, MinecraftVersionRange.of("1.20.1", "1.20.2"), SupportLevel.CONSIDERED, "Tracked for API-contract checks; compile smoke belongs to the explicit matrix workflow."),
            new LoaderVersionSupport(LoaderId.NEOFORGE, MinecraftVersionRange.of("1.20.1", "1.20.2"), SupportLevel.UNSUPPORTED, "NeoForge 1.20.1 is outside the SlateUI MVP4 runtime target.")
        ));
    }

    public List<LoaderVersionSupport> entries() {
        return entries;
    }

    public List<LoaderVersionSupport> runtimeAllowedEntries() {
        return entries.stream().filter(entry -> entry.level().runtimeAllowed()).toList();
    }

    public LoaderVersionSupport resolve(LoaderId loader, String minecraftVersion) {
        LoaderId requestedLoader = loader == null ? LoaderId.UNKNOWN : loader;
        return entries.stream()
            .filter(entry -> entry.matches(requestedLoader, minecraftVersion))
            .findFirst()
            .orElse(new LoaderVersionSupport(requestedLoader, MinecraftVersionRange.of("0", "999"), SupportLevel.UNSUPPORTED, "No matching SlateUI support entry."));
    }

    public LoaderVersionSupport requireRuntimeAllowed(LoaderId loader, String minecraftVersion) {
        LoaderVersionSupport support = resolve(loader, minecraftVersion);
        if (!support.level().runtimeAllowed()) {
            throw new IllegalStateException("Unsupported SlateUI runtime combination: " + loader + " " + minecraftVersion + " - " + support.note());
        }
        return support;
    }

    public String asMarkdownTable() {
        String header = "| Loader | Minecraft range | Level | Note |\n|---|---|---|---|";
        String body = entries.stream()
            .sorted(Comparator.comparing((LoaderVersionSupport entry) -> entry.loader().name()).thenComparing(entry -> entry.minecraftRange().minInclusive()))
            .map(entry -> "| " + entry.loader() + " | " + entry.minecraftRange().minInclusive() + " <= mc < " + entry.minecraftRange().maxExclusive() + " | " + entry.level() + " | " + sanitize(entry.note()) + " |")
            .collect(Collectors.joining("\n"));
        return header + (body.isBlank() ? "" : "\n" + body);
    }

    private static String sanitize(String text) {
        return Objects.toString(text, "").replace("|", "\\|").replace('\n', ' ');
    }
}
