package top.huliawsl.slateui.server;

import java.util.ArrayList;
import java.util.List;
import top.huliawsl.slateui.command.CommandContext;

public final class QueuedSlateServerIntentBridge implements SlateServerIntentBridge {

    private final List<SlateServerIntent> intents = new ArrayList<>();

    @Override
    public void send(SlateServerIntent intent, CommandContext context) {
        intents.add(intent);
    }

    public List<SlateServerIntent> intents() {
        return List.copyOf(intents);
    }

    public List<SlateServerIntent> drain() {
        List<SlateServerIntent> copy = List.copyOf(intents);
        intents.clear();
        return copy;
    }
}
