package top.huliawsl.slateui.command;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public final class SlateCommandRegistry {

    private final Map<String, Consumer<CommandContext>> commands;

    public SlateCommandRegistry() {
        this.commands = new LinkedHashMap<>();
    }

    private SlateCommandRegistry(Map<String, Consumer<CommandContext>> commands) {
        this.commands = new LinkedHashMap<>(commands);
    }

    public SlateCommandRegistry register(String id, Consumer<CommandContext> handler) {
        commands.put(Objects.requireNonNull(id, "id"), Objects.requireNonNull(handler, "handler"));
        return this;
    }

    public boolean execute(String id, CommandContext context) {
        Consumer<CommandContext> handler = commands.get(id);
        if (handler == null) {
            return false;
        }
        handler.accept(context);
        return true;
    }

    public Optional<Consumer<CommandContext>> find(String id) {
        return Optional.ofNullable(commands.get(id));
    }

    public SlateCommandRegistry copy() {
        return new SlateCommandRegistry(commands);
    }
}
