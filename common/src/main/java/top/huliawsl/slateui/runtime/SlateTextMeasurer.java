package top.huliawsl.slateui.runtime;

public interface SlateTextMeasurer {

    SlateTextMeasurer FALLBACK = new SlateTextMeasurer() {
        @Override
        public int width(String text) {
            return text == null ? 0 : text.length() * 6;
        }

        @Override
        public int lineHeight() {
            return 9;
        }
    };

    int width(String text);

    int lineHeight();
}
