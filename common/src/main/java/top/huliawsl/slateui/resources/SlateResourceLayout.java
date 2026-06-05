package top.huliawsl.slateui.resources;

import java.util.Arrays;
import java.util.List;

public final class SlateResourceLayout {

    private SlateResourceLayout() {
    }

    public static String baseDirectory(String namespace) {
        return "assets/" + namespace + "/slate";
    }

    public static String directory(String namespace, SlateResourceKind kind) {
        return baseDirectory(namespace) + "/" + kind.directory();
    }

    public static List<String> requiredDirectories(String namespace) {
        return Arrays.stream(SlateResourceKind.values())
            .map(kind -> directory(namespace, kind))
            .toList();
    }

    public static SlateResourceLocation screen(String namespace, String path) {
        return new SlateResourceLocation(namespace, SlateResourceKind.SCREEN, path);
    }

    public static SlateResourceLocation theme(String namespace, String path) {
        return new SlateResourceLocation(namespace, SlateResourceKind.THEME, path);
    }

    public static SlateResourceLocation component(String namespace, String path) {
        return new SlateResourceLocation(namespace, SlateResourceKind.COMPONENT, path);
    }
}
