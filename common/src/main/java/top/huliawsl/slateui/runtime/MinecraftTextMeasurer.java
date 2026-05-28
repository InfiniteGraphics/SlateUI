package top.huliawsl.slateui.runtime;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import top.huliawsl.slateui.api.SlateText;

public record MinecraftTextMeasurer(Font font) implements SlateTextMeasurer {

    @Override
    public int width(SlateText text) {
        return font == null ? SlateTextMeasurer.FALLBACK.width(text) : font.width(toComponent(text));
    }

    @Override
    public int lineHeight() {
        return font == null ? SlateTextMeasurer.FALLBACK.lineHeight() : font.lineHeight;
    }

    private static Component toComponent(SlateText text) {
        return switch (text) {
            case SlateText.Literal literal -> Component.literal(literal.fallbackText());
            case SlateText.Translatable translatable -> Component.translatable(translatable.key(), translatable.args().toArray());
        };
    }
}
