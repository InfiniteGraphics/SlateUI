package top.huliawsl.slateui.api;

import top.huliawsl.slateui.runtime.SlateInteractionContext;

@FunctionalInterface
public interface ToggleValueHandler {
    void onChange(SlateInteractionContext context, boolean checked);
}
