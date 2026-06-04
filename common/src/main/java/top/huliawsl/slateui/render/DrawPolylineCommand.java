package top.huliawsl.slateui.render;

import java.util.List;
import top.huliawsl.slateui.layout.Point;

public record DrawPolylineCommand(List<Point> points, int color, int thickness) implements DrawCommand {

    public DrawPolylineCommand {
        points = List.copyOf(points == null ? List.of() : points);
        thickness = Math.max(1, thickness);
    }

    @Override
    public String describe() {
        return "polyline points=" + points.size();
    }
}
