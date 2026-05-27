package top.huliawsl.slateui.command;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.server.SlateServerIntent;
import top.huliawsl.slateui.server.SlateServerIntentBridge;

public final class SlateCommandRegistry {

    private final Map<String, Consumer<CommandContext>> commands;
    private final Set<String> serverIntentCommands;
    private final SlateServerIntentBridge serverIntentBridge;

    public SlateCommandRegistry() {
        this(new LinkedHashMap<>(), new LinkedHashSet<>(), SlateServerIntentBridge.noop());
    }

    private SlateCommandRegistry(
        Map<String, Consumer<CommandContext>> commands,
        Set<String> serverIntentCommands,
        SlateServerIntentBridge serverIntentBridge
    ) {
        this.commands = new LinkedHashMap<>(commands);
        this.serverIntentCommands = new LinkedHashSet<>(serverIntentCommands);
        this.serverIntentBridge = Objects.requireNonNull(serverIntentBridge, "serverIntentBridge");
    }

    public SlateCommandRegistry register(String id, Consumer<CommandContext> handler) {
        commands.put(Objects.requireNonNull(id, "id"), Objects.requireNonNull(handler, "handler"));
        return this;
    }

    public SlateCommandRegistry registerServerIntent(String id) {
        serverIntentCommands.add(Objects.requireNonNull(id, "id"));
        return this;
    }

    public SlateCommandRegistry withServerIntentBridge(SlateServerIntentBridge bridge) {
        return new SlateCommandRegistry(commands, serverIntentCommands, bridge);
    }

    public boolean execute(String id, CommandContext context) {
        Consumer<CommandContext> handler = commands.get(id);
        if (handler == null) {
            return false;
        }
        handler.accept(context);
        return true;
    }

    public boolean execute(String id, SlateInteractionContext context) {
        return execute(id, context, Map.of());
    }

    public boolean execute(String id, SlateInteractionContext context, Map<String, Object> payload) {
        Consumer<CommandContext> handler = commands.get(id);
        if (handler != null) {
            handler.accept(context.commandContext().withPayload(payload));
            return true;
        }
        if (!serverIntentCommands.contains(id)) {
            return false;
        }
        CommandContext commandContext = context.commandContext().withPayload(payload);
        String title = commandContext.screen() == null ? "" : commandContext.screen().getTitle().getString();
        serverIntentBridge.send(SlateServerIntent.now(id, title, payload, context.stateProvider().snapshot()), commandContext);
        return true;
    }

    public Optional<Consumer<CommandContext>> find(String id) {
        return Optional.ofNullable(commands.get(id));
    }

    public boolean isServerIntent(String id) {
        return serverIntentCommands.contains(id);
    }

    public Set<String> serverIntentCommands() {
        return Set.copyOf(serverIntentCommands);
    }

    public SlateCommandRegistry copy() {
        return new SlateCommandRegistry(commands, serverIntentCommands, serverIntentBridge);
    }
}
