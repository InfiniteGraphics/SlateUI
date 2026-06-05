package top.huliawsl.slateui.server;

import java.util.Map;

public record SlateIntentPacket(
    int protocolVersion,
    String intentId,
    String screenTitle,
    Map<String, Object> payload,
    long nonce,
    long createdAtEpochMillis
) {

    public static final int CURRENT_PROTOCOL_VERSION = 1;

    public SlateIntentPacket(int protocolVersion, String intentId, String screenTitle, Map<String, Object> payload, long nonce) {
        this(protocolVersion, intentId, screenTitle, payload, nonce, System.currentTimeMillis());
    }

    public SlateIntentPacket {
        payload = payload == null ? Map.of() : Map.copyOf(payload);
        screenTitle = screenTitle == null ? "" : screenTitle;
    }

    public static SlateIntentPacket fromIntent(SlateServerIntent intent, long nonce) {
        return new SlateIntentPacket(CURRENT_PROTOCOL_VERSION, intent.id(), intent.screenTitle(), intent.payload(), nonce, intent.createdAtEpochMillis());
    }
}
