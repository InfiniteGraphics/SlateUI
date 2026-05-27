package top.huliawsl.slateui.render;

import top.huliawsl.slateui.layout.Rect;

public record DrawRectCommand(Rect rect, int color, int radius) implements DrawCommand {

    public DrawRectCommand(Rect rect, int color) {
        this(rect, color, 0);
    }

    public DrawRectCommand {
        radius = Math.max(0, radius);
    }

    @Override
    public String describe() {
        return "rect " + rect + " color=#" + String.format("%08X", color) + " radius=" + radius;
    }
}
