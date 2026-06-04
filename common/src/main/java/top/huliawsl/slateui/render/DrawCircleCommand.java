package top.huliawsl.slateui.render;

import top.huliawsl.slateui.layout.Point;

public record DrawCircleCommand(Point center, int radius, int color, boolean filled, int thickness) implements DrawCommand {
    @Override
    public String describe() {
        return (filled ? "filled-circle" : "circle") + " center=" + center + " radius=" + radius;
    }
}
