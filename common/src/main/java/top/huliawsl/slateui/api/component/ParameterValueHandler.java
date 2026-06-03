package top.huliawsl.slateui.api.component;

import top.huliawsl.slateui.runtime.SlateInteractionContext;

@FunctionalInterface
public interface ParameterValueHandler {

    void onChange(SlateInteractionContext context, String key, Object value);
}
