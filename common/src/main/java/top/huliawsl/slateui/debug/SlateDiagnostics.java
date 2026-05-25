package top.huliawsl.slateui.debug;

import java.util.ArrayList;
import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.render.DrawCommand;

public final class SlateDiagnostics {

    private static final int MAX_LOG_ENTRIES = 32;

    private String componentTreeDump = "";
    private String layoutDump = "";
    private String drawCommandDump = "";
    private final List<String> commandLog = new ArrayList<>();

    public void capture(SlateComponent root, List<DrawCommand> drawCommands) {
        this.componentTreeDump = root.dumpComponentTree();
        this.layoutDump = root.dumpLayoutTree();
        this.drawCommandDump = drawCommands.stream()
            .map(DrawCommand::describe)
            .reduce((left, right) -> left + "\n" + right)
            .orElse("");
    }

    public void logCommand(String entry) {
        if (commandLog.size() == MAX_LOG_ENTRIES) {
            commandLog.removeFirst();
        }
        commandLog.add(entry);
    }

    public String componentTreeDump() {
        return componentTreeDump;
    }

    public String layoutDump() {
        return layoutDump;
    }

    public String drawCommandDump() {
        return drawCommandDump;
    }

    public String commandLogDump() {
        return commandLog.isEmpty() ? "" : String.join("\n", commandLog);
    }
}
