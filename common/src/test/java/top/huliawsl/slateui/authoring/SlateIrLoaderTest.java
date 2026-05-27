package top.huliawsl.slateui.authoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SlateIrLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    void loadsLocalJsonResourceFromFileSystem() throws Exception {
        Path resource = tempDir.resolve("gallery.json");
        Files.writeString(resource, "{\"componentType\":\"Box\"}", StandardCharsets.UTF_8);

        JsonObject json = SlateIrLoader.loadFromFile(resource, "slateui/gallery.json");

        assertEquals("Box", json.get("componentType").getAsString());
    }

    @Test
    void resolvesGeneratedResourceFromNestedIdeaWorkingDirectory() throws Exception {
        Path resource = tempDir.resolve(Path.of("common", "build", "generated", "resources", "slateui", "gallery.json"));
        Files.createDirectories(resource.getParent());
        Files.writeString(resource, "{\"componentType\":\"Stack\"}", StandardCharsets.UTF_8);

        Path resolved = SlateIrLoader.resolveLocalResource(tempDir.resolve(Path.of("fabric", "run")), "slateui/gallery.json");

        assertEquals(resource, resolved);
    }

    @Test
    void loadsGeneratedGalleryResourceThroughLoader() {
        JsonObject json = SlateIrLoader.load("slateui/gallery.json");

        assertNotNull(json.getAsJsonObject("root").get("componentType"));
    }

    @Test
    void missingResourceReportsCompileSlateHint() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
            SlateIrLoader.load("slateui/missing-test-resource.json")
        );

        assertEquals(
            "Missing Slate IR resource: slateui/missing-test-resource.json. Run compileSlate before opening the authoring screen.",
            exception.getMessage()
        );
    }
}
