package top.huliawsl.slateui.api.component;

import java.util.ArrayList;
import java.util.List;
import top.huliawsl.slateui.api.HorizontalAlign;
import top.huliawsl.slateui.api.SlateBorder;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateCompositeComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;
import top.huliawsl.slateui.layout.Insets;

public final class PropertyGrid extends SlateCompositeComponent {

    private static final SlateStyle DEFAULT_STYLE = SlateStyle.builder()
        .padding(Insets.all(6))
        .backgroundColor(0xFF020617)
        .border(new SlateBorder(0xFF334155, 1))
        .borderRadiusToken("radius.md")
        .clipContent(true)
        .build();

    private final List<PropertyDescriptor> properties;

    public PropertyGrid(List<PropertyDescriptor> properties, SlateStyle style) {
        super(List.of(), SlateStyle.withDefaults(DEFAULT_STYLE, style));
        this.properties = properties == null ? List.of() : List.copyOf(properties);
    }

    @Override
    protected SlateComponent compose() {
        List<SlateComponent> rows = new ArrayList<>();
        String currentGroup = null;
        for (PropertyDescriptor property : properties) {
            if (!property.group().equals(currentGroup)) {
                currentGroup = property.group();
                if (!currentGroup.isBlank()) {
                    rows.add(new Text(currentGroup, SlateStyle.builder().textColor(0xFF93C5FD).padding(new Insets(6, 6, 6, 2)).build()));
                }
            }
            rows.add(row(property));
        }
        return new ScrollView(new Stack(StackDirection.COLUMN, rows, SlateStyle.builder().gap(3).build()), style());
    }

    private static SlateComponent row(PropertyDescriptor property) {
        int valueColor = property.error() ? 0xFFFCA5A5 : property.readOnly() ? 0xFF94A3B8 : 0xFFE2E8F0;
        return new Stack(
            StackDirection.ROW,
            List.of(
                new Text(property.name(), SlateStyle.builder().width(140).textColor(0xFFCBD5E1).padding(Insets.symmetric(4, 3)).build()),
                new Text(property.value(), SlateStyle.builder().minWidth(80).textColor(valueColor).horizontalAlign(HorizontalAlign.END).padding(Insets.symmetric(4, 3)).build())
            ),
            SlateStyle.builder().backgroundColor(0x331E293B).borderRadius(3).build()
        );
    }
}
