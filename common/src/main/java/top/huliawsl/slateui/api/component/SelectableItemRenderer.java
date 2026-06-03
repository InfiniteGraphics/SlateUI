package top.huliawsl.slateui.api.component;

import top.huliawsl.slateui.api.SlateComponent;

@FunctionalInterface
public interface SelectableItemRenderer<T> {

    SlateComponent render(T item, SelectableItemState state);
}
