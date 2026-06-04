package top.huliawsl.slateui.render;

import top.huliawsl.slateui.layout.Point;

public record DrawDashedLineCommand(Point start, Point end, int color, int thickness, int dashLength, int gapLength) implements DrawCommand {
    @Override
    public String describe() {
        return "dashed-line " + start + " -> " + end + " dash=" + dashLength + " gap=" + gapLength;
    }
}
