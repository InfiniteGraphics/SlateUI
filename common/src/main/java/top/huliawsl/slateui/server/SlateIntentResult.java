package top.huliawsl.slateui.server;

public record SlateIntentResult(boolean accepted, String message) {

    public static SlateIntentResult ok() {
        return new SlateIntentResult(true, "");
    }

    public static SlateIntentResult rejected(String message) {
        return new SlateIntentResult(false, message == null ? "" : message);
    }
}
