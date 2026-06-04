package top.huliawsl.slateui.runtime;

import top.huliawsl.slateui.api.SlateComponent;

public final class SlateDragDropManager {

    private SlateComponent source;
    private SlateDragPayload payload;
    private double startX;
    private double startY;
    private double currentX;
    private double currentY;

    public boolean dragging() {
        return payload != null;
    }

    public SlateComponent source() {
        return source;
    }

    public SlateDragPayload payload() {
        return payload;
    }

    public double startX() {
        return startX;
    }

    public double startY() {
        return startY;
    }

    public double currentX() {
        return currentX;
    }

    public double currentY() {
        return currentY;
    }

    public void begin(SlateComponent source, SlateDragPayload payload, double mouseX, double mouseY) {
        if (source == null || payload == null) {
            clear();
            return;
        }
        this.source = source;
        this.payload = payload;
        this.startX = mouseX;
        this.startY = mouseY;
        this.currentX = mouseX;
        this.currentY = mouseY;
    }

    public void update(double mouseX, double mouseY) {
        this.currentX = mouseX;
        this.currentY = mouseY;
    }

    public boolean tryDrop(SlateInteractionContext context, SlateDropTarget target, double mouseX, double mouseY) {
        if (!dragging() || target == null || !target.canDrop(context, payload, mouseX, mouseY)) {
            return false;
        }
        target.drop(context, payload, mouseX, mouseY);
        clear();
        return true;
    }

    public void clear() {
        source = null;
        payload = null;
        startX = 0D;
        startY = 0D;
        currentX = 0D;
        currentY = 0D;
    }
}
