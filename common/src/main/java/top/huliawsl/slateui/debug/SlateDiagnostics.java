package top.huliawsl.slateui.debug;

import java.util.ArrayList;
import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.Theme;
import top.huliawsl.slateui.render.DrawCommand;

public final class SlateDiagnostics {

    private static final int MAX_LOG_ENTRIES = 32;

    private String componentTreeDump = "";
    private String layoutDump = "";
    private String drawCommandDump = "";
    private String focusDump = "";
    private String bindingDump = "";
    private String stateDump = "";
    private String hitTestDump = "";
    private String hitRegionDump = "";
    private int componentCount;
    private int drawCommandCount;
    private SlateComponent capturedRoot;
    private final List<String> commandLog = new ArrayList<>();
    private final List<String> diagnosticsLog = new ArrayList<>();

    public void capture(SlateComponent root, List<DrawCommand> drawCommands, String focusDump, String bindingDump, String stateDump) {
        capture(root, drawCommands, focusDump, bindingDump, stateDump, Theme.DEFAULT);
    }

    public void capture(SlateComponent root, List<DrawCommand> drawCommands, String focusDump, String bindingDump, String stateDump, Theme theme) {
        this.capturedRoot = root;
        this.componentTreeDump = root.dumpComponentTree();
        this.layoutDump = root.dumpLayoutTree();
        this.hitRegionDump = root.dumpHitRegionTree(theme);
        this.drawCommandDump = drawCommands.stream()
            .map(DrawCommand::describe)
            .reduce((left, right) -> left + "\n" + right)
            .orElse("");
        this.focusDump = focusDump == null ? "" : focusDump;
        this.bindingDump = bindingDump == null ? "" : bindingDump;
        this.stateDump = stateDump == null ? "" : stateDump;
        this.componentCount = countComponents(root);
        this.drawCommandCount = drawCommands.size();
    }

    public void capturePointer(double mouseX, double mouseY) {
        hitTestDump = componentPathAt(mouseX, mouseY);
    }

    public String componentPathAt(double mouseX, double mouseY) {
        if (capturedRoot == null) {
            return "<none>";
        }
        List<String> path = new ArrayList<>();
        if (!appendHitPath(capturedRoot, mouseX, mouseY, path)) {
            return "<none>";
        }
        return String.join(" > ", path);
    }

    public void logCommand(String entry) {
        append(commandLog, entry);
    }

    public void logDiagnostic(String entry) {
        append(diagnosticsLog, entry);
    }

    private static boolean appendHitPath(SlateComponent component, double mouseX, double mouseY, List<String> path) {
        if (!component.bounds().contains(mouseX, mouseY)) {
            return false;
        }
        path.add(component.debugName());
        List<SlateComponent> children = component.children();
        for (int index = children.size() - 1; index >= 0; index--) {
            if (appendHitPath(children.get(index), mouseX, mouseY, path)) {
                return true;
            }
        }
        return true;
    }

    private static int countComponents(SlateComponent component) {
        int total = 1;
        for (SlateComponent child : component.children()) {
            total += countComponents(child);
        }
        return total;
    }

    private static void append(List<String> target, String entry) {
        if (target.size() == MAX_LOG_ENTRIES) {
            target.remove(0);
        }
        target.add(entry);
    }

    public String componentTreeDump() { return componentTreeDump; }
    public String layoutDump() { return layoutDump; }
    public String drawCommandDump() { return drawCommandDump; }
    public String focusDump() { return focusDump; }
    public String bindingDump() { return bindingDump; }
    public String stateDump() { return stateDump; }
    public String hitTestDump() { return hitTestDump; }
    public String hitRegionDump() { return hitRegionDump; }
    public String runtimeSummaryDump() { return "components=" + componentCount + "\ndrawCommands=" + drawCommandCount; }
    public String commandLogDump() { return join(commandLog); }
    public String diagnosticsLogDump() { return join(diagnosticsLog); }

    private static String join(List<String> values) {
        return values.isEmpty() ? "" : String.join("\n", values);
    }
}
