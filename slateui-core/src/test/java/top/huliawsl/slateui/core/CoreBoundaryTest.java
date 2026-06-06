package top.huliawsl.slateui.core;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class CoreBoundaryTest {

    @Test
    void coreSourcesDoNotImportMinecraft() throws Exception {
        Path root = Path.of("..", "common", "src", "main", "java").normalize();
        try (var files = Files.walk(root)) {
            java.util.List<Path> offenders = files
                .filter(path -> path.toString().endsWith(".java"))
                .filter(path -> {
                    String text;
                    try {
                        text = Files.readString(path);
                    } catch (Exception exception) {
                        return true;
                    }
                    if (!text.contains("net.minecraft")) {
                        return false;
                    }
                    String normalized = path.toString().replace('\\', '/');
                    return normalized.contains("/api/component/")
                        || normalized.contains("/api/container/")
                        || normalized.contains("/binding/")
                        || normalized.contains("/layout/")
                        || normalized.contains("/override/")
                        || normalized.contains("/server/")
                        || normalized.endsWith("/command/CommandContext.java")
                        || normalized.endsWith("/command/SlateCommandRegistry.java")
                        || normalized.contains("/runtime/Slate")
                        || (normalized.contains("/render/")
                            && !normalized.contains("/Minecraft")
                            && !normalized.endsWith("/GuiGraphicsCompat.java"));
                })
                .toList();
            assertTrue(offenders.isEmpty(), offenders.toString());
        }
    }
}
