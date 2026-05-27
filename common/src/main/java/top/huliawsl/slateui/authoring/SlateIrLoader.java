package top.huliawsl.slateui.authoring;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SlateIrLoader {

    private static final Gson GSON = new Gson();
    private static final Map<String, JsonObject> CACHE = new ConcurrentHashMap<>();

    private SlateIrLoader() {
    }

    public static boolean resourceExists(String resourcePath) {
        return SlateIrLoader.class.getClassLoader().getResource(resourcePath) != null || resolveLocalResource(resourcePath) != null;
    }

    public static JsonObject load(String resourcePath) {
        return CACHE.computeIfAbsent(resourcePath, SlateIrLoader::readResource);
    }

    public static void clearCache() {
        CACHE.clear();
    }

    private static JsonObject readResource(String resourcePath) {
        InputStream stream = SlateIrLoader.class.getClassLoader().getResourceAsStream(resourcePath);
        if (stream != null) {
            try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                return GSON.fromJson(reader, JsonObject.class);
            } catch (Exception exception) {
                throw new IllegalStateException("Failed to load Slate IR resource: " + resourcePath, exception);
            }
        }

        Path localResource = resolveLocalResource(resourcePath);
        if (localResource != null) {
            return loadFromFile(localResource, resourcePath);
        }

        throw new IllegalStateException("Missing Slate IR resource: " + resourcePath + ". Run compileSlate before opening the authoring screen.");
    }

    static JsonObject loadFromFile(Path path, String resourcePath) {
        try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8)) {
            return GSON.fromJson(reader, JsonObject.class);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to load Slate IR resource: " + resourcePath, exception);
        }
    }

    private static Path resolveLocalResource(String resourcePath) {
        for (Path root : List.of(
            Paths.get("common", "build", "generated", "resources"),
            Paths.get("common", "build", "resources", "main"),
            Paths.get("build", "generated", "resources"),
            Paths.get("build", "resources", "main")
        )) {
            Path candidate = root.resolve(resourcePath);
            if (Files.exists(candidate)) {
                return candidate;
            }
        }
        return null;
    }
}
