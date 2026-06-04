package top.huliawsl.slateui.render;

import top.huliawsl.slateui.layout.Point;

public record DrawLineCommand(Point start, Point end, int color, int thickness) implements DrawCommand {

    public DrawLineCommand {
        start = start == null ? new Point(0, 0) : start;
        end = end == null ? start : end;
        thickness = Math.max(1, thickness);
    }

    @Override
    public String describe() {
        return "line (" + start.x() + "," + start.y() + ") -> (" + end.x() + "," + end.y() + ")";
    }
}
