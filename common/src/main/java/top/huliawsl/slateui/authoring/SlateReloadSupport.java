package top.huliawsl.slateui.authoring;

import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import top.huliawsl.slateui.api.SlateScreen;
import top.huliawsl.slateui.api.StateProvider;
import top.huliawsl.slateui.api.Theme;
import top.huliawsl.slateui.command.SlateCommandRegistry;

public final class SlateReloadSupport {

    private SlateReloadSupport() {
    }

    public static void reload(String resourcePath, Component title, SlateCommandRegistry commands, StateProvider provider, Theme theme, boolean debugEnabled) {
        runCompileSlate();
        SlateIrLoader.clearCache();
        SlateScreen screen = new SlateIrRuntimeFactory().createScreen(title, resourcePath, commands, provider, theme, debugEnabled);
        Minecraft.getInstance().setScreen(screen);
    }

    private static void runCompileSlate() {
        try {
            String command = new File("gradlew.bat").exists() ? "gradlew.bat compileSlate" : "./gradlew compileSlate";
            Process process = new ProcessBuilder("cmd", "/c", command)
                .directory(new File("."))
                .inheritIO()
                .start();
            process.waitFor();
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to recompile Slate templates", exception);
        }
    }
}