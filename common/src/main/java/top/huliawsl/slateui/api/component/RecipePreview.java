package top.huliawsl.slateui.api.component;

import java.util.ArrayList;
import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;

public class RecipePreview extends Stack {

    public RecipePreview(List<String> inputs, String output, SlateStyle style) {
        super(StackDirection.ROW, createItems(inputs, output), style);
    }

    private static List<SlateComponent> createItems(List<String> inputs, String output) {
        List<SlateComponent> children = new ArrayList<>();
        for (String input : inputs == null ? List.<String>of() : inputs) {
            children.add(new ItemIcon(input, 1, SlateStyle.EMPTY));
        }
        children.add(new Text("->"));
        children.add(new ItemIcon(output, 1, SlateStyle.EMPTY));
        return children;
    }
}
