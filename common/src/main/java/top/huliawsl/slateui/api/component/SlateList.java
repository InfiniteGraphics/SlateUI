package top.huliawsl.slateui.api.component;

import java.util.Map;
import top.huliawsl.slateui.api.SlateBorder;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateCompositeComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;
import top.huliawsl.slateui.layout.Insets;

public class SlateList extends SlateCompositeComponent {

    private static final SlateStyle DEFAULT_STYLE = SlateStyle.builder()
        .padding(Insets.all(6))
        .backgroundColor(0xFF020617)
        .border(new SlateBorder(0xFF334155, 1))
        .borderRadiusToken("radius.md")
        .clipContent(true)
        .build();

    private final SlateStyle itemContainerStyle;

    public SlateList(java.util.List<SlateComponent> children, SlateStyle style) {
        this(children, Map.of(), style, SlateStyle.builder().gap(6).build());
    }

    public SlateList(java.util.List<SlateComponent> children, Map<String, java.util.List<SlateComponent>> namedSlots, SlateStyle style, SlateStyle itemContainerStyle) {
        super(children, namedSlots, SlateStyle.withDefaults(DEFAULT_STYLE, style));
        this.itemContainerStyle = itemContainerStyle == null ? SlateStyle.builder().gap(6).build() : itemContainerStyle;
    }

    @Override
    protected SlateComponent compose() {
        return new ScrollView(
            new Stack(StackDirection.COLUMN, slotChildren(), itemContainerStyle),
            style()
        );
    }
}
