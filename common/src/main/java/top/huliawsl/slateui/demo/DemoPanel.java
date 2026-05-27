package top.huliawsl.slateui.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import top.huliawsl.slateui.api.SlateCompositeComponent;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;
import top.huliawsl.slateui.api.component.Stack;
import top.huliawsl.slateui.api.component.Text;

public final class DemoPanel extends SlateCompositeComponent {

    private final String title;
    private final SlateStyle panelStyle;
    private final SlateStyle contentStyle;

    public DemoPanel(String title, List<SlateComponent> children, SlateStyle panelStyle, SlateStyle contentStyle) {
        this(title, children, Map.of(), panelStyle, contentStyle);
    }

    public DemoPanel(String title, List<SlateComponent> children, Map<String, List<SlateComponent>> namedSlots, SlateStyle panelStyle, SlateStyle contentStyle) {
        super(children, namedSlots, panelStyle);
        this.title = title;
        this.panelStyle = panelStyle;
        this.contentStyle = contentStyle;
    }

    @Override
    protected SlateComponent compose() {
        List<SlateComponent> content = new ArrayList<>();
        List<SlateComponent> header = slotChildren("header");
        if (header.isEmpty()) {
            content.add(new Text(title));
        } else {
            content.addAll(header);
        }
        content.add(new Stack(StackDirection.COLUMN, slotChildren(), contentStyle));
        content.addAll(slotChildren("footer"));
        return new Stack(StackDirection.COLUMN, content, panelStyle);
    }
}
