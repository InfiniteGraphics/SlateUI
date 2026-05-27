package top.huliawsl.slateui.runtime;

import net.minecraft.client.gui.Font;

public record MinecraftTextMeasurer(Font font) implements SlateTextMeasurer {

    @Override
    public int width(String text) {
        return font == null ? SlateTextMeasurer.FALLBACK.width(text) : font.width(text);
    }

    @Override
    public int lineHeight() {
        return font == null ? SlateTextMeasurer.FALLBACK.lineHeight() : font.lineHeight;
    }
}
