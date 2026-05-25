package top.huliawsl.slateui.render;

import top.huliawsl.slateui.layout.Rect;

public record DrawRectCommand(Rect rect, int color) implements DrawCommand {

    @Override
    public String describe() {
        return "rect " + rect;
    }
}
