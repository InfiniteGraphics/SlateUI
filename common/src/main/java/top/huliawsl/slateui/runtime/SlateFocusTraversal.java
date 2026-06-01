package top.huliawsl.slateui.runtime;

import java.util.ArrayList;
import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;

public final class SlateFocusTraversal {

    private SlateFocusTraversal() {
    }

    public static List<SlateComponent> collect(SlateComponent root) {
        List<SlateComponent> order = new ArrayList<>();
        collect(root, order, true);
        return List.copyOf(order);
    }

    public static SlateComponent next(SlateComponent root, SlateComponent current, int direction) {
        List<SlateComponent> order = collect(root);
        if (order.isEmpty()) {
            return null;
        }
        int currentIndex = current == null ? -1 : order.indexOf(current);
        int step = direction < 0 ? -1 : 1;
        return order.get(Math.floorMod(currentIndex + step, order.size()));
    }

    private static void collect(SlateComponent component, List<SlateComponent> order, boolean enabled) {
        boolean componentEnabled = enabled && !component.style().disabled();
        if (!componentEnabled) {
            return;
        }
        if (component.acceptsKeyboardFocus()) {
            order.add(component);
        }
        for (SlateComponent child : component.children()) {
            collect(child, order, componentEnabled);
        }
    }
}
