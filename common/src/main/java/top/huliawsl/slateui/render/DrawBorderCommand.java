package top.huliawsl.slateui.render;

import top.huliawsl.slateui.layout.Rect;

public record DrawBorderCommand(Rect rect, int color, int thickness, int radius) implements DrawCommand {

    public DrawBorderCommand(Rect rect, int color, int thickness) {
        this(rect, color, thickness, 0);
    }

    public DrawBorderCommand {
        thickness = Math.max(0, thickness);
        radius = Math.max(0, radius);
    }

    @Override
    public String describe() {
        return "border " + rect + " thickness=" + thickness + " radius=" + radius;
    }
}
