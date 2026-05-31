package top.huliawsl.slateui.editor;

public final class SlateEditorDocument {

    private final String source;

    private SlateEditorDocument(String source) {
        this.source = source == null ? "" : source;
    }

    public static SlateEditorDocument importSlate(String source) {
        return new SlateEditorDocument(source);
    }

    public String exportSlate() {
        return source;
    }

    public String sourceMapTarget(int line, int column) {
        return "line=" + Math.max(1, line) + " column=" + Math.max(1, column);
    }
}
