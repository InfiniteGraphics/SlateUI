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
import top.huliawsl.slateui.runtime.InvalidationType;
import top.huliawsl.slateui.runtime.SlateClipboard;
import top.huliawsl.slateui.runtime.SlateFocusTraversal;
import top.huliawsl.slateui.runtime.SlateHost;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;
import top.huliawsl.slateui.runtime.SlateCursor;

public class SlateScreen extends Screen implements SlateHost {

    private static final int BACKGROUND_COLOR = 0xFF0B1220;

    private final SlateComponent root;
    private final SlateCommandRegistry commands;
    private final boolean debugEnabled;
    private final SlateDiagnostics diagnostics = new SlateDiagnostics();
    private final StateProvider stateProvider;
    private final StateListener stateListener;
    private int lastWidth = -1;
    private int lastHeight = -1;
    private final Theme theme;
    private final String bindingDump;
    private List<DrawCommand> drawCommands = List.of();
    private boolean runtimeDirty = true;
    private boolean layoutDirty = true;
    private boolean paintDirty = true;
    private boolean interactionDirty = true;
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
        this.stateListener = path -> {
            this.root.notifyStateUpdated(path);
            requestInvalidation(InvalidationType.LAYOUT, "state:" + path);
        };
        this.stateProvider.addListener(stateListener);
    }

    @Override
    protected void init() {
        root.mountTree();
        root.notifyThemeUpdated(theme);
        rebuildRuntime();
    }

    @Override
    public void tick() {
        if (lastWidth != width || lastHeight != height) {
            lastWidth = width;
            lastHeight = height;
            root.notifyScreenResized(new Size(width, height));
            requestInvalidation(InvalidationType.LAYOUT, "screen-resize");
        }
        if (runtimeDirty) {
            rebuildRuntime();
        }
    }

    @Override
    public void requestRebuild(String reason) {
        requestInvalidation(InvalidationType.LAYOUT, reason);
    }

    @Override
    public void requestInvalidation(InvalidationType type, String reason) {
        InvalidationType resolvedType = type == null ? InvalidationType.LAYOUT : type;
        diagnostics.logDiagnostic("INVALIDATE " + resolvedType + " " + reason);
        runtimeDirty = true;
        switch (resolvedType) {
            case LAYOUT -> {
                layoutDirty = true;
                paintDirty = true;
                interactionDirty = true;
            }
            case PAINT -> paintDirty = true;
            case INTERACTION -> {
                interactionDirty = true;
                paintDirty = true;
            }
        }
    }

    protected void rebuildRuntime() {
        long rebuildStart = System.nanoTime();
        runtimeDirty = false;
        try {
            SlateLayoutContext layoutContext = new SlateLayoutContext(new MinecraftTextMeasurer(font), theme);
            Size available = new Size(width, height);
            long layoutStart = System.nanoTime();
            if (layoutDirty) {
                root.refreshDebugPaths();
                measureRoot(layoutContext, available);
                layoutRoot(layoutContext, new Rect(0, 0, width, height));
            }
            long layoutNanos = System.nanoTime() - layoutStart;
            if (layoutDirty || paintDirty) {
                List<DrawCommand> commands = new ArrayList<>();
                renderRoot(commands);
                this.drawCommands = List.copyOf(commands);
            }
            diagnostics.capture(root, drawCommands, focusedComponent == null ? "<none>" : focusedComponent.debugPath(), bindingDump, dumpState(), theme);
            diagnostics.captureTimings(System.nanoTime() - rebuildStart, layoutNanos);
            layoutDirty = false;
            paintDirty = false;
            interactionDirty = false;
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
            renderSlateBackground(guiGraphics, mouseX, mouseY, partialTick);
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

    protected void renderSlateBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, width, height, BACKGROUND_COLOR);
    }

    protected boolean shouldDispatchPointerEvent(double mouseX, double mouseY, String eventName) {
        return true;
    }

    protected boolean shouldDispatchKeyboardEvent(String eventName) {
        return true;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        try {
            if (shouldDispatchPointerEvent(mouseX, mouseY, "mouseMoved")) {
                root.mouseMoved(createInteractionContext(), mouseX, mouseY);
            }
        } catch (Throwable throwable) {
            openErrorScreen("mouseMoved", throwable);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        try {
            if (!shouldDispatchPointerEvent(mouseX, mouseY, "mouseClicked")) {
                return false;
            }
            String path = diagnostics.componentPathAt(mouseX, mouseY);
            boolean handled = root.mouseClicked(createInteractionContext(currentPointerModifiers()), mouseX, mouseY, button);
            if (!handled) {
                clearFocus(focusedComponent);
            }
            handled = handled || super.mouseClicked(mouseX, mouseY, button);
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
            if (!shouldDispatchPointerEvent(mouseX, mouseY, "mouseReleased")) {
                return false;
            }
            String path = diagnostics.componentPathAt(mouseX, mouseY);
            boolean handled = root.mouseReleased(createInteractionContext(currentPointerModifiers()), mouseX, mouseY, button) || super.mouseReleased(mouseX, mouseY, button);
            diagnostics.captureEvent("mouseReleased", path, handled);
            return handled;
        } catch (Throwable throwable) {
            openErrorScreen("mouseReleased", throwable);
            return true;
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        try {
            if (!shouldDispatchPointerEvent(mouseX, mouseY, "mouseDragged")) {
                return false;
            }
            String path = diagnostics.componentPathAt(mouseX, mouseY);
            boolean handled = root.mouseDragged(createInteractionContext(currentPointerModifiers()), mouseX, mouseY, button, dragX, dragY)
                || super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
            diagnostics.captureEvent("mouseDragged", path, handled);
            return handled;
        } catch (Throwable throwable) {
            openErrorScreen("mouseDragged", throwable);
            return true;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        try {
            if (!shouldDispatchPointerEvent(mouseX, mouseY, "mouseScrolled")) {
                return false;
            }
            String path = diagnostics.componentPathAt(mouseX, mouseY);
            boolean handled = root.mouseScrolled(createInteractionContext(currentPointerModifiers()), mouseX, mouseY, scrollY) || super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
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
                requestInvalidation(InvalidationType.PAINT, "debug-bounds");
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_F10) {
                diagnostics.logDiagnostic("DEBUG toggle hit regions requested");
                requestInvalidation(InvalidationType.PAINT, "debug-hit-regions");
                return true;
            }
            if (!shouldDispatchKeyboardEvent("keyPressed")) {
                return false;
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
            if (focusedComponent != null && focusedComponent.keyPressed(createInteractionContext(modifiers), keyCode, scanCode, modifiers)) {
                return true;
            }
            return root.keyPressed(createInteractionContext(modifiers), keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
        } catch (Throwable throwable) {
            openErrorScreen("keyPressed", throwable);
            return true;
        }
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        try {
            if (!shouldDispatchKeyboardEvent("charTyped")) {
                return false;
            }
            if (focusedComponent != null && focusedComponent.charTyped(createInteractionContext(modifiers), codePoint, modifiers)) {
                return true;
            }
            return root.charTyped(createInteractionContext(modifiers), codePoint, modifiers) || super.charTyped(codePoint, modifiers);
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

    @Override
    public void requestPointerCapture(String reason) {
        long window = Minecraft.getInstance().getWindow().getWindow();
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        diagnostics.logDiagnostic("POINTER capture " + reason);
    }

    @Override
    public void releasePointerCapture(String reason) {
        long window = Minecraft.getInstance().getWindow().getWindow();
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        diagnostics.logDiagnostic("POINTER release " + reason);
    }

    @Override
    public void setCursor(SlateCursor cursor) {
        if (cursor == SlateCursor.HIDDEN) {
            long window = Minecraft.getInstance().getWindow().getWindow();
            GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
        } else {
            long window = Minecraft.getInstance().getWindow().getWindow();
            GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        }
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
        requestInvalidation(InvalidationType.INTERACTION, "focus-change");
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
        return createInteractionContext(0);
    }

    protected SlateInteractionContext createInteractionContext(int modifiers) {
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
            clipboard,
            modifiers
        );
    }

    private int currentPointerModifiers() {
        long window = Minecraft.getInstance().getWindow().getWindow();
        int modifiers = 0;
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
            || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS) {
            modifiers |= GLFW.GLFW_MOD_SHIFT;
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS
            || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS) {
            modifiers |= GLFW.GLFW_MOD_CONTROL;
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_ALT) == GLFW.GLFW_PRESS
            || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_ALT) == GLFW.GLFW_PRESS) {
            modifiers |= GLFW.GLFW_MOD_ALT;
        }
        return modifiers;
    }

    private void moveFocus(int direction) {
        SlateComponent next = SlateFocusTraversal.next(root, focusedComponent, direction);
        if (next != null) {
            setFocusedComponent(next);
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
        root.unmountTree();
        root.disposeTree();
        stateProvider.removeListener(stateListener);
        super.removed();
    }
}
