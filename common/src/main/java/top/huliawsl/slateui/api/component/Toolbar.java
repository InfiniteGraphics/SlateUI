package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;
import top.huliawsl.slateui.layout.Insets;

public final class Toolbar extends Stack {

    private static final SlateStyle DEFAULT_STYLE = SlateStyle.builder()
        .padding(Insets.symmetric(6, 4))
        .gap(4)
        .backgroundColor(0xFF0F172A)
        .borderRadiusToken("radius.sm")
        .build();

    public Toolbar(List<SlateComponent> children) {
        this(children, SlateStyle.EMPTY);
    }

    public Toolbar(List<SlateComponent> children, SlateStyle style) {
        super(StackDirection.ROW, children, SlateStyle.withDefaults(DEFAULT_STYLE, style));
    }
}
