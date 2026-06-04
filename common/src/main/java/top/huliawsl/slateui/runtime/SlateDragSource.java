package top.huliawsl.slateui.runtime;

public interface SlateDragSource {

    SlateDragPayload beginDrag(SlateInteractionContext context, double mouseX, double mouseY, int button);
}
