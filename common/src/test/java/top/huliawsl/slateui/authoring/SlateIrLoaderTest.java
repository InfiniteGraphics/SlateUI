package top.huliawsl.slateui.authoring;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
