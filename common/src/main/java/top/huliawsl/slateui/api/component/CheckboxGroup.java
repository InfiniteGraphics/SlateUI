package top.huliawsl.slateui.api.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;

public final class CheckboxGroup extends Stack {

    public CheckboxGroup(List<String> options, Set<String> selectedValues, String changeCommand, SlateStyle style) {
        super(StackDirection.COLUMN, createOptions(options, selectedValues, changeCommand), style);
    }

    private static List<SlateComponent> createOptions(List<String> options, Set<String> selectedValues, String changeCommand) {
        List<SlateComponent> children = new ArrayList<>();
        List<String> safeOptions = options == null ? List.of() : options;
        Set<String> selected = selectedValues == null ? Set.of() : selectedValues;
        for (String option : safeOptions) {
            boolean checked = selected.contains(option);
            String prefix = checked ? "[x] " : "[ ] ";
            children.add(new Button(
                prefix + option,
                changeCommand,
                Map.of("value", option, "selected", checked, "nextSelected", !checked),
                SlateStyle.EMPTY
            ).componentKey(option));
        }
        return children;
    }
}
