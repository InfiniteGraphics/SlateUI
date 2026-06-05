package example.slateui.forge;

import top.huliawsl.slateui.platform.Services;

public final class ConsumerForgeEntrypoint {
    public String platformName() {
        return Services.PLATFORM.getPlatformName();
    }
}
