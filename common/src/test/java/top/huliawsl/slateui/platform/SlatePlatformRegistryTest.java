package top.huliawsl.slateui.platform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import top.huliawsl.slateui.platform.services.SlatePlatformRegistry;

class SlatePlatformRegistryTest {

    @Test
    void storesConfigScreensAndLifecycleHooks() {
        SlatePlatformRegistry registry = new SlatePlatformRegistry();
        AtomicInteger ticks = new AtomicInteger();
        registry.registerConfigScreen("demo", parent -> null);
        registry.registerTickListener("tick", ticks::incrementAndGet);
        registry.fireClientTick();

        assertTrue(registry.configScreen("demo").isPresent());
        assertEquals(1, ticks.get());
    }
}
