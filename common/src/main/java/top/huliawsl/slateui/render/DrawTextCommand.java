package top.huliawsl.slateui.render;

import top.huliawsl.slateui.api.SlateText;

public record DrawTextCommand(int x, int y, SlateText slateText, int color) implements DrawCommand {

    public DrawTextCommand(int x, int y, String text, int color) {
        this(x, y, new SlateText.Literal(text), color);
    }

    public DrawTextCommand {
        slateText = slateText == null ? new SlateText.Literal("") : slateText;
    }

    public String text() {
        return slateText.fallbackText();
    }

    @Override
    public String describe() {
        return "text \"" + text() + "\" @(" + x + "," + y + ")";
    }
}
