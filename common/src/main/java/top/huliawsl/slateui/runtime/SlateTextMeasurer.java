package top.huliawsl.slateui.runtime;

import top.huliawsl.slateui.api.SlateText;

public interface SlateTextMeasurer {

    SlateTextMeasurer FALLBACK = new SlateTextMeasurer() {
        @Override
        public int width(SlateText text) {
            String value = text == null ? "" : text.fallbackText();
            return value.length() * 6;
        }

        @Override
        public int lineHeight() {
            return 9;
        }
    };

    int width(SlateText text);

    default int width(String text) {
        return width(new SlateText.Literal(text));
    }

    int lineHeight();
}
