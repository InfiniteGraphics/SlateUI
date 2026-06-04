package top.huliawsl.slateui.editor;

import java.util.ArrayDeque;
import java.util.Deque;

public final class SlateUndoManager {

    private final Deque<UndoableAction> undoStack = new ArrayDeque<>();
    private final Deque<UndoableAction> redoStack = new ArrayDeque<>();
    private int limit = 128;

    public int limit() {
        return limit;
    }

    public SlateUndoManager limit(int limit) {
        this.limit = Math.max(1, limit);
        trimUndoStack();
        return this;
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public String nextUndoLabel() {
        return canUndo() ? undoStack.peek().label() : "";
    }

    public String nextRedoLabel() {
        return canRedo() ? redoStack.peek().label() : "";
    }

    public void push(UndoableAction action) {
        if (action == null) {
            return;
        }
        undoStack.push(action);
        redoStack.clear();
        trimUndoStack();
    }

    public boolean undo() {
        if (!canUndo()) {
            return false;
        }
        UndoableAction action = undoStack.pop();
        action.undo();
        redoStack.push(action);
        return true;
    }

    public boolean redo() {
        if (!canRedo()) {
            return false;
        }
        UndoableAction action = redoStack.pop();
        action.redo();
        undoStack.push(action);
        trimUndoStack();
        return true;
    }

    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }

    private void trimUndoStack() {
        while (undoStack.size() > limit) {
            undoStack.removeLast();
        }
    }
}
