package top.huliawsl.slateui.api.container;

public record SlotValidationResult(boolean valid, String message) {

    public static SlotValidationResult ok() {
        return new SlotValidationResult(true, "");
    }

    public static SlotValidationResult error(String message) {
        return new SlotValidationResult(false, message == null ? "" : message);
    }
}
