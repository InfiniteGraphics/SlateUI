package top.huliawsl.slateui.authoring;

public record SlateReloadResult(boolean success, String message) {

    public static SlateReloadResult success(String message) {
        return new SlateReloadResult(true, message == null ? "" : message);
    }

    public static SlateReloadResult failure(String message) {
        return new SlateReloadResult(false, message == null ? "" : message);
    }
}
