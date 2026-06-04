package top.huliawsl.slateui.editor;

import java.util.Objects;

public record SlateRunnableUndoableAction(String label, Runnable undoAction, Runnable redoAction) implements UndoableAction {

    public SlateRunnableUndoableAction {
        label = label == null || label.isBlank() ? "Edit" : label;
        undoAction = Objects.requireNonNull(undoAction, "undoAction");
        redoAction = Objects.requireNonNull(redoAction, "redoAction");
    }

    @Override
    public void undo() {
        undoAction.run();
    }

    @Override
    public void redo() {
        redoAction.run();
    }
}
