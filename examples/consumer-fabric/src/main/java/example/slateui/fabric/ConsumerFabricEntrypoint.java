package example.slateui.fabric;

import top.huliawsl.slateui.platform.Services;

public final class ConsumerFabricEntrypoint {
    public String platformName() {
        return Services.PLATFORM.getPlatformName();
    }
}
