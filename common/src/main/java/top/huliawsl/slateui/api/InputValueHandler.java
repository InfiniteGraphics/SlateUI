package top.huliawsl.slateui.api;

import top.huliawsl.slateui.runtime.SlateInteractionContext;

@FunctionalInterface
public interface InputValueHandler {

    void onChange(SlateInteractionContext context, String value);
}
