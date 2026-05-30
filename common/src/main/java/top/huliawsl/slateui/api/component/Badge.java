package top.huliawsl.slateui.api.component;

import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.layout.Insets;

public final class Badge extends Text {

    public Badge(String label) {
        this(label, SlateStyle.EMPTY);
    }

    public Badge(String label, SlateStyle style) {
        super(label, SlateStyle.withDefaults(SlateStyle.builder()
            .padding(Insets.symmetric(6, 2))
            .backgroundColor(0xFF334155)
            .borderRadiusToken("radius.sm")
            .build(), style));
    }
}
