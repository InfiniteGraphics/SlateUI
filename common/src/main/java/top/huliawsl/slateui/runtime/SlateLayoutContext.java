package top.huliawsl.slateui.runtime;

import net.minecraft.client.gui.Font;

public record SlateLayoutContext(Font font) {

    public int textWidth(String text) {
        return font.width(text);
    }

    public int lineHeight() {
        return font.lineHeight;
    }
}
