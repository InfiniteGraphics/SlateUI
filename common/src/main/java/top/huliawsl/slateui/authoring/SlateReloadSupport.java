package top.huliawsl.slateui.authoring;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import top.huliawsl.slateui.api.SlateScreen;
import top.huliawsl.slateui.api.StateProvider;
import top.huliawsl.slateui.api.Theme;
import top.huliawsl.slateui.command.SlateCommandRegistry;

public final class SlateReloadSupport {

    @FunctionalInterface
    interface CompileSlateInvoker {
        int run() throws Exception;
    }

    @FunctionalInterface
    interface ScreenCreator {
        SlateScreen create(String resourcePath, Component title, SlateCommandRegistry commands, StateProvider provider, Theme theme, boolean debugEnabled);
    }

    private SlateReloadSupport() {
    }

    public static void reload(String resourcePath, Component title, SlateCommandRegistry commands, StateProvider provider, Theme theme, boolean debugEnabled) {
        SlateScreen screen = reloadScreen(
            resourcePath,
            title,
            commands,
            provider,
            theme,
            debugEnabled,
            SlateReloadSupport::runCompileSlate,
            (path, nextTitle, nextCommands, nextProvider, nextTheme, nextDebugEnabled) ->
                new SlateIrRuntimeFactory().createScreen(nextTitle, path, nextCommands, nextProvider, nextTheme, nextDebugEnabled),
            SlateIrLoader::clearCache
        );
        Minecraft.getInstance().setScreen(screen);
    }

    static SlateScreen reloadScreen(
        String resourcePath,
        Component title,
        SlateCommandRegistry commands,
        StateProvider provider,
        Theme theme,
        boolean debugEnabled,
        CompileSlateInvoker compileSlateInvoker,
        ScreenCreator screenCreator,
        Runnable clearCache
    ) {
        int exitCode = invokeCompileSlate(compileSlateInvoker);
        if (exitCode != 0) {
            throw new IllegalStateException("compileSlate failed during reload with exit code " + exitCode);
        }
        clearCache.run();
        return screenCreator.create(resourcePath, title, commands, provider, theme, debugEnabled);
    }

    private static int invokeCompileSlate(CompileSlateInvoker compileSlateInvoker) {
        try {
            return compileSlateInvoker.run();
        } catch (CompileSlateExecutionException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalStateException("compileSlate failed during reload", exception);
        }
    }

    private static int runCompileSlate() throws Exception {
        File projectRoot = findProjectRoot();
        String wrapperName = isWindows() ? "gradlew.bat" : "gradlew";
        File wrapper = new File(projectRoot, wrapperName);
        ProcessBuilder builder = isWindows()
            ? new ProcessBuilder("cmd", "/c", wrapperName, "compileSlate")
            : new ProcessBuilder("./" + wrapperName, "compileSlate");
        Process process = builder
            .directory(projectRoot)
            .redirectErrorStream(true)
            .start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new CompileSlateExecutionException(
                "compileSlate failed during reload with exit code " + exitCode
                    + "\nProject root: " + projectRoot.getAbsolutePath()
                    + "\nCommand: " + wrapper.getName() + " compileSlate"
                    + "\nOutput:\n" + summarizeOutput(output)
            );
        }
        return 0;
    }

    static File findProjectRoot() {
        File fromUserDir = findProjectRoot(new File(System.getProperty("user.dir", ".")));
        if (fromUserDir != null) {
            return fromUserDir;
        }
        try {
            Path codeSource = Path.of(SlateReloadSupport.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File fromCodeSource = findProjectRoot(codeSource.toFile());
            if (fromCodeSource != null) {
                return fromCodeSource;
            }
        } catch (Exception ignored) {
        }
        throw new CompileSlateExecutionException("Unable to locate project root for compileSlate. Run from a checkout that contains gradlew.bat and settings.gradle.");
    }

    static File findProjectRoot(File start) {
        File cursor = start == null ? null : start.getAbsoluteFile();
        if (cursor != null && cursor.isFile()) {
            cursor = cursor.getParentFile();
        }
        while (cursor != null) {
            if (isProjectRoot(cursor)) {
                return cursor;
            }
            cursor = cursor.getParentFile();
        }
        return null;
    }

    private static boolean isProjectRoot(File directory) {
        return new File(directory, "settings.gradle").isFile()
            && (new File(directory, "gradlew.bat").isFile() || new File(directory, "gradlew").isFile());
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
    }

    private static String summarizeOutput(String output) {
        if (output == null || output.isBlank()) {
            return "<empty>";
        }
        String[] lines = output.strip().split("\\R");
        int start = Math.max(0, lines.length - 30);
        return String.join("\n", Arrays.copyOfRange(lines, start, lines.length));
    }

    private static final class CompileSlateExecutionException extends IllegalStateException {

        private CompileSlateExecutionException(String message) {
            super(message);
        }
    }
}
