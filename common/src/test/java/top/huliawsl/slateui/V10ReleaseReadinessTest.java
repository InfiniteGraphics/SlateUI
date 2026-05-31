package top.huliawsl.slateui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import top.huliawsl.slateui.api.SlateApiSurface;
import top.huliawsl.slateui.api.SlateExperimentalApi;
import top.huliawsl.slateui.api.SlateInternalApi;
import top.huliawsl.slateui.api.SlatePublicApi;
import top.huliawsl.slateui.api.ThemeTokens;
import top.huliawsl.slateui.platform.LoaderId;
import top.huliawsl.slateui.platform.SlateCompatibilityMatrix;
import top.huliawsl.slateui.platform.SupportLevel;
import top.huliawsl.slateui.release.SlateReleaseQualityBar;

class V10ReleaseReadinessTest {

    @Test
    void apiStatusMarkersAreAvailableWithoutExternalDependencies() {
        assertNotNull(SlatePublicApi.class.getAnnotation(java.lang.annotation.Retention.class));
        assertNotNull(SlateExperimentalApi.class.getAnnotation(java.lang.annotation.Retention.class));
        assertNotNull(SlateInternalApi.class.getAnnotation(java.lang.annotation.Retention.class));
    }

    @Test
    void stableApiSurfaceFreezesPackagesComponentsTokensAndModelVersions() {
        assertTrue(SlateApiSurface.publicPackages().contains("top.huliawsl.slateui.api.component"));
        assertTrue(SlateApiSurface.internalPackages().contains("top.huliawsl.slateui.mixin"));
        assertTrue(SlateApiSurface.experimentalPackages().contains("top.huliawsl.slateui.hud"));
        assertEquals(1, SlateApiSurface.AUTHORING_IR_VERSION);
        assertEquals(1, SlateApiSurface.COMMAND_MODEL_VERSION);
        assertTrue(SlateApiSurface.stableComponents().containsAll(List.of("Button", "Input", "ScrollView", "List")));

        ThemeTokens defaults = ThemeTokens.defaults();
        Set<String> defaultKeys = new java.util.HashSet<>();
        defaultKeys.addAll(defaults.colors().keySet());
        defaultKeys.addAll(defaults.spacing().keySet());
        defaultKeys.addAll(defaults.radii().keySet());
        assertEquals(defaultKeys, SlateApiSurface.frozenThemeTokens());
    }

    @Test
    void stableBinaryCompatibilitySurfaceKeepsExpectedConstructors() throws Exception {
        assertNotNull(Class.forName("top.huliawsl.slateui.api.component.Button")
            .getConstructor(String.class, String.class, top.huliawsl.slateui.api.SlateStyle.class));
        assertNotNull(Class.forName("top.huliawsl.slateui.api.component.Input")
            .getConstructor(String.class, String.class, String.class, top.huliawsl.slateui.api.SlateStyle.class));
        assertNotNull(Class.forName("top.huliawsl.slateui.command.SlateCommandRegistry")
            .getMethod("executeResult", String.class, top.huliawsl.slateui.command.CommandContext.class));
        assertNotNull(Class.forName("top.huliawsl.slateui.render.DrawCommandDispatcher")
            .getMethod("render", List.class, top.huliawsl.slateui.runtime.SlateRenderer.class));
    }

    @Test
    void compatibilityMatrixAndStableScopeAreFrozenForRelease() {
        SlateCompatibilityMatrix matrix = SlateCompatibilityMatrix.mvp4();

        assertEquals(SupportLevel.SUPPORTED, matrix.resolve(LoaderId.FABRIC, "1.21.1").level());
        assertEquals(SupportLevel.SUPPORTED, matrix.resolve(LoaderId.NEOFORGE, "1.21.1").level());
        assertEquals(SupportLevel.EXPERIMENTAL, matrix.resolve(LoaderId.FORGE, "1.21.1").level());
        assertTrue(SlateApiSurface.stableScope().contains("Screen runtime"));
        assertTrue(SlateApiSurface.experimentalScope().contains("HUD"));
    }

    @Test
    void releaseQualityBarAndDocsCoverRequiredChecks() throws Exception {
        assertEquals(15, SlateReleaseQualityBar.v1RequiredChecks().size());
        assertTrue(SlateReleaseQualityBar.v1RequiredChecks().stream().allMatch(check -> check.required()));
        assertTrue(Files.readString(root().resolve("README.md")).contains("docs/api-stability.md"));
        assertTrue(Files.readString(root().resolve("ARCHITECTURE.md")).contains("1.0 Stability Boundary"));
        assertTrue(Files.readString(root().resolve("MIGRATION.md")).contains("Migration Policy"));
        assertTrue(Files.readString(root().resolve("LICENSE")).contains("MIT License"));
    }

    @Test
    void ciContainsReleaseBuildAndSmokeTasks() throws Exception {
        String ci = Files.readString(root().resolve(Path.of(".github", "workflows", "ci.yml")));

        assertTrue(ci.contains("test"));
        assertTrue(ci.contains(":slateui-core:test"));
        assertTrue(ci.contains(":slateui-minecraft:compileJava"));
        assertTrue(ci.contains(":fabric:build"));
        assertTrue(ci.contains(":forge:build"));
        assertTrue(ci.contains(":neoforge:build"));
        assertFalse(ci.contains("TODO"));
    }

    private static Path root() {
        Path cwd = Path.of("").toAbsolutePath();
        return cwd.getFileName().toString().equals("common") ? cwd.getParent() : cwd;
    }
}
