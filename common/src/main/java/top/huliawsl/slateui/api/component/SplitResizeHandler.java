package top.huliawsl.slateui.api.component;

import top.huliawsl.slateui.runtime.SlateInteractionContext;

@FunctionalInterface
public interface SplitResizeHandler {

    void onResize(SlateInteractionContext context, float ratio, int firstPixels, int secondPixels);
}
