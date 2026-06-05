package top.huliawsl.slateui.resources;

import java.util.Objects;

public record SlateResourceLocation(String namespace, SlateResourceKind kind, String path) {

    public SlateResourceLocation {
        namespace = sanitizeNamespace(namespace);
        kind = Objects.requireNonNull(kind, "kind");
        path = sanitizePath(path);
    }

    public static SlateResourceLocation parse(String id, SlateResourceKind defaultKind) {
        Objects.requireNonNull(defaultKind, "defaultKind");
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Slate resource id cannot be blank");
        }
        String[] split = id.split(":", 2);
        String namespace = split.length == 2 ? split[0] : "minecraft";
        String path = split.length == 2 ? split[1] : split[0];
        return new SlateResourceLocation(namespace, defaultKind, path);
    }

    public String assetPath() {
        String normalized = path.endsWith(kind.extension()) ? path : path + kind.extension();
        return "assets/" + namespace + "/slate/" + kind.directory() + "/" + normalized;
    }

    public String id() {
        return namespace + ":" + path;
    }

    private static String sanitizeNamespace(String namespace) {
        if (namespace == null || namespace.isBlank()) {
            throw new IllegalArgumentException("Slate resource namespace cannot be blank");
        }
        if (!namespace.matches("[a-z0-9_.-]+")) {
            throw new IllegalArgumentException("Invalid Slate resource namespace: " + namespace);
        }
        return namespace;
    }

    private static String sanitizePath(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Slate resource path cannot be blank");
        }
        String normalized = path.replace('\\', '/');
        if (normalized.startsWith("/") || normalized.contains("../") || normalized.contains("..\\") || normalized.contains("//")) {
            throw new IllegalArgumentException("Invalid Slate resource path: " + path);
        }
        return normalized;
    }
}
