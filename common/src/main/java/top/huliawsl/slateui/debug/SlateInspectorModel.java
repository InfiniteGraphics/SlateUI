package top.huliawsl.slateui.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import top.huliawsl.slateui.layout.Rect;

public final class SlateInspectorModel {

    private String selectedPath = "";
    private String hoverPath = "";
    private String searchQuery = "";
    private String commandFilter = "";
    private String eventFilter = "";
    private Rect liveBounds = Rect.ZERO;

    public String selectedPath() {
        return selectedPath;
    }

    public SlateInspectorModel select(String path) {
        selectedPath = path == null ? "" : path;
        return this;
    }

    public SlateInspectorModel hover(String path) {
        hoverPath = path == null ? "" : path;
        return this;
    }

    public SlateInspectorModel search(String query) {
        searchQuery = query == null ? "" : query;
        return this;
    }

    public SlateInspectorModel commandFilter(String filter) {
        commandFilter = filter == null ? "" : filter;
        return this;
    }

    public SlateInspectorModel eventFilter(String filter) {
        eventFilter = filter == null ? "" : filter;
        return this;
    }

    public SlateInspectorModel liveBounds(Rect bounds) {
        liveBounds = bounds == null ? Rect.ZERO : bounds;
        return this;
    }

    public List<String> selectedPanel(SlateDiagnostics diagnostics) {
        List<String> lines = new ArrayList<>();
        lines.add("Selected: " + blank(selectedPath));
        lines.add("Hover: " + blank(hoverPath));
        lines.add("Live bounds: " + liveBounds);
        lines.add("Style:");
        appendMatching(lines, diagnostics.styleDump(), selectedPath, 6);
        lines.add("State:");
        appendMatching(lines, diagnostics.stateDump(), searchQuery, 6);
        return List.copyOf(lines);
    }

    public String filteredCommandLog(SlateDiagnostics diagnostics) {
        return filterBlock(diagnostics.commandLogDump(), commandFilter);
    }

    public String filteredEventLog(SlateDiagnostics diagnostics) {
        return filterBlock(diagnostics.lastEventDump(), eventFilter);
    }

    public String copyReport(SlateDiagnostics diagnostics) {
        return "Runtime summary:\n" + diagnostics.runtimeSummaryDump()
            + "\n\nSelected:\n" + String.join("\n", selectedPanel(diagnostics))
            + "\n\nCommands:\n" + filteredCommandLog(diagnostics)
            + "\n\nEvents:\n" + filteredEventLog(diagnostics);
    }

    public String findComponentPath(SlateDiagnostics diagnostics) {
        if (searchQuery.isBlank()) {
            return "";
        }
        String needle = searchQuery.toLowerCase(Locale.ROOT);
        for (String line : diagnostics.componentTreeDump().split("\\R")) {
            if (line.toLowerCase(Locale.ROOT).contains(needle)) {
                return line.strip();
            }
        }
        return "";
    }

    private static void appendMatching(List<String> lines, String block, String query, int maxLines) {
        String filtered = filterBlock(block, query);
        if (filtered.isBlank()) {
            lines.add("  <empty>");
            return;
        }
        String[] split = filtered.split("\\R");
        for (int index = 0; index < split.length && index < maxLines; index++) {
            lines.add("  " + split[index]);
        }
    }

    private static String filterBlock(String block, String query) {
        if (block == null || block.isBlank()) {
            return "";
        }
        if (query == null || query.isBlank()) {
            return block;
        }
        String needle = query.toLowerCase(Locale.ROOT);
        List<String> lines = new ArrayList<>();
        for (String line : block.split("\\R")) {
            if (line.toLowerCase(Locale.ROOT).contains(needle)) {
                lines.add(line);
            }
        }
        return String.join("\n", lines);
    }

    private static String blank(String value) {
        return value == null || value.isBlank() ? "<none>" : value;
    }
}
