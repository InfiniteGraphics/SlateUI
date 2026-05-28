package top.huliawsl.slateui.api;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import top.huliawsl.slateui.command.MinecraftCommandContext;
import top.huliawsl.slateui.command.SlateCommandRegistry;
import top.huliawsl.slateui.api.component.Button;
import top.huliawsl.slateui.api.component.Toggle;
import top.huliawsl.slateui.debug.SlateDiagnostics;
import top.huliawsl.slateui.debug.SlateErrorScreen;
import top.huliawsl.slateui.debug.SlateInspectorScreen;
import top.huliawsl.slateui.debug.SlateRuntimeException;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommandDispatcher;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.MinecraftSlateRenderer;
import top.huliawsl.slateui.runtime.MinecraftTextMeasurer;
import top.huliawsl.slateui.runtime.SlateClipboard;
import top.huliawsl.slateui.runtime.SlateHost;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public class SlateScreen extends Screen implements SlateHost {

    private static final int BACKGROUND_COLOR = 0xFF0B1220;

    private final SlateComponent root;
    private final SlateCommandRegistry commands;
    private final boolean debugEnabled;
    private final SlateDiagnostics diagnostics = new SlateDiagnostics();
    private final StateProvider stateProvider;
    private final StateListener stateListener = path -> requestRebuild("state:" + path);
    private final Theme theme;
    private final String bindingDump;
    private List<DrawCommand> drawCommands = List.of();
    private boolean runtimeDirty = true;
    private SlateComponent focusedComponent;

    public SlateScreen(Component title, SlateComponent root, SlateCommandRegistry commands, boolean debugEnabled) {
        this(title, root, commands, StateProvider.EMPTY, Theme.DEFAULT, debugEnabled);
    }

    public SlateScreen(Component title, SlateComponent root, SlateCommandRegistry commands, StateProvider stateProvider, Theme theme, boolean debugEnabled) {
        this(title, root, commands, stateProvider, theme, debugEnabled, "<programmatic tree>");
    }

    public SlateScreen(Component title, SlateComponent root, SlateCommandRegistry commands, StateProvider stateProvider, Theme theme, boolean debugEnabled, String bindingDump) {
        super(title);
        this.root = root;
        this.commands = commands.copy()
            .register("screen.close", context -> context.host().closeScreen())
            .register("screen.inspect", context -> context.host().inspect());
        this.stateProvider = stateProvider == null ? StateProvider.EMPTY : stateProvider;
        this.theme = theme == null ? Theme.DEFAULT : theme;
        this.bindingDump = bindingDump == null || bindingDump.isBlank() ? "<none>" : bindingDump;
        this.debugEnabled = debugEnabled;
        this.stateProvider.addListener(stateListener);
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

    @Override
    public void requestRebuild(String reason) {
        diagnostics.logDiagnostic("REBUILD " + reason);
        runtimeDirty = true;
    }

    protected void rebuildRuntime() {
        runtimeDirty = false;
        try {
            SlateLayoutContext layoutContext = new SlateLayoutContext(new MinecraftTextMeasurer(font), theme);
            Size available = new Size(width, height);
            root.refreshDebugPaths();
            measureRoot(layoutContext, available);
            layoutRoot(layoutContext, new Rect(0, 0, width, height));
            List<DrawCommand> commands = new ArrayList<>();
            renderRoot(commands);
            this.drawCommands = List.copyOf(commands);
            diagnostics.capture(root, drawCommands, focusedComponent == null ? "<none>" : focusedComponent.debugPath(), bindingDump, dumpState(), theme);
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
            diagnostics.capturePointer(mouseX, mouseY);
            guiGraphics.fill(0, 0, width, height, BACKGROUND_COLOR);
            MinecraftSlateRenderer renderer = new MinecraftSlateRenderer(guiGraphics, font);
            try {
                DrawCommandDispatcher.render(drawCommands, renderer);
            } finally {
                renderer.close();
            }
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
            String path = diagnostics.componentPathAt(mouseX, mouseY);
            boolean handled = root.mouseClicked(createInteractionContext(), mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
            diagnostics.captureEvent("mouseClicked", path, handled);
            return handled;
        } catch (Throwable throwable) {
            openErrorScreen("mouseClicked", throwable);
            return true;
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        try {
            String path = diagnostics.componentPathAt(mouseX, mouseY);
            boolean handled = root.mouseReleased(createInteractionContext(), mouseX, mouseY, button) || super.mouseReleased(mouseX, mouseY, button);
            diagnostics.captureEvent("mouseReleased", path, handled);
            return handled;
        } catch (Throwable throwable) {
            openErrorScreen("mouseReleased", throwable);
            return true;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        try {
            String path = diagnostics.componentPathAt(mouseX, mouseY);
            boolean handled = root.mouseScrolled(createInteractionContext(), mouseX, mouseY, scrollY) || super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            diagnostics.captureEvent("mouseScrolled", path, handled);
            return handled;
        } catch (Throwable throwable) {
            openErrorScreen("mouseScrolled", throwable);
            return true;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        try {
            if (keyCode == GLFW.GLFW_KEY_F6 || keyCode == GLFW.GLFW_KEY_F8 || keyCode == GLFW.GLFW_KEY_F11) {
                Minecraft.getInstance().setScreen(new SlateInspectorScreen(this, diagnostics));
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_F9) {
                diagnostics.logDiagnostic("DEBUG toggle bounds requested");
                requestRebuild("debug-bounds");
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_F10) {
                diagnostics.logDiagnostic("DEBUG toggle hit regions requested");
                requestRebuild("debug-hit-regions");
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_TAB) {
                moveFocus((modifiers & GLFW.GLFW_MOD_SHIFT) != 0 ? -1 : 1);
                return true;
            }
            if ((keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER || keyCode == GLFW.GLFW_KEY_SPACE)
                && focusedComponent != null
                && activateFocusedComponent()) {
                return true;
            }
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

    public SlateDiagnostics diagnostics() {
        return diagnostics;
    }

    public SlateComponent focusedComponent() {
        return focusedComponent;
    }

    @Override
    public void requestFocus(SlateComponent component) {
        setFocusedComponent(component);
    }

    @Override
    public void clearFocus(SlateComponent component) {
        if (focusedComponent == component) {
            setFocusedComponent(null);
        }
    }

    @Override
    public String title() {
        return getTitle().getString();
    }

    @Override
    public void openScreen(Object screenHandle) {
        if (screenHandle instanceof Screen screen) {
            Minecraft.getInstance().setScreen(screen);
        }
    }

    @Override
    public void closeScreen() {
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    public void inspect() {
        Minecraft.getInstance().setScreen(new SlateInspectorScreen(this, diagnostics));
    }

    @Override
    public void reportDiagnostic(String entry) {
        diagnostics.logDiagnostic(entry);
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
            diagnostics.logDiagnostic("FOCUS " + focusedComponent.debugPath());
        }
        requestRebuild("focus-change");
    }

    private void measureRoot(SlateLayoutContext layoutContext, Size available) {
        try {
            root.measure(layoutContext, available);
        } catch (SlateRuntimeException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throw SlateRuntimeException.component("measure", root, throwable);
        }
    }

    private void layoutRoot(SlateLayoutContext layoutContext, Rect bounds) {
        try {
            root.layout(layoutContext, bounds);
        } catch (SlateRuntimeException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throw SlateRuntimeException.component("layout", root, throwable);
        }
    }

    private void renderRoot(List<DrawCommand> commands) {
        try {
            root.collectDrawCommands(new SlateRenderContext(debugEnabled, theme), commands);
        } catch (SlateRuntimeException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throw SlateRuntimeException.component("render", root, throwable);
        }
    }

    protected SlateInteractionContext createInteractionContext() {
        SlateClipboard clipboard = new SlateClipboard() {
            @Override
            public String get() {
                return Minecraft.getInstance().keyboardHandler.getClipboard();
            }

            @Override
            public void set(String value) {
                Minecraft.getInstance().keyboardHandler.setClipboard(value == null ? "" : value);
            }
        };
        return new SlateInteractionContext(
            commands,
            new MinecraftCommandContext(Minecraft.getInstance(), this, this),
            diagnostics::logCommand,
            ignored -> {},
            this,
            stateProvider,
            theme,
            clipboard
        );
    }

    private void moveFocus(int direction) {
        List<SlateComponent> focusOrder = new ArrayList<>();
        collectFocusable(root, focusOrder);
        if (focusOrder.isEmpty()) {
            return;
        }
        int current = focusedComponent == null ? -1 : focusOrder.indexOf(focusedComponent);
        int next = Math.floorMod(current + direction, focusOrder.size());
        setFocusedComponent(focusOrder.get(next));
    }

    private static void collectFocusable(SlateComponent component, List<SlateComponent> focusOrder) {
        if (component.focusable() && !component.style().disabled()) {
            focusOrder.add(component);
        }
        for (SlateComponent child : component.children()) {
            collectFocusable(child, focusOrder);
        }
    }

    private boolean activateFocusedComponent() {
        if (!(focusedComponent instanceof Button) && !(focusedComponent instanceof Toggle)) {
            return false;
        }
        SlateInteractionContext context = createInteractionContext();
        Rect bounds = focusedComponent.bounds();
        double x = bounds.x() + Math.max(0, bounds.width() / 2.0D);
        double y = bounds.y() + Math.max(0, bounds.height() / 2.0D);
        focusedComponent.mouseClicked(context, x, y, 0);
        return focusedComponent.mouseReleased(context, x, y, 0);
    }

    private String dumpState() {
        if (stateProvider == StateProvider.EMPTY || stateProvider.snapshot().isEmpty()) {
            return "<empty>";
        }
        List<String> lines = new ArrayList<>();
        lines.add("provider=" + stateProvider.getClass().getSimpleName());
        for (var entry : stateProvider.snapshot().entrySet()) {
            lines.add(entry.getKey() + "=" + String.valueOf(entry.getValue()));
        }
        return String.join("\n", lines);
    }

    private void openErrorScreen(String stage, Throwable throwable) {
        Minecraft.getInstance().setScreen(new SlateErrorScreen(stage, throwable, diagnostics));
    }

    @Override
    public void removed() {
        stateProvider.removeListener(stateListener);
        super.removed();
    }
}
