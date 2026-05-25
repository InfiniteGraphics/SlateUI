package top.huliawsl.slateui.render;

import top.huliawsl.slateui.layout.Rect;

public record PushClipCommand(Rect rect) implements DrawCommand {

    @Override
    public String describe() {
        return "PushClip rect=" + rect;
    }
}
