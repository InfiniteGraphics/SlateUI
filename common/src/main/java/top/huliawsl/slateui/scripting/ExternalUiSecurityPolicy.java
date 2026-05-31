package top.huliawsl.slateui.scripting;

import java.util.Set;

public record ExternalUiSecurityPolicy(Set<String> allowedCommandNamespaces, boolean allowBindings, boolean allowResourceOverrides) {

    public ExternalUiSecurityPolicy {
        allowedCommandNamespaces = allowedCommandNamespaces == null ? Set.of() : Set.copyOf(allowedCommandNamespaces);
    }

    public boolean commandAllowed(String commandId) {
        if (commandId == null || commandId.isBlank()) {
            return false;
        }
        int dot = commandId.indexOf('.');
        String namespace = dot < 0 ? commandId : commandId.substring(0, dot);
        return allowedCommandNamespaces.contains(namespace);
    }
}
