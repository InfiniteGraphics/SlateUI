package top.huliawsl.slateui.platform;

import java.util.ArrayList;
import java.util.List;

public final class SlatePlatformRegistration {

    private final List<SlateConfigScreenRegistration> configScreens = new ArrayList<>();

    public SlatePlatformRegistration registerConfigScreen(SlateConfigScreenRegistration registration) {
        if (registration != null) {
            configScreens.add(registration);
        }
        return this;
    }

    public List<SlateConfigScreenRegistration> configScreens() {
        return List.copyOf(configScreens);
    }
}
