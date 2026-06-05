package top.huliawsl.slateui.server;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import top.huliawsl.slateui.security.SlateCommandCapability;
import top.huliawsl.slateui.security.SlateCommandSecurityPolicy;
import top.huliawsl.slateui.security.SlateSecurityDecision;

public final class SlateIntentSecurityPolicy {

    private static final int DEFAULT_MAX_NONCES = 4096;
    private static final int DEFAULT_MAX_PAYLOAD_KEYS = 128;

    private final Set<Long> seenNonces = new HashSet<>();
    private final ArrayDeque<Long> nonceOrder = new ArrayDeque<>();
    private final SlateCommandSecurityPolicy commandPolicy;
    private final int maxNonces;
    private final int maxPayloadKeys;
    private final Duration maxClockSkew;

    public SlateIntentSecurityPolicy() {
        this(SlateCommandSecurityPolicy.trustedClient(), DEFAULT_MAX_NONCES, DEFAULT_MAX_PAYLOAD_KEYS, Duration.ofMinutes(5));
    }

    public SlateIntentSecurityPolicy(SlateCommandSecurityPolicy commandPolicy, int maxNonces, int maxPayloadKeys, Duration maxClockSkew) {
        this.commandPolicy = commandPolicy == null ? SlateCommandSecurityPolicy.trustedClient() : commandPolicy;
        this.maxNonces = Math.max(128, maxNonces);
        this.maxPayloadKeys = Math.max(8, maxPayloadKeys);
        this.maxClockSkew = maxClockSkew == null ? Duration.ofMinutes(5) : maxClockSkew;
    }

    public SlateIntentResult validate(SlateIntentPacket packet) {
        if (packet == null) {
            return SlateIntentResult.rejected("Missing Slate intent packet");
        }
        if (packet.protocolVersion() != SlateIntentPacket.CURRENT_PROTOCOL_VERSION) {
            return SlateIntentResult.rejected("Unsupported Slate intent protocol: " + packet.protocolVersion());
        }
        if (packet.intentId() == null || packet.intentId().isBlank()) {
            return SlateIntentResult.rejected("Missing intent id");
        }
        SlateSecurityDecision decision = commandPolicy.evaluate(packet.intentId(), SlateCommandCapability.SERVER_INTENT);
        if (!decision.allowed()) {
            return SlateIntentResult.rejected(decision.reason());
        }
        if (!rememberNonce(packet.nonce())) {
            return SlateIntentResult.rejected("Duplicate Slate intent nonce");
        }
        Map<String, Object> payload = packet.payload();
        if (payload != null && payload.size() > maxPayloadKeys) {
            return SlateIntentResult.rejected("Slate intent payload has too many keys: " + payload.size());
        }
        long now = System.currentTimeMillis();
        long delta = Math.abs(now - packet.createdAtEpochMillis());
        if (packet.createdAtEpochMillis() > 0 && delta > maxClockSkew.toMillis()) {
            return SlateIntentResult.rejected("Slate intent timestamp outside allowed skew");
        }
        return SlateIntentResult.ok();
    }

    private boolean rememberNonce(long nonce) {
        if (!seenNonces.add(nonce)) {
            return false;
        }
        nonceOrder.addLast(nonce);
        while (nonceOrder.size() > maxNonces) {
            Long removed = nonceOrder.removeFirst();
            seenNonces.remove(removed);
        }
        return true;
    }
}
