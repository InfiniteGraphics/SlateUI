package top.huliawsl.slateui.runtime;

import top.huliawsl.slateui.command.CommandContext;
import top.huliawsl.slateui.command.SlateCommandRegistry;

public record SlateInteractionContext(SlateCommandRegistry commands, CommandContext commandContext) {
}
