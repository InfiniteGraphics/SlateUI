package top.huliawsl.slateui.platform.artifact;

import java.util.ArrayList;
import java.util.List;
import top.huliawsl.slateui.platform.LoaderVersionSupport;
import top.huliawsl.slateui.platform.SlateCompatibilityMatrix;

public record SlatePublicationPlan(List<SlateArtifactCoordinates> artifacts) {

    public SlatePublicationPlan {
        artifacts = artifacts == null ? List.of() : List.copyOf(artifacts);
    }

    public static SlatePublicationPlan fromCompatibilityMatrix(String group, String version, SlateCompatibilityMatrix matrix) {
        List<SlateArtifactCoordinates> artifacts = new ArrayList<>();
        artifacts.add(SlateArtifactCoordinates.core(group, version));
        artifacts.add(SlateArtifactCoordinates.bom(group, version));
        for (LoaderVersionSupport support : matrix.runtimeAllowedEntries()) {
            artifacts.add(SlateArtifactCoordinates.loaderArtifact(group, support.loader(), support.minecraftRange().minInclusive(), version));
        }
        return new SlatePublicationPlan(artifacts);
    }

    public String asMarkdownList() {
        return artifacts.stream()
            .map(artifact -> "- `" + artifact.gav() + "`")
            .reduce((left, right) -> left + "\n" + right)
            .orElse("- <none>");
    }
}
