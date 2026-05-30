package top.huliawsl.slateui.server;

import java.util.HashSet;
import java.util.Set;

public final class SlateIntentSecurityPolicy {

    private final Set<Long> seenNonces = new HashSet<>();

    public SlateIntentResult validate(SlateIntentPacket packet) {
        if (packet.protocolVersion() != SlateIntentPacket.CURRENT_PROTOCOL_VERSION) {
            return SlateIntentResult.rejected("Unsupported Slate intent protocol: " + packet.protocolVersion());
        }
        if (!seenNonces.add(packet.nonce())) {
            return SlateIntentResult.rejected("Duplicate Slate intent nonce");
        }
        if (packet.intentId() == null || packet.intentId().isBlank()) {
            return SlateIntentResult.rejected("Missing intent id");
        }
        return SlateIntentResult.ok();
    }
}
