package top.huliawsl.slateui.editor;

public interface UndoableAction {

    String label();

    void undo();

    void redo();
}
