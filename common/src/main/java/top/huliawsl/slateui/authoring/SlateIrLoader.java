package top.huliawsl.slateui.authoring;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        return resolveLocalResource(Paths.get("").toAbsolutePath(), resourcePath);
    }

    static Path resolveLocalResource(Path workingDirectory, String resourcePath) {
        for (Path root : candidateResourceRoots(workingDirectory)) {
            Path candidate = root.resolve(resourcePath);
            if (Files.exists(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private static List<Path> candidateResourceRoots(Path workingDirectory) {
        Set<Path> roots = new LinkedHashSet<>();
        for (Path base : candidateBaseDirectories(workingDirectory)) {
            Path current = base.toAbsolutePath().normalize();
            while (current != null) {
                roots.add(current.resolve(Paths.get("common", "build", "generated", "resources")));
                roots.add(current.resolve(Paths.get("common", "build", "resources", "main")));
                roots.add(current.resolve(Paths.get("build", "generated", "resources")));
                roots.add(current.resolve(Paths.get("build", "resources", "main")));
                current = current.getParent();
            }
        }
        return new ArrayList<>(roots);
    }

    private static List<Path> candidateBaseDirectories(Path workingDirectory) {
        List<Path> bases = new ArrayList<>();
        if (workingDirectory != null) {
            bases.add(workingDirectory);
        }
        Path codeSource = codeSourcePath();
        if (codeSource != null) {
            Path base = Files.isDirectory(codeSource) ? codeSource : codeSource.getParent();
            if (base != null) {
                bases.add(base);
            }
        }
        return bases;
    }

    private static Path codeSourcePath() {
        try {
            CodeSource codeSource = SlateIrLoader.class.getProtectionDomain().getCodeSource();
            if (codeSource == null || codeSource.getLocation() == null) {
                return null;
            }
            URI uri = codeSource.getLocation().toURI();
            return Paths.get(uri);
        } catch (Exception ignored) {
            return null;
        }
    }
}
