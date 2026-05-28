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
    private String styleDump = "";
    private String lastEventDump = "";
    private int componentCount;
    private int drawCommandCount;
    private long rebuildNanos;
    private long layoutNanos;
    private SlateComponent capturedRoot;
    private final List<String> commandLog = new ArrayList<>();
    private final List<String> diagnosticsLog = new ArrayList<>();

    public void capture(SlateComponent root, List<DrawCommand> drawCommands, String focusDump, String bindingDump, String stateDump) {
        capture(root, drawCommands, focusDump, bindingDump, stateDump, Theme.DEFAULT);
    }

    public void capture(SlateComponent root, List<DrawCommand> drawCommands, String focusDump, String bindingDump, String stateDump, Theme theme) {
        capture(root, drawCommands, focusDump, bindingDump, stateDump, theme, root.dumpStyleTree(theme));
    }

    public void capture(SlateComponent root, List<DrawCommand> drawCommands, String focusDump, String bindingDump, String stateDump, Theme theme, String styleDump) {
        this.capturedRoot = root;
        this.componentTreeDump = root.dumpComponentTree();
        this.layoutDump = root.dumpLayoutTree();
        this.hitRegionDump = root.dumpHitRegionTree(theme);
        this.styleDump = styleDump == null ? "" : styleDump;
        this.drawCommandDump = drawCommands.stream()
            .map(DrawCommand::describe)
            .reduce((left, right) -> left + "\n" + right)
            .orElse("");
        this.focusDump = focusDump == null ? "" : focusDump;
        this.bindingDump = bindingDump == null ? "" : bindingDump;
        this.stateDump = stateDump == null ? "" : stateDump;
        this.componentCount = countComponents(root);
        this.drawCommandCount = drawCommands.size();
        if (componentCount > 250) {
            logDiagnostic("WARN component count high: " + componentCount);
        }
        if (drawCommandCount > 1000) {
            logDiagnostic("WARN draw command count high: " + drawCommandCount);
        }
    }

    public void captureTimings(long rebuildNanos, long layoutNanos) {
        this.rebuildNanos = Math.max(0, rebuildNanos);
        this.layoutNanos = Math.max(0, layoutNanos);
    }

    public void logMissingCommand(String id) {
        logDiagnostic("WARN missing command id=" + id);
    }

    public void logMissingTexture(String texture) {
        logDiagnostic("WARN missing texture=" + texture);
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

    public void captureEvent(String type, String path, boolean consumed) {
        lastEventDump = "type=" + (type == null ? "event" : type)
            + " path=" + (path == null || path.isBlank() ? "<none>" : path)
            + " consumed=" + consumed;
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
    public String styleDump() { return styleDump; }
    public String lastEventDump() { return lastEventDump; }
    public String runtimeSummaryDump() {
        return "components=" + componentCount
            + "\ndrawCommands=" + drawCommandCount
            + "\nrebuildMs=" + String.format("%.3f", rebuildNanos / 1_000_000.0D)
            + "\nlayoutMs=" + String.format("%.3f", layoutNanos / 1_000_000.0D);
    }
    public String commandLogDump() { return join(commandLog); }
    public String diagnosticsLogDump() { return join(diagnosticsLog); }

    private static String join(List<String> values) {
        return values.isEmpty() ? "" : String.join("\n", values);
    }
}
