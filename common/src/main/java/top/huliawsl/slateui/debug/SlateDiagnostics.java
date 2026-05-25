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
    private String focusDump = "";
    private String bindingDump = "";
    private String stateDump = "";
    private final List<String> commandLog = new ArrayList<>();
    private final List<String> diagnosticsLog = new ArrayList<>();

    public void capture(SlateComponent root, List<DrawCommand> drawCommands, String focusDump, String bindingDump, String stateDump) {
        this.componentTreeDump = root.dumpComponentTree();
        this.layoutDump = root.dumpLayoutTree();
        this.drawCommandDump = drawCommands.stream()
            .map(DrawCommand::describe)
            .reduce((left, right) -> left + "\n" + right)
            .orElse("");
        this.focusDump = focusDump == null ? "" : focusDump;
        this.bindingDump = bindingDump == null ? "" : bindingDump;
        this.stateDump = stateDump == null ? "" : stateDump;
    }

    public void logCommand(String entry) {
        append(commandLog, entry);
    }

    public void logDiagnostic(String entry) {
        append(diagnosticsLog, entry);
    }

    private static void append(List<String> target, String entry) {
        if (target.size() == MAX_LOG_ENTRIES) {
            target.remove(0);
        }
        target.add(entry);
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

    public String focusDump() {
        return focusDump;
    }

    public String bindingDump() {
        return bindingDump;
    }

    public String stateDump() {
        return stateDump;
    }

    public String commandLogDump() {
        return join(commandLog);
    }

    public String diagnosticsLogDump() {
        return join(diagnosticsLog);
    }

    private static String join(List<String> values) {
        return values.isEmpty() ? "" : String.join("\n", values);
    }
}
