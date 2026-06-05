package top.huliawsl.slateui.security;

import java.util.EnumSet;
import java.util.Set;

public final class SlateCommandSecurityPolicy {

    private final Set<SlateCommandCapability> allowedCapabilities;
    private final Set<String> allowedNamespaces;

    public SlateCommandSecurityPolicy(Set<SlateCommandCapability> allowedCapabilities, Set<String> allowedNamespaces) {
        this.allowedCapabilities = allowedCapabilities == null || allowedCapabilities.isEmpty()
            ? EnumSet.of(SlateCommandCapability.LOCAL_SAFE)
            : EnumSet.copyOf(allowedCapabilities);
        this.allowedNamespaces = allowedNamespaces == null ? Set.of() : Set.copyOf(allowedNamespaces);
    }

    public static SlateCommandSecurityPolicy localOnly() {
        return new SlateCommandSecurityPolicy(EnumSet.of(SlateCommandCapability.LOCAL_SAFE), Set.of("slate", "ui", "local"));
    }

    public static SlateCommandSecurityPolicy trustedClient() {
        return new SlateCommandSecurityPolicy(EnumSet.of(SlateCommandCapability.LOCAL_SAFE, SlateCommandCapability.CLIENT_ACTION, SlateCommandCapability.SERVER_INTENT), Set.of());
    }

    public SlateSecurityDecision evaluate(String commandId, SlateCommandCapability capability) {
        if (commandId == null || commandId.isBlank()) {
            return SlateSecurityDecision.deny("Missing command id");
        }
        SlateCommandCapability resolvedCapability = capability == null ? SlateCommandCapability.LOCAL_SAFE : capability;
        if (!allowedCapabilities.contains(resolvedCapability)) {
            return SlateSecurityDecision.deny("Capability not allowed: " + resolvedCapability);
        }
        String namespace = namespace(commandId);
        if (!allowedNamespaces.isEmpty() && !allowedNamespaces.contains(namespace)) {
            return SlateSecurityDecision.deny("Command namespace not allowed: " + namespace);
        }
        return SlateSecurityDecision.allow();
    }

    private static String namespace(String commandId) {
        int dot = commandId.indexOf('.');
        int colon = commandId.indexOf(':');
        int split = dot < 0 ? colon : colon < 0 ? dot : Math.min(dot, colon);
        return split < 0 ? commandId : commandId.substring(0, split);
    }
}
