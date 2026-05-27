package top.huliawsl.slateui.runtime;

import top.huliawsl.slateui.api.Theme;

public record SlateLayoutContext(SlateTextMeasurer textMeasurer, Theme theme) {

    public SlateLayoutContext(SlateTextMeasurer textMeasurer) {
        this(textMeasurer, Theme.DEFAULT);
    }

    public SlateLayoutContext {
        textMeasurer = textMeasurer == null ? SlateTextMeasurer.FALLBACK : textMeasurer;
        theme = theme == null ? Theme.DEFAULT : theme;
    }

    public int textWidth(String text) {
        return textMeasurer.width(text);
    }

    public int lineHeight() {
        return textMeasurer.lineHeight();
    }
}
