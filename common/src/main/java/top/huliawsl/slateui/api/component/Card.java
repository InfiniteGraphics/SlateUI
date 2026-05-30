package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.SlateBorder;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.layout.Insets;

public final class Card extends Panel {

    private static final SlateStyle DEFAULT_STYLE = SlateStyle.builder()
        .padding(Insets.all(8))
        .backgroundColor(0xFF111827)
        .border(new SlateBorder(0xFF334155, 1))
        .borderRadiusToken("radius.md")
        .clipContent(true)
        .build();

    public Card(List<SlateComponent> children) {
        this(children, SlateStyle.EMPTY);
    }

    public Card(List<SlateComponent> children, SlateStyle style) {
        super("", children, SlateStyle.withDefaults(DEFAULT_STYLE, style));
    }
}
