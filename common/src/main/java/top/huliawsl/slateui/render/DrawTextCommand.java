package top.huliawsl.slateui.render;

public record DrawTextCommand(int x, int y, String text, int color) implements DrawCommand {

    @Override
    public String describe() {
        return "text \"" + text + "\" @(" + x + "," + y + ")";
    }
}
