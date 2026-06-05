package top.huliawsl.slateui.debug.commands;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import top.huliawsl.slateui.debug.SlateDiagnostics;

public final class SlateDebugCommandRegistry {

    private final Map<String, SlateDebugCommand> commands = new LinkedHashMap<>();

    public static SlateDebugCommandRegistry forDiagnostics(SlateDiagnostics diagnostics) {
        SlateDebugCommandRegistry registry = new SlateDebugCommandRegistry();
        return registry
            .register("slate.dump.tree", "Dump component tree", diagnostics::componentTreeDump)
            .register("slate.dump.layout", "Dump layout bounds", diagnostics::layoutDump)
            .register("slate.dump.draw", "Dump retained draw commands", diagnostics::drawCommandDump)
            .register("slate.dump.style", "Dump resolved style tree", diagnostics::styleDump)
            .register("slate.dump.state", "Dump state snapshot", diagnostics::stateDump)
            .register("slate.dump.focus", "Dump focus path", diagnostics::focusDump)
            .register("slate.dump.events", "Dump last event", diagnostics::lastEventDump)
            .register("slate.dump.summary", "Dump runtime summary", diagnostics::runtimeSummaryDump);
    }

    public SlateDebugCommandRegistry register(String id, String description, java.util.function.Supplier<String> handler) {
        return register(new SlateDebugCommand(id, description, handler));
    }

    public SlateDebugCommandRegistry register(SlateDebugCommand command) {
        if (command != null) {
            commands.put(command.id(), command);
        }
        return this;
    }

    public Optional<SlateDebugCommand> command(String id) {
        return Optional.ofNullable(commands.get(id));
    }

    public String execute(String id) {
        return command(id).map(command -> command.handler().get()).orElse("missing debug command: " + id);
    }

    public Map<String, SlateDebugCommand> commands() {
        return Map.copyOf(commands);
    }
}
