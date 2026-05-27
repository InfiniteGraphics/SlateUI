package top.huliawsl.slateui.runtime;

import net.minecraft.client.gui.Font;

public record SlateLayoutContext(Font font) {

    public int textWidth(String text) {
        return font == null ? (text == null ? 0 : text.length() * 6) : font.width(text);
    }

    public int lineHeight() {
        return font == null ? 9 : font.lineHeight;
    }
}
