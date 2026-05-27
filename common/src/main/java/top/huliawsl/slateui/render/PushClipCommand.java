package top.huliawsl.slateui.render;

import top.huliawsl.slateui.layout.Rect;

public record PushClipCommand(Rect rect, int radius) implements DrawCommand {

    public PushClipCommand(Rect rect) {
        this(rect, 0);
    }

    public PushClipCommand {
        radius = Math.max(0, radius);
    }

    @Override
    public String describe() {
        return "PushClip rect=" + rect + " radius=" + radius;
    }
}
