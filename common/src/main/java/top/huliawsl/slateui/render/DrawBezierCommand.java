package top.huliawsl.slateui.render;

import top.huliawsl.slateui.layout.Point;

public record DrawBezierCommand(Point start, Point control1, Point control2, Point end, int color, int thickness) implements DrawCommand {
    @Override
    public String describe() {
        return "bezier " + start + " -> " + end + " thickness=" + thickness;
    }
}
