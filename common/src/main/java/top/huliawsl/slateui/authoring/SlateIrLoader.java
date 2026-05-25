package top.huliawsl.slateui.authoring;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SlateIrLoader {

    private static final Gson GSON = new Gson();
    private static final Map<String, JsonObject> CACHE = new ConcurrentHashMap<>();

    private SlateIrLoader() {
    }

    public static boolean resourceExists(String resourcePath) {
        return SlateIrLoader.class.getClassLoader().getResource(resourcePath) != null;
    }

    public static JsonObject load(String resourcePath) {
        return CACHE.computeIfAbsent(resourcePath, SlateIrLoader::readResource);
    }

    public static void clearCache() {
        CACHE.clear();
    }

    private static JsonObject readResource(String resourcePath) {
        InputStream stream = SlateIrLoader.class.getClassLoader().getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new IllegalStateException("Missing Slate IR resource: " + resourcePath + ". Run compileSlate before opening the authoring screen.");
        }
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return GSON.fromJson(reader, JsonObject.class);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to load Slate IR resource: " + resourcePath, exception);
        }
    }
}
