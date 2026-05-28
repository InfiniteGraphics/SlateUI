package top.huliawsl.slateui.render;

import top.huliawsl.slateui.layout.Rect;

public record DrawItemIconCommand(Rect rect, String itemId, int count) implements DrawCommand {

    @Override
    public String describe() {
        return "item " + itemId + " x" + count + " " + rect;
    }
}
