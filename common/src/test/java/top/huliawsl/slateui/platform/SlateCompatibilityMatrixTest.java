package top.huliawsl.slateui.platform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SlateCompatibilityMatrixTest {

    @Test
    void resolvesSupportedHighVersionFabricLane() {
        LoaderVersionSupport support = SlateCompatibilityMatrix.current().resolve(LoaderId.FABRIC, "1.21.4");
        assertEquals(SupportLevel.SUPPORTED, support.level());
    }

    @Test
    void resolvesExperimentalBackportLane() {
        LoaderVersionSupport support = SlateCompatibilityMatrix.current().resolve(LoaderId.FORGE, "1.20.1");
        assertEquals(SupportLevel.EXPERIMENTAL, support.level());
        assertTrue(support.level().runtimeAllowed());
    }

    @Test
    void rejectsUnsupportedUnknownLane() {
        assertThrows(IllegalStateException.class, () -> SlateCompatibilityMatrix.current().requireRuntimeAllowed(LoaderId.UNKNOWN, "1.19.4"));
    }

    @Test
    void versionParserHandlesReleaseCandidates() {
        assertTrue(new MinecraftVersionRange("1.21", "1.22").contains("1.21.1-rc1"));
    }
}
