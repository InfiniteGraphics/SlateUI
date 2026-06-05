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
import top.huliawsl.slateui.security.SlateCommandCapability;
import top.huliawsl.slateui.security.SlateCommandSecurityPolicy;
import top.huliawsl.slateui.security.SlateSecurityDecision;

public final class SlateCommandRegistry {

    private final Map<String, Consumer<CommandContext>> commands;
    private final Map<String, SlateCommand> commandModels;
    private final Set<String> serverIntentCommands;
    private final SlateServerIntentBridge serverIntentBridge;
    private final SlateCommandSecurityPolicy securityPolicy;

    public SlateCommandRegistry() {
        this(new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashSet<>(), SlateServerIntentBridge.noop(), SlateCommandSecurityPolicy.trustedClient());
    }

    private SlateCommandRegistry(
        Map<String, Consumer<CommandContext>> commands,
        Map<String, SlateCommand> commandModels,
        Set<String> serverIntentCommands,
        SlateServerIntentBridge serverIntentBridge,
        SlateCommandSecurityPolicy securityPolicy
    ) {
        this.commands = new LinkedHashMap<>(commands);
        this.commandModels = new LinkedHashMap<>(commandModels);
        this.serverIntentCommands = new LinkedHashSet<>(serverIntentCommands);
        this.serverIntentBridge = Objects.requireNonNull(serverIntentBridge, "serverIntentBridge");
        this.securityPolicy = Objects.requireNonNullElseGet(securityPolicy, SlateCommandSecurityPolicy::localOnly);
    }

    public SlateCommandRegistry register(String id, Consumer<CommandContext> handler) {
        commands.put(Objects.requireNonNull(id, "id"), Objects.requireNonNull(handler, "handler"));
        return this;
    }

    public SlateCommandRegistry register(SlateCommand command) {
        Objects.requireNonNull(command, "command");
        commandModels.put(command.id(), command);
        commands.put(command.id(), command::execute);
        return this;
    }

    public Optional<SlateCommand> commandModel(String id) {
        return Optional.ofNullable(commandModels.get(id));
    }

    public Set<SlateCommand> commandModels() {
        return Set.copyOf(commandModels.values());
    }

    public SlateCommandRegistry registerServerIntent(String id) {
        serverIntentCommands.add(Objects.requireNonNull(id, "id"));
        return this;
    }

    public SlateCommandRegistry withServerIntentBridge(SlateServerIntentBridge bridge) {
        return new SlateCommandRegistry(commands, commandModels, serverIntentCommands, bridge, securityPolicy);
    }

    public SlateCommandRegistry withSecurityPolicy(SlateCommandSecurityPolicy policy) {
        return new SlateCommandRegistry(commands, commandModels, serverIntentCommands, serverIntentBridge, policy);
    }

    public boolean execute(String id, CommandContext context) {
        return executeResult(id, context).executed();
    }

    public CommandResult executeResult(String id, CommandContext context) {
        Consumer<CommandContext> handler = commands.get(id);
        if (handler == null) {
            return CommandResult.MISSING;
        }
        SlateCommand command = commandModels.get(id);
        SlateCommandCapability capability = command == null ? SlateCommandCapability.LOCAL_SAFE : command.capability();
        SlateSecurityDecision decision = securityPolicy.evaluate(id, capability);
        if (!decision.allowed()) {
            return CommandResult.rejected(decision.reason());
        }
        handler.accept(context);
        return CommandResult.EXECUTED;
    }

    public boolean execute(String id, SlateInteractionContext context) {
        return execute(id, context, Map.of());
    }

    public boolean execute(String id, SlateInteractionContext context, Map<String, Object> payload) {
        return executeResult(id, context, payload).executed();
    }

    public CommandResult executeResult(String id, SlateInteractionContext context, Map<String, Object> payload) {
        Consumer<CommandContext> handler = commands.get(id);
        if (handler != null) {
            SlateCommand command = commandModels.get(id);
            SlateCommandCapability capability = command == null ? SlateCommandCapability.LOCAL_SAFE : command.capability();
            SlateSecurityDecision decision = securityPolicy.evaluate(id, capability);
            if (!decision.allowed()) {
                return CommandResult.rejected(decision.reason());
            }
            handler.accept(context.commandContext().withPayload(payload));
            return CommandResult.EXECUTED;
        }
        if (!serverIntentCommands.contains(id)) {
            return CommandResult.MISSING;
        }
        SlateSecurityDecision decision = securityPolicy.evaluate(id, SlateCommandCapability.SERVER_INTENT);
        if (!decision.allowed()) {
            return CommandResult.rejected(decision.reason());
        }
        CommandContext commandContext = context.commandContext().withPayload(payload);
        String title = commandContext.host().title();
        serverIntentBridge.send(SlateServerIntent.now(id, title, payload, context.stateProvider().snapshot()), commandContext);
        return CommandResult.executed("server-intent");
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
        return new SlateCommandRegistry(commands, commandModels, serverIntentCommands, serverIntentBridge, securityPolicy);
    }
}
