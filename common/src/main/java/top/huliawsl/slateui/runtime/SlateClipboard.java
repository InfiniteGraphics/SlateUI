package top.huliawsl.slateui.runtime;

public interface SlateClipboard {

    SlateClipboard EMPTY = new SlateClipboard() {
        @Override
        public String get() {
            return "";
        }

        @Override
        public void set(String value) {
        }
    };

    String get();

    void set(String value);
}
