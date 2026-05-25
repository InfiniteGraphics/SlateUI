package top.huliawsl.slateui.api;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import top.huliawsl.slateui.command.CommandContext;
import top.huliawsl.slateui.command.SlateCommandRegistry;
import top.huliawsl.slateui.debug.SlateDiagnostics;
import top.huliawsl.slateui.debug.SlateErrorScreen;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.MinecraftDrawCommandRenderer;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public class SlateScreen extends Screen {

    private static final int BACKGROUND_COLOR = 0xFF0B1220;

    private final SlateComponent root;
    private final SlateCommandRegistry commands;
    private final boolean debugEnabled;
    private final SlateDiagnostics diagnostics = new SlateDiagnostics();
    private final StateProvider stateProvider;
    private final Theme theme;
    private List<DrawCommand> drawCommands = List.of();
    private boolean runtimeDirty = true;
    private SlateComponent focusedComponent;

    public SlateScreen(Component title, SlateComponent root, SlateCommandRegistry commands, boolean debugEnabled) {
        this(title, root, commands, StateProvider.EMPTY, Theme.DEFAULT, debugEnabled);
    }

    public SlateScreen(Component title, SlateComponent root, SlateCommandRegistry commands, StateProvider stateProvider, Theme theme, boolean debugEnabled) {
        super(title);
        this.root = root;
        this.commands = commands.copy().register("screen.close", context -> context.minecraft().setScreen(null));
        this.stateProvider = stateProvider == null ? StateProvider.EMPTY : stateProvider;
        this.theme = theme == null ? Theme.DEFAULT : theme;
        this.debugEnabled = debugEnabled;
        this.stateProvider.addListener(path -> requestRebuild("state:" + path));
    }

    @Override
    protected void init() {
        rebuildRuntime();
    }

    @Override
    public void tick() {
        if (runtimeDirty) {
            rebuildRuntime();
        }
    }

    public void requestRebuild(String reason) {
        diagnostics.logDiagnostic("REBUILD " + reason);
        runtimeDirty = true;
    }

    protected void rebuildRuntime() {
        runtimeDirty = false;
        try {
            SlateLayoutContext layoutContext = new SlateLayoutContext(font);
            Size available = new Size(width, height);
            root.measure(layoutContext, available);
            root.layout(layoutContext, new Rect(0, 0, width, height));
            List<DrawCommand> commands = new ArrayList<>();
            root.collectDrawCommands(new SlateRenderContext(debugEnabled, theme), commands);
            this.drawCommands = List.copyOf(commands);
            diagnostics.capture(root, drawCommands, focusedComponent == null ? "<none>" : focusedComponent.debugName(), "<bindings logged at runtime>", dumpState());
        } catch (Throwable throwable) {
            openErrorScreen("rebuild", throwable);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        try {
            if (runtimeDirty) {
                rebuildRuntime();
            }
            guiGraphics.fill(0, 0, width, height, BACKGROUND_COLOR);
            MinecraftDrawCommandRenderer.render(guiGraphics, font, drawCommands);
        } catch (Throwable throwable) {
            openErrorScreen("render", throwable);
        }
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        try {
            root.mouseMoved(createInteractionContext(), mouseX, mouseY);
        } catch (Throwable throwable) {
            openErrorScreen("mouseMoved", throwable);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        try {
            return root.mouseClicked(createInteractionContext(), mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
        } catch (Throwable throwable) {
            openErrorScreen("mouseClicked", throwable);
            return true;
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        try {
            return root.mouseReleased(createInteractionContext(), mouseX, mouseY, button) || super.mouseReleased(mouseX, mouseY, button);
        } catch (Throwable throwable) {
            openErrorScreen("mouseReleased", throwable);
            return true;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        try {
            return root.mouseScrolled(createInteractionContext(), mouseX, mouseY, scrollY) || super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        } catch (Throwable throwable) {
            openErrorScreen("mouseScrolled", throwable);
            return true;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        try {
            if (focusedComponent != null && focusedComponent.keyPressed(createInteractionContext(), keyCode, scanCode, modifiers)) {
                return true;
            }
            return root.keyPressed(createInteractionContext(), keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
        } catch (Throwable throwable) {
            openErrorScreen("keyPressed", throwable);
            return true;
        }
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        try {
            if (focusedComponent != null && focusedComponent.charTyped(createInteractionContext(), codePoint, modifiers)) {
                return true;
            }
            return root.charTyped(createInteractionContext(), codePoint, modifiers) || super.charTyped(codePoint, modifiers);
        } catch (Throwable throwable) {
            openErrorScreen("charTyped", throwable);
            return true;
        }
    }

    public SlateComponent root() {
        return root;
    }

    public StateProvider stateProvider() {
        return stateProvider;
    }

    public Theme theme() {
        return theme;
    }

    public SlateComponent focusedComponent() {
        return focusedComponent;
    }

    public void setFocusedComponent(SlateComponent component) {
        if (focusedComponent == component) {
            return;
        }
        if (focusedComponent != null) {
            focusedComponent.setFocused(false);
        }
        focusedComponent = component;
        if (focusedComponent != null) {
            focusedComponent.setFocused(true);
            diagnostics.logDiagnostic("FOCUS " + focusedComponent.debugName());
        }
        requestRebuild("focus-change");
    }

    protected SlateInteractionContext createInteractionContext() {
        return new SlateInteractionContext(
            commands,
            new CommandContext(Minecraft.getInstance(), this),
            diagnostics::logCommand,
            diagnostics::logDiagnostic,
            this,
            stateProvider,
            theme
        );
    }

    private String dumpState() {
        List<String> lines = new ArrayList<>();
        if (stateProvider == StateProvider.EMPTY) {
            return "<empty>";
        }
        lines.add("provider=" + stateProvider.getClass().getSimpleName());
        return String.join("\n", lines);
    }

    private void openErrorScreen(String stage, Throwable throwable) {
        Minecraft.getInstance().setScreen(new SlateErrorScreen(stage, throwable, diagnostics));
    }
}
