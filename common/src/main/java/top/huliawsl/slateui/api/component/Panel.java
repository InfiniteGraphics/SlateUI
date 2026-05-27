package top.huliawsl.slateui.api.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import top.huliawsl.slateui.api.SlateBorder;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateCompositeComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;
import top.huliawsl.slateui.layout.Insets;

public class Panel extends SlateCompositeComponent {

    private static final SlateStyle DEFAULT_STYLE = SlateStyle.builder()
        .padding(Insets.all(12))
        .backgroundToken("color.panel")
        .border(new SlateBorder(0xFF334155, 1))
        .borderColorToken("color.border")
        .borderRadiusToken("radius.md")
        .clipContent(true)
        .build();

    private final String title;
    private final SlateStyle contentStyle;

    public Panel(String title, List<SlateComponent> children, SlateStyle style) {
        this(title, children, Map.of(), style, SlateStyle.builder().gap(8).build());
    }

    public Panel(String title, List<SlateComponent> children, Map<String, List<SlateComponent>> namedSlots, SlateStyle style, SlateStyle contentStyle) {
        super(children, namedSlots, SlateStyle.withDefaults(DEFAULT_STYLE, style));
        this.title = title == null ? "" : title;
        this.contentStyle = contentStyle == null ? SlateStyle.builder().gap(8).build() : contentStyle;
    }

    @Override
    protected SlateComponent compose() {
        List<SlateComponent> content = new ArrayList<>();
        List<SlateComponent> header = slotChildren("header");
        if (!header.isEmpty()) {
            content.addAll(header);
        } else if (!title.isBlank()) {
            content.add(new Text(title));
        }
        content.add(new Stack(StackDirection.COLUMN, slotChildren(), contentStyle));
        content.addAll(slotChildren("footer"));
        return new Stack(StackDirection.COLUMN, content, style());
    }
}
