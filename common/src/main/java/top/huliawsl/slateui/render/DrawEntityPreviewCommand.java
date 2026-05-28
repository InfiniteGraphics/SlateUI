package top.huliawsl.slateui.render;

import top.huliawsl.slateui.layout.Rect;

public record DrawEntityPreviewCommand(Rect rect, String entityType, float yaw, float pitch) implements DrawCommand {

    @Override
    public String describe() {
        return "entity " + entityType + " " + rect + " yaw=" + yaw + " pitch=" + pitch;
    }
}
