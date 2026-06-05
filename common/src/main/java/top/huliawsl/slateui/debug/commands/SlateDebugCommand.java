package top.huliawsl.slateui.debug.commands;

import java.util.Objects;
import java.util.function.Supplier;

public record SlateDebugCommand(String id, String description, Supplier<String> handler) {
    public SlateDebugCommand {
        id = Objects.requireNonNull(id, "id");
        description = description == null ? "" : description;
        handler = handler == null ? () -> "" : handler;
    }
}
