package top.huliawsl.slateui.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SlateCommandSecurityPolicyTest {

    @Test
    void localPolicyAllowsOnlySafeNamespaces() {
        SlateCommandSecurityPolicy policy = SlateCommandSecurityPolicy.localOnly();
        assertTrue(policy.evaluate("slate.reload", SlateCommandCapability.LOCAL_SAFE).allowed());
        assertFalse(policy.evaluate("server.bake", SlateCommandCapability.SERVER_INTENT).allowed());
        assertFalse(policy.evaluate("external.openUrl", SlateCommandCapability.DANGEROUS_EXTERNAL).allowed());
    }
}
