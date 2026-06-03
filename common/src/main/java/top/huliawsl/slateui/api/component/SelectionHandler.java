package top.huliawsl.slateui.api.component;

import top.huliawsl.slateui.runtime.SlateInteractionContext;

@FunctionalInterface
public interface SelectionHandler<T> {

    void onSelect(SlateInteractionContext context, T item, String key, int index);
}
