package top.huliawsl.slateui.runtime;

import java.util.Objects;
import java.util.function.Consumer;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateScreen;
import top.huliawsl.slateui.api.StateProvider;
import top.huliawsl.slateui.api.Theme;
import top.huliawsl.slateui.command.CommandContext;
import top.huliawsl.slateui.command.SlateCommandRegistry;

public final class SlateInteractionContext {

    private final SlateCommandRegistry commands;
    private final CommandContext commandContext;
    private final Consumer<String> commandLogger;
    private final Consumer<String> diagnosticsLogger;
    private final SlateScreen screen;
    private final StateProvider stateProvider;
    private final Theme theme;

    public SlateInteractionContext(
        SlateCommandRegistry commands,
        CommandContext commandContext,
        Consumer<String> commandLogger,
        Consumer<String> diagnosticsLogger,
        SlateScreen screen,
        StateProvider stateProvider,
        Theme theme
    ) {
        this.commands = Objects.requireNonNull(commands, "commands");
        this.commandContext = Objects.requireNonNull(commandContext, "commandContext");
        this.commandLogger = Objects.requireNonNull(commandLogger, "commandLogger");
        this.diagnosticsLogger = Objects.requireNonNull(diagnosticsLogger, "diagnosticsLogger");
        this.screen = Objects.requireNonNull(screen, "screen");
        this.stateProvider = Objects.requireNonNull(stateProvider, "stateProvider");
        this.theme = Objects.requireNonNull(theme, "theme");
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
    }

    public SlateScreen screen() {
        return screen;
    }

    public StateProvider stateProvider() {
        return stateProvider;
    }

    public Theme theme() {
        return theme;
    }

    public void requestFocus(SlateComponent component) {
        screen.setFocusedComponent(component);
    }

    public void clearFocus(SlateComponent component) {
        if (screen.focusedComponent() == component) {
            screen.setFocusedComponent(null);
        }
    }
}
