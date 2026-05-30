package top.huliawsl.slateui.api.component;

import top.huliawsl.slateui.api.SlateStyle;

public final class KeybindLabel extends Text {

    public KeybindLabel(String keyName, SlateStyle style) {
        super(keyName == null || keyName.isBlank() ? "Unbound" : keyName, style);
    }
}
