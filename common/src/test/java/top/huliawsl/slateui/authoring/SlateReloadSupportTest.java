package top.huliawsl.slateui.authoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import net.minecraft.network.chat.Component;
import top.huliawsl.slateui.api.SlateScreen;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StateProvider;
import top.huliawsl.slateui.api.Theme;
import top.huliawsl.slateui.api.component.Box;
import top.huliawsl.slateui.command.SlateCommandRegistry;

class SlateReloadSupportTest {

    @TempDir
    java.nio.file.Path tempDir;

    @Test
    void failedCompileStopsBeforeCacheClearAndScreenCreate() {
        AtomicBoolean cacheCleared = new AtomicBoolean(false);
        AtomicBoolean screenCreated = new AtomicBoolean(false);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> SlateReloadSupport.reloadScreen(
            "slateui/gallery.json",
            Component.literal("Test"),
            new SlateCommandRegistry(),
            StateProvider.EMPTY,
            Theme.DEFAULT,
            false,
            () -> 7,
            (resourcePath, title, commands, provider, theme, debugEnabled) -> {
                screenCreated.set(true);
                return createStubScreen();
            },
            () -> cacheCleared.set(true)
        ));

        assertTrue(exception.getMessage().contains("compileSlate failed"));
        assertTrue(exception.getMessage().contains("7"));
        assertFalse(cacheCleared.get());
        assertFalse(screenCreated.get());
    }

    @Test
    void successfulCompileClearsCacheAndBuildsScreen() {
        AtomicBoolean cacheCleared = new AtomicBoolean(false);
        SlateScreen expected = createStubScreen();

        SlateScreen actual = SlateReloadSupport.reloadScreen(
            "slateui/gallery.json",
            Component.literal("Test"),
            new SlateCommandRegistry(),
            StateProvider.EMPTY,
            Theme.DEFAULT,
            false,
            () -> 0,
            (resourcePath, title, commands, provider, theme, debugEnabled) -> expected,
            () -> cacheCleared.set(true)
        );

        assertTrue(cacheCleared.get());
        assertSame(expected, actual);
    }

    @Test
    void compileInvokerExceptionIsWrappedAsReloadFailure() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> SlateReloadSupport.reloadScreen(
            "slateui/gallery.json",
            Component.literal("Test"),
            new SlateCommandRegistry(),
            StateProvider.EMPTY,
            Theme.DEFAULT,
            false,
            () -> {
                throw new IllegalStateException("boom");
            },
            (resourcePath, title, commands, provider, theme, debugEnabled) -> createStubScreen(),
            () -> {
            }
        ));

        assertTrue(exception.getMessage().contains("compileSlate failed during reload"));
        assertEquals("boom", exception.getCause().getMessage());
    }

    @Test
    void projectRootCanBeFoundFromNestedIdeaWorkingDirectory() throws Exception {
        java.nio.file.Path root = tempDir.resolve("SlateUI");
        java.nio.file.Files.createDirectories(root.resolve("common/build/classes/java/main"));
        java.nio.file.Files.writeString(root.resolve("settings.gradle"), "rootProject.name='SlateUI'");
        java.nio.file.Files.writeString(root.resolve("gradlew.bat"), "@echo off");

        assertEquals(
            root.toFile().getAbsoluteFile(),
            SlateReloadSupport.findProjectRoot(root.resolve("common/build/classes/java/main").toFile())
        );
    }

    @Test
    void projectRootSearchReturnsNullWhenWrapperIsMissing() throws Exception {
        java.nio.file.Path root = tempDir.resolve("SlateUI");
        java.nio.file.Files.createDirectories(root.resolve("common"));
        java.nio.file.Files.writeString(root.resolve("settings.gradle"), "rootProject.name='SlateUI'");

        assertNull(SlateReloadSupport.findProjectRoot(root.resolve("common").toFile()));
    }

    private static SlateScreen createStubScreen() {
        return new SlateScreen(
            Component.literal("Stub"),
            new Box(List.of(), SlateStyle.EMPTY),
            new SlateCommandRegistry(),
            StateProvider.EMPTY,
            Theme.DEFAULT,
            false
        );
    }
}
