package top.huliawsl.slateui.authoring;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;
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
        CompileResult fastResult = runFastCompileSlate(projectRoot);
        if (fastResult.status() == CompileStatus.SUCCESS) {
            return 0;
        }
        if (fastResult.status() == CompileStatus.FAILED) {
            throw new CompileSlateExecutionException(
                "compileSlate failed during fast reload with exit code " + fastResult.exitCode()
                    + "\nProject root: " + projectRoot.getAbsolutePath()
                    + "\nCommand: " + fastResult.command()
                    + "\nOutput:\n" + summarizeOutput(fastResult.output())
            );
        }
        return runGradleCompileSlate(projectRoot);
    }

    private static int runGradleCompileSlate(File projectRoot) throws Exception {
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

    static CompileResult runFastCompileSlate(File projectRoot) throws Exception {
        File compilerClasses = new File(projectRoot, "slateui-authoring/build/classes/java/main");
        File compilerMainClass = new File(compilerClasses, "top/huliawsl/slateui/authoring/SlateCompilerCli.class");
        if (!compilerMainClass.isFile()) {
            return CompileResult.unavailable("SlateCompilerCli has not been compiled yet.");
        }

        File inputDir = new File(projectRoot, "common/src/main/slate");
        File outputDir = new File(projectRoot, "common/build/generated/resources/slateui");
        String classpath = fastCompilerClasspath(compilerClasses, new File(projectRoot, "slateui-authoring/build/resources/main"));
        String javaExecutable = javaExecutable();
        Process process = new ProcessBuilder(
            javaExecutable,
            "-cp",
            classpath,
            "top.huliawsl.slateui.authoring.SlateCompilerCli",
            inputDir.getAbsolutePath(),
            outputDir.getAbsolutePath()
        )
            .directory(projectRoot)
            .redirectErrorStream(true)
            .start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        int exitCode = process.waitFor();
        String command = new File(javaExecutable).getName() + " -cp <runtime> SlateCompilerCli";
        if (exitCode == 0) {
            return CompileResult.success(command, output);
        }
        if (isFastCompilerClasspathFailure(output)) {
            return CompileResult.unavailable(output);
        }
        return CompileResult.failed(exitCode, command, output);
    }

    private static String fastCompilerClasspath(File compilerClasses, File compilerResources) {
        StringJoiner joiner = new StringJoiner(File.pathSeparator);
        joiner.add(compilerClasses.getAbsolutePath());
        if (compilerResources.isDirectory()) {
            joiner.add(compilerResources.getAbsolutePath());
        }
        findGsonJar().ifPresent(path -> joiner.add(path.toAbsolutePath().toString()));
        String currentClasspath = System.getProperty("java.class.path", "");
        if (!currentClasspath.isBlank()) {
            joiner.add(currentClasspath);
        }
        return joiner.toString();
    }

    private static boolean isFastCompilerClasspathFailure(String output) {
        if (output == null) {
            return false;
        }
        return output.contains("Could not find or load main class")
            || output.contains("ClassNotFoundException")
            || output.contains("NoClassDefFoundError");
    }

    private static java.util.Optional<Path> findGsonJar() {
        for (Path cacheRoot : gradleCacheRoots()) {
            Path gsonRoot = cacheRoot.resolve(Path.of("modules-2", "files-2.1", "com.google.code.gson", "gson", "2.10.1"));
            if (!Files.isDirectory(gsonRoot)) {
                continue;
            }
            try (var paths = Files.find(gsonRoot, 3, (path, attributes) -> path.getFileName().toString().equals("gson-2.10.1.jar"))) {
                java.util.Optional<Path> jar = paths.findFirst();
                if (jar.isPresent()) {
                    return jar;
                }
            } catch (Exception ignored) {
            }
        }
        return java.util.Optional.empty();
    }

    private static List<Path> gradleCacheRoots() {
        String gradleUserHome = System.getenv("GRADLE_USER_HOME");
        Path userHome = Path.of(System.getProperty("user.home", "."));
        return List.of(
            gradleUserHome == null || gradleUserHome.isBlank() ? userHome.resolve(".gradle").resolve("caches") : Path.of(gradleUserHome).resolve("caches"),
            userHome.resolve(".gradle").resolve("caches"),
            Path.of("D:\\env\\gradle\\caches"),
            Path.of("D:\\ENV\\gradle\\caches")
        );
    }

    private static String javaExecutable() {
        String name = isWindows() ? "java.exe" : "java";
        return Path.of(System.getProperty("java.home"), "bin", name).toString();
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

    enum CompileStatus {
        SUCCESS,
        UNAVAILABLE,
        FAILED
    }

    record CompileResult(CompileStatus status, int exitCode, String command, String output) {

        static CompileResult success(String command, String output) {
            return new CompileResult(CompileStatus.SUCCESS, 0, command, output);
        }

        static CompileResult unavailable(String output) {
            return new CompileResult(CompileStatus.UNAVAILABLE, -1, "<fast compiler unavailable>", output);
        }

        static CompileResult failed(int exitCode, String command, String output) {
            return new CompileResult(CompileStatus.FAILED, exitCode, command, output);
        }
    }
}
