package top.huliawsl.slateui.render;

public record PopTransformCommand() implements DrawCommand {

    @Override
    public String describe() {
        return "pop-transform";
    }
}
