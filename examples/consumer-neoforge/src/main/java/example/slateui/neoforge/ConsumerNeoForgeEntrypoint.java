package example.slateui.neoforge;

import top.huliawsl.slateui.platform.Services;

public final class ConsumerNeoForgeEntrypoint {
    public String platformName() {
        return Services.PLATFORM.getPlatformName();
    }
}
