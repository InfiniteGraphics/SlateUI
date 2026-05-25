package top.huliawsl.slateui.runtime;

import java.util.function.Consumer;
import top.huliawsl.slateui.command.CommandContext;
import top.huliawsl.slateui.command.SlateCommandRegistry;

public record SlateInteractionContext(SlateCommandRegistry commands, CommandContext commandContext, Consumer<String> commandLogger) {
}
