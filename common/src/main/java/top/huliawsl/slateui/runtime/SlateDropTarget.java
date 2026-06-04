package top.huliawsl.slateui.runtime;

public interface SlateDropTarget {

    boolean canDrop(SlateInteractionContext context, SlateDragPayload payload, double mouseX, double mouseY);

    void drop(SlateInteractionContext context, SlateDragPayload payload, double mouseX, double mouseY);
}
