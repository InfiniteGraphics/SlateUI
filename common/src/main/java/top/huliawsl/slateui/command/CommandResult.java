package top.huliawsl.slateui.command;

public record CommandResult(boolean executed, String message) {

    public static final CommandResult EXECUTED = new CommandResult(true, "executed");
    public static final CommandResult MISSING = new CommandResult(false, "missing");

    public static CommandResult executed(String message) {
        return new CommandResult(true, message == null || message.isBlank() ? "executed" : message);
    }

    public static CommandResult missing(String message) {
        return new CommandResult(false, message == null || message.isBlank() ? "missing" : message);
    }
}
