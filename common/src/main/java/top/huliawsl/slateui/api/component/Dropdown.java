package top.huliawsl.slateui.api.component;

import java.util.List;
import java.util.Map;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateCompositeComponent;
import top.huliawsl.slateui.api.SlateStyle;

public final class Dropdown extends SlateCompositeComponent {

    private final List<String> options;
    private final int selectedIndex;
    private final boolean open;
    private final String toggleCommandId;
    private final String selectCommandId;

    public Dropdown(List<String> options, int selectedIndex, String commandId, SlateStyle style) {
        this(options, selectedIndex, false, commandId, commandId, style);
    }

    public Dropdown(List<String> options, int selectedIndex, boolean open, String toggleCommandId, String selectCommandId, SlateStyle style) {
        super(List.of(), style);
        this.options = List.copyOf(options == null ? List.of() : options);
        this.selectedIndex = Math.max(0, Math.min(Math.max(0, this.options.size() - 1), selectedIndex));
        this.open = open;
        this.toggleCommandId = toggleCommandId;
        this.selectCommandId = selectCommandId;
    }

    public List<String> options() {
        return options;
    }

    public String selectedValue() {
        return options.isEmpty() ? "" : options.get(selectedIndex);
    }

    @Override
    protected SlateComponent compose() {
        Button anchor = new Button(
            label(options, selectedIndex),
            toggleCommandId,
            Map.of(
                "action", open ? "close" : "open",
                "open", open,
                "selectedIndex", selectedIndex,
                "selectedValue", selectedValue()
            ),
            style()
        );
        SlateComponent menu = new Panel(null, optionButtons(), SlateStyle.builder().padding(top.huliawsl.slateui.layout.Insets.all(4)).gap(4).build());
        return new Popup(anchor, menu, () -> open, toggleCommandId, true, true, true, SlateStyle.EMPTY);
    }

    private static String label(List<String> options, int selectedIndex) {
        if (options == null || options.isEmpty()) {
            return "Select";
        }
        int index = Math.max(0, Math.min(options.size() - 1, selectedIndex));
        return options.get(index);
    }

    private List<SlateComponent> optionButtons() {
        java.util.ArrayList<SlateComponent> children = new java.util.ArrayList<>();
        for (int index = 0; index < options.size(); index++) {
            String option = options.get(index);
            children.add(new Button(
                (index == selectedIndex ? "* " : "") + option,
                selectCommandId,
                Map.of(
                    "action", "select",
                    "selectedIndex", index,
                    "selectedValue", option,
                    "open", false
                ),
                SlateStyle.EMPTY
            ).componentKey(String.valueOf(index)));
        }
        return children;
    }
}
