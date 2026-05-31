package top.huliawsl.slateui.release;

import java.util.List;

public final class SlateReleaseQualityBar {

    private SlateReleaseQualityBar() {
    }

    public static List<SlateReleaseCheck> v1RequiredChecks() {
        return List.of(
            new SlateReleaseCheck("ci.clean-build", "CI runs Gradle build and loader smoke jobs.", true),
            new SlateReleaseCheck("tests.unit", "Unit tests pass.", true),
            new SlateReleaseCheck("tests.integration", "Runtime and authoring integration tests pass.", true),
            new SlateReleaseCheck("smoke.fabric", "Fabric smoke build passes.", true),
            new SlateReleaseCheck("smoke.forge", "Forge smoke build passes.", true),
            new SlateReleaseCheck("smoke.neoforge", "NeoForge smoke build passes.", true),
            new SlateReleaseCheck("examples.build", "Example sources compile with the main project.", true),
            new SlateReleaseCheck("examples.launch", "Example launch is documented as a manual client check.", true),
            new SlateReleaseCheck("lifecycle.memory", "Screen lifecycle removes listeners and disposes component trees.", true),
            new SlateReleaseCheck("renderer.supported", "Renderer dispatch is covered by adapter tests.", true),
            new SlateReleaseCheck("authoring.strict", ".slate compiler rejects unknown or ignored syntax.", true),
            new SlateReleaseCheck("docs.readme", "README documents stable and experimental scope.", true),
            new SlateReleaseCheck("docs.architecture", "Architecture docs define runtime boundaries.", true),
            new SlateReleaseCheck("docs.migration", "Migration policy is documented.", true),
            new SlateReleaseCheck("license.final", "MIT license is finalized.", true)
        );
    }
}
