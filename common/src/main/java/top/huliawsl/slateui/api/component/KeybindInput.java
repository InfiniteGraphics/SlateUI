package top.huliawsl.slateui.api.component;

import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.runtime.InvalidationType;
import top.huliawsl.slateui.runtime.SlateInteractionContext;

public final class KeybindInput extends Button {

    private String keyName;

    public KeybindInput(String keyName, String commandId, SlateStyle style) {
        super(keyName == null || keyName.isBlank() ? "Unbound" : keyName, commandId, style);
        this.keyName = keyName == null ? "" : keyName;
    }

    public String keyName() {
        return keyName;
    }

    @Override
    public boolean keyPressed(SlateInteractionContext context, int keyCode, int scanCode, int modifiers) {
        if (!isFocused()) {
            return false;
        }
        keyName = "key." + keyCode;
        context.requestInvalidation(InvalidationType.PAINT, "keybind-change");
        return true;
    }
}
