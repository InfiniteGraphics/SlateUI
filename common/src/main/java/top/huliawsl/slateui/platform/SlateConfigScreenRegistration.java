package top.huliawsl.slateui.platform;

import top.huliawsl.slateui.api.SlateComponent;

public record SlateConfigScreenRegistration(String modId, SlateComponent root) {

    public SlateConfigScreenRegistration {
        modId = modId == null ? "" : modId;
    }
}
