package top.huliawsl.slateui.scripting;

import java.util.Map;

public record SandboxedCommandModel(String commandId, Map<String, Object> payload) {

    public SandboxedCommandModel {
        commandId = commandId == null ? "" : commandId;
        payload = payload == null ? Map.of() : Map.copyOf(payload);
    }
}
