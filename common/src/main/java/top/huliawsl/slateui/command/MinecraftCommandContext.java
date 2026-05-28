package top.huliawsl.slateui.command;

import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import top.huliawsl.slateui.runtime.SlateHost;

public final class MinecraftCommandContext extends CommandContext {

    private final Minecraft minecraft;
    private final Screen screen;

    public MinecraftCommandContext(Minecraft minecraft, Screen screen, SlateHost host) {
        this(minecraft, screen, host, Map.of());
    }

    private MinecraftCommandContext(Minecraft minecraft, Screen screen, SlateHost host, Map<String, Object> payload) {
        super(host, payload);
        this.minecraft = minecraft;
        this.screen = screen;
    }

    public Minecraft minecraft() {
        return minecraft;
    }

    public Screen screen() {
        return screen;
    }

    @Override
    public MinecraftCommandContext withPayload(Map<String, Object> payload) {
        return new MinecraftCommandContext(minecraft, screen, host(), payload);
    }

    public static MinecraftCommandContext require(CommandContext context) {
        if (context instanceof MinecraftCommandContext minecraftContext) {
            return minecraftContext;
        }
        throw new IllegalStateException("Minecraft command context required");
    }
}
