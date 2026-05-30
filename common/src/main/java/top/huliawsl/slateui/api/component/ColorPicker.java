package top.huliawsl.slateui.api.component;

import java.util.ArrayList;
import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;

public final class ColorPicker extends Stack {

    private static final List<Integer> DEFAULT_SWATCHES = List.of(
        0xFFFFFFFF, 0xFFEF4444, 0xFFF59E0B, 0xFF22C55E,
        0xFF06B6D4, 0xFF3B82F6, 0xFFA855F7, 0xFF111827
    );

    public ColorPicker(int selectedColor, String changeCommand, SlateStyle style) {
        this(DEFAULT_SWATCHES, selectedColor, changeCommand, style);
    }

    public ColorPicker(List<Integer> swatches, int selectedColor, String changeCommand, SlateStyle style) {
        super(StackDirection.ROW, createSwatches(swatches, selectedColor, changeCommand), style);
    }

    private static List<SlateComponent> createSwatches(List<Integer> swatches, int selectedColor, String changeCommand) {
        List<SlateComponent> children = new ArrayList<>();
        for (Integer color : swatches == null ? DEFAULT_SWATCHES : swatches) {
            int value = color == null ? 0xFFFFFFFF : color;
            SlateStyle swatchStyle = SlateStyle.builder()
                .width(16)
                .height(16)
                .backgroundColor(value)
                .border(new top.huliawsl.slateui.api.SlateBorder(value == selectedColor ? 0xFFFFFFFF : 0xFF334155, 1))
                .borderRadius(2)
                .build();
            children.add(new Button(List.of(), changeCommand, swatchStyle).componentKey(String.format("%08X", value)));
        }
        return children;
    }
}
