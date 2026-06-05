package top.huliawsl.slateui.platform.artifact;

import java.util.Objects;
import top.huliawsl.slateui.platform.LoaderId;

public record SlateArtifactCoordinates(String group, String module, String version) {

    public SlateArtifactCoordinates {
        group = group == null || group.isBlank() ? "top.huliawsl" : group;
        module = Objects.requireNonNull(module, "module");
        version = version == null || version.isBlank() ? "unspecified" : version;
    }

    public static SlateArtifactCoordinates loaderArtifact(String group, LoaderId loader, String minecraftLine, String version) {
        String normalizedLine = minecraftLine == null || minecraftLine.isBlank()
            ? "unknown"
            : minecraftLine.replace('.', '_').replace('-', '_');
        String suffix = loader == null ? LoaderId.UNKNOWN.artifactSuffix() : loader.artifactSuffix();
        return new SlateArtifactCoordinates(group, "slateui-" + suffix + "-mc" + normalizedLine, version);
    }

    public static SlateArtifactCoordinates core(String group, String version) {
        return new SlateArtifactCoordinates(group, "slateui-core", version);
    }

    public static SlateArtifactCoordinates bom(String group, String version) {
        return new SlateArtifactCoordinates(group, "slateui-bom", version);
    }

    public String gav() {
        return group + ":" + module + ":" + version;
    }
}
