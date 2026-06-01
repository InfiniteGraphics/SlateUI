package top.huliawsl.slateui.api.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;

public final class RadioGroup extends Stack {

    public RadioGroup(List<String> options, int selectedIndex, String changeCommand, SlateStyle style) {
        super(StackDirection.COLUMN, createOptions(options, selectedIndex, changeCommand), style);
    }

    private static List<SlateComponent> createOptions(List<String> options, int selectedIndex, String changeCommand) {
        List<SlateComponent> children = new ArrayList<>();
        List<String> safeOptions = options == null ? List.of() : options;
        for (int index = 0; index < safeOptions.size(); index++) {
            String prefix = index == selectedIndex ? "(*) " : "( ) ";
            children.add(new Button(
                prefix + safeOptions.get(index),
                changeCommand,
                Map.of("selectedIndex", index, "selectedValue", safeOptions.get(index)),
                SlateStyle.EMPTY
            ).componentKey(String.valueOf(index)));
        }
        return children;
    }
}
