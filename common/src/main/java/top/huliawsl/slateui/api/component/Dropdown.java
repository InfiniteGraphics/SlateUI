package top.huliawsl.slateui.api.component;

import java.util.List;
import java.util.Map;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.debug.SlateRuntimeException;
import top.huliawsl.slateui.runtime.SlateInteractionContext;

public final class Dropdown extends Button {

    private final List<String> options;
    private final int selectedIndex;

    public Dropdown(List<String> options, int selectedIndex, String commandId, SlateStyle style) {
        super(label(options, selectedIndex), commandId, style);
        this.options = List.copyOf(options == null ? List.of() : options);
        this.selectedIndex = Math.max(0, Math.min(Math.max(0, this.options.size() - 1), selectedIndex));
    }

    public List<String> options() {
        return options;
    }

    public String selectedValue() {
        return options.isEmpty() ? "" : options.get(selectedIndex);
    }

    @Override
    public boolean mouseReleased(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        boolean handled = super.mouseReleased(context, mouseX, mouseY, button);
        if (handled && commandId() != null && !commandId().isBlank()) {
            try {
                context.commands().execute(commandId(), context, Map.of("selectedIndex", selectedIndex, "selectedValue", selectedValue()));
            } catch (Throwable throwable) {
                throw SlateRuntimeException.command(this, commandId(), throwable);
            }
        }
        return handled;
    }

    private static String label(List<String> options, int selectedIndex) {
        if (options == null || options.isEmpty()) {
            return "Select";
        }
        int index = Math.max(0, Math.min(options.size() - 1, selectedIndex));
        return options.get(index);
    }
}
