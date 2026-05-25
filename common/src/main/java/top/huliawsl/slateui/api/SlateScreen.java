package top.huliawsl.slateui.api;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import top.huliawsl.slateui.command.CommandContext;
import top.huliawsl.slateui.command.SlateCommandRegistry;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.MinecraftDrawCommandRenderer;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public class SlateScreen extends Screen {

    private final SlateComponent root;
    private final SlateCommandRegistry commands;
    private final boolean debugEnabled;
    private List<DrawCommand> drawCommands = List.of();

    public SlateScreen(Component title, SlateComponent root, SlateCommandRegistry commands, boolean debugEnabled) {
        super(title);
        this.root = root;
        this.commands = commands.copy().register("screen.close", context -> context.minecraft().setScreen(null));
        this.debugEnabled = debugEnabled;
    }

    @Override
    protected void init() {
        rebuildRuntime();
    }

    protected void rebuildRuntime() {
        SlateLayoutContext layoutContext = new SlateLayoutContext(font);
        Size available = new Size(width, height);
        root.measure(layoutContext, available);
        root.layout(layoutContext, new Rect(0, 0, width, height));
        List<DrawCommand> commands = new ArrayList<>();
        root.collectDrawCommands(new SlateRenderContext(debugEnabled), commands);
        this.drawCommands = List.copyOf(commands);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        MinecraftDrawCommandRenderer.render(guiGraphics, font, drawCommands);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        SlateInteractionContext context = new SlateInteractionContext(commands, new CommandContext(Minecraft.getInstance(), this));
        return root.mouseClicked(context, mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }

    public SlateComponent root() {
        return root;
    }
}
