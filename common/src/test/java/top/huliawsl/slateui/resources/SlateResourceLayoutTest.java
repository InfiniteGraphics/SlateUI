package top.huliawsl.slateui.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class SlateResourceLayoutTest {

    @Test
    void buildsCanonicalAssetPaths() {
        assertEquals("assets/demo/slate/screens/settings.slate", SlateResourceLayout.screen("demo", "settings").assetPath());
        assertEquals("assets/demo/slate/themes/dark.json", SlateResourceLayout.theme("demo", "dark.json").assetPath());
    }

    @Test
    void rejectsPathTraversal() {
        assertThrows(IllegalArgumentException.class, () -> SlateResourceLayout.screen("demo", "../bad"));
    }
}
