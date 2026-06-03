package top.huliawsl.slateui.api.component;

import top.huliawsl.slateui.runtime.SlateInteractionContext;

@FunctionalInterface
public interface CanvasInputHandler {

    boolean handle(SlateInteractionContext context, CanvasPointerEvent event);
}
