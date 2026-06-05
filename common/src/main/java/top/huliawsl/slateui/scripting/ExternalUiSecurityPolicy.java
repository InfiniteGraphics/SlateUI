package top.huliawsl.slateui.scripting;

import java.util.EnumSet;
import java.util.Set;
import top.huliawsl.slateui.security.SlateCommandCapability;
import top.huliawsl.slateui.security.SlateCommandSecurityPolicy;
import top.huliawsl.slateui.security.SlateSecurityDecision;

public record ExternalUiSecurityPolicy(
    Set<String> allowedCommandNamespaces,
    Set<SlateCommandCapability> allowedCapabilities,
    boolean allowBindings,
    boolean allowResourceOverrides
) {

    public ExternalUiSecurityPolicy {
        allowedCommandNamespaces = allowedCommandNamespaces == null ? Set.of() : Set.copyOf(allowedCommandNamespaces);
        allowedCapabilities = allowedCapabilities == null || allowedCapabilities.isEmpty()
            ? EnumSet.of(SlateCommandCapability.LOCAL_SAFE)
            : EnumSet.copyOf(allowedCapabilities);
    }

    public ExternalUiSecurityPolicy(Set<String> allowedCommandNamespaces, boolean allowBindings, boolean allowResourceOverrides) {
        this(allowedCommandNamespaces, EnumSet.of(SlateCommandCapability.LOCAL_SAFE), allowBindings, allowResourceOverrides);
    }

    public static ExternalUiSecurityPolicy localResourcePack() {
        return new ExternalUiSecurityPolicy(Set.of("slate", "ui", "local"), EnumSet.of(SlateCommandCapability.LOCAL_SAFE), true, true);
    }

    public static ExternalUiSecurityPolicy serverProvided() {
        return new ExternalUiSecurityPolicy(Set.of("slate", "ui", "server"), EnumSet.of(SlateCommandCapability.LOCAL_SAFE, SlateCommandCapability.SERVER_INTENT), false, false);
    }

    public boolean commandAllowed(String commandId) {
        return evaluate(commandId, SlateCommandCapability.LOCAL_SAFE).allowed();
    }

    public SlateSecurityDecision evaluate(String commandId, SlateCommandCapability capability) {
        return new SlateCommandSecurityPolicy(allowedCapabilities, allowedCommandNamespaces).evaluate(commandId, capability);
    }
}
