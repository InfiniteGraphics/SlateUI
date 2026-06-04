package top.huliawsl.slateui.render;

import top.huliawsl.slateui.layout.Point;

public record DrawArrowCommand(Point start, Point end, int color, int thickness, int headLength) implements DrawCommand {
    @Override
    public String describe() {
        return "arrow " + start + " -> " + end;
    }
}
