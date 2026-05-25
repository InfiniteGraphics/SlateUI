package top.huliawsl.slateui.render;

public record PopClipCommand() implements DrawCommand {

    @Override
    public String describe() {
        return "PopClip";
    }
}
