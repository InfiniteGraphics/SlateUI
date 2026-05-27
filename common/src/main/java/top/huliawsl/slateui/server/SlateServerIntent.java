package top.huliawsl.slateui.server;

import java.util.Map;
import java.util.Objects;

public record SlateServerIntent(
    String id,
    String screenTitle,
    Map<String, Object> payload,
    Map<String, Object> stateSnapshot,
    long createdAtEpochMillis
) {

    public SlateServerIntent {
        Objects.requireNonNull(id, "id");
        screenTitle = screenTitle == null ? "" : screenTitle;
        payload = payload == null ? Map.of() : Map.copyOf(payload);
        stateSnapshot = stateSnapshot == null ? Map.of() : Map.copyOf(stateSnapshot);
    }

    public static SlateServerIntent now(String id, String screenTitle, Map<String, Object> payload, Map<String, Object> stateSnapshot) {
        return new SlateServerIntent(id, screenTitle, payload, stateSnapshot, System.currentTimeMillis());
    }
}
