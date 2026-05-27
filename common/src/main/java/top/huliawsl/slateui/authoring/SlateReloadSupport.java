package top.huliawsl.slateui.authoring;

import java.io.File;
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
        } catch (Exception exception) {
            throw new IllegalStateException("compileSlate failed during reload", exception);
        }
    }

    private static int runCompileSlate() throws Exception {
        String command = new File("gradlew.bat").exists() ? "gradlew.bat compileSlate" : "./gradlew compileSlate";
        Process process = new ProcessBuilder("cmd", "/c", command)
            .directory(new File("."))
            .inheritIO()
            .start();
        return process.waitFor();
    }
}
