package top.huliawsl.slateui.render;

import java.util.ArrayDeque;
import top.huliawsl.slateui.layout.Rect;

final class ClipStack {

    private final ArrayDeque<Entry> stack = new ArrayDeque<>();

    Entry push(Rect rect) {
        return push(rect, 0);
    }

    Entry push(Rect rect, int radius) {
        Rect effective = stack.isEmpty() ? rect : stack.peek().rect().intersect(rect);
        int requestedRadius = clampRadius(rect, radius);
        int effectiveRadius = requestedRadius;
        if (!stack.isEmpty()) {
            int parentRadius = stack.peek().radius();
            if (parentRadius > 0 && requestedRadius > 0) {
                effectiveRadius = Math.min(parentRadius, requestedRadius);
            } else if (parentRadius > 0) {
                effectiveRadius = parentRadius;
            }
        }
        effectiveRadius = clampRadius(effective, effectiveRadius);
        Entry entry = new Entry(effective, effectiveRadius, effective.width() > 0 && effective.height() > 0);
        stack.push(entry);
        return entry;
    }

    Entry current() {
        return stack.peek();
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

    private static int clampRadius(Rect rect, int radius) {
        if (radius <= 0 || rect.width() <= 0 || rect.height() <= 0) {
            return 0;
        }
        return Math.min(radius, Math.min(rect.width(), rect.height()) / 2);
    }

    record Entry(Rect rect, int radius, boolean enabled) {
    }
}
