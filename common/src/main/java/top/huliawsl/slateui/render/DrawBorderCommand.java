package top.huliawsl.slateui.render;

import top.huliawsl.slateui.layout.Rect;

public record DrawBorderCommand(Rect rect, int color, int thickness) implements DrawCommand {

    @Override
    public String describe() {
        return "border " + rect + " thickness=" + thickness;
    }
}
