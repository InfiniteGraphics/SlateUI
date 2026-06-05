package top.huliawsl.slateui.security;

public record SlateSecurityDecision(boolean allowed, String reason) {
    public static SlateSecurityDecision allow() {
        return new SlateSecurityDecision(true, "allowed");
    }

    public static SlateSecurityDecision deny(String reason) {
        return new SlateSecurityDecision(false, reason == null || reason.isBlank() ? "denied" : reason);
    }
}
