package top.huliawsl.slateui.server;

import top.huliawsl.slateui.command.CommandContext;

@FunctionalInterface
public interface SlateServerIntentBridge {

    void send(SlateServerIntent intent, CommandContext context);

    static SlateServerIntentBridge noop() {
        return (intent, context) -> {
        };
    }
}
