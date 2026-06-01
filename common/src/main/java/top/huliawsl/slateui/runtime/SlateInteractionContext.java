package top.huliawsl.slateui.runtime;

import java.util.Objects;
import java.util.function.Consumer;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.StateProvider;
import top.huliawsl.slateui.api.Theme;
import top.huliawsl.slateui.command.CommandContext;
import top.huliawsl.slateui.command.SlateCommandRegistry;

public final class SlateInteractionContext {

    private final SlateCommandRegistry commands;
    private final CommandContext commandContext;
    private final Consumer<String> commandLogger;
    private final Consumer<String> diagnosticsLogger;
    private final SlateHost host;
    private final StateProvider stateProvider;
    private final Theme theme;
    private final SlateClipboard clipboard;
    private final int modifiers;

    public SlateInteractionContext(
        SlateCommandRegistry commands,
        CommandContext commandContext,
        Consumer<String> commandLogger,
        Consumer<String> diagnosticsLogger,
        SlateHost host,
        StateProvider stateProvider,
        Theme theme
    ) {
        this(commands, commandContext, commandLogger, diagnosticsLogger, host, stateProvider, theme, SlateClipboard.EMPTY, 0);
    }

    public SlateInteractionContext(
        SlateCommandRegistry commands,
        CommandContext commandContext,
        Consumer<String> commandLogger,
        Consumer<String> diagnosticsLogger,
        SlateHost host,
        StateProvider stateProvider,
        Theme theme,
        SlateClipboard clipboard
    ) {
        this(commands, commandContext, commandLogger, diagnosticsLogger, host, stateProvider, theme, clipboard, 0);
    }

    public SlateInteractionContext(
        SlateCommandRegistry commands,
        CommandContext commandContext,
        Consumer<String> commandLogger,
        Consumer<String> diagnosticsLogger,
        SlateHost host,
        StateProvider stateProvider,
        Theme theme,
        SlateClipboard clipboard,
        int modifiers
    ) {
        this.commands = Objects.requireNonNull(commands, "commands");
        this.commandContext = Objects.requireNonNull(commandContext, "commandContext");
        this.commandLogger = Objects.requireNonNull(commandLogger, "commandLogger");
        this.diagnosticsLogger = Objects.requireNonNull(diagnosticsLogger, "diagnosticsLogger");
        this.host = host == null ? SlateHost.NOOP : host;
        this.stateProvider = Objects.requireNonNull(stateProvider, "stateProvider");
        this.theme = Objects.requireNonNull(theme, "theme");
        this.clipboard = clipboard == null ? SlateClipboard.EMPTY : clipboard;
        this.modifiers = modifiers;
    }

    public SlateCommandRegistry commands() {
        return commands;
    }

    public CommandContext commandContext() {
        return commandContext;
    }

    public Consumer<String> commandLogger() {
        return commandLogger;
    }

    public void logDiagnostic(String entry) {
        diagnosticsLogger.accept(entry);
        host.reportDiagnostic(entry);
    }

    public SlateHost host() {
        return host;
    }

    public void requestRebuild(String reason) {
        host.requestRebuild(reason);
    }

    public void requestInvalidation(InvalidationType type, String reason) {
        host.requestInvalidation(type, reason);
    }

    public StateProvider stateProvider() {
        return stateProvider;
    }

    public Theme theme() {
        return theme;
    }

    public SlateClipboard clipboard() {
        return clipboard;
    }

    public int modifiers() {
        return modifiers;
    }

    public boolean shiftDown() {
        return (modifiers & org.lwjgl.glfw.GLFW.GLFW_MOD_SHIFT) != 0;
    }

    public boolean controlDown() {
        return (modifiers & org.lwjgl.glfw.GLFW.GLFW_MOD_CONTROL) != 0;
    }

    public boolean altDown() {
        return (modifiers & org.lwjgl.glfw.GLFW.GLFW_MOD_ALT) != 0;
    }

    public void requestFocus(SlateComponent component) {
        host.requestFocus(component);
    }

    public void clearFocus(SlateComponent component) {
        host.clearFocus(component);
    }
}
