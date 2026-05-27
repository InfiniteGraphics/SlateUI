package top.huliawsl.slateui.render;

import java.util.ArrayDeque;
import top.huliawsl.slateui.layout.Rect;

final class ClipStack {

    private final ArrayDeque<Entry> stack = new ArrayDeque<>();

    Entry push(Rect rect) {
        Rect effective = stack.isEmpty() ? rect : stack.peek().rect().intersect(rect);
        Entry entry = new Entry(effective, effective.width() > 0 && effective.height() > 0);
        stack.push(entry);
        return entry;
    }

    boolean popEnabled() {
        if (stack.isEmpty()) {
            return false;
        }
        return stack.pop().enabled();
    }

    boolean shouldSkip(DrawCommand command) {
        return isBlocked() && !(command instanceof PushClipCommand) && !(command instanceof PopClipCommand);
    }

    boolean isBlocked() {
        return !stack.isEmpty() && !stack.peek().enabled();
    }

    boolean isEmpty() {
        return stack.isEmpty();
    }

    record Entry(Rect rect, boolean enabled) {
    }
}
