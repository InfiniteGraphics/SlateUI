package top.huliawsl.slateui.demo;

import java.util.List;
import top.huliawsl.slateui.api.SlateCompositeComponent;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;
import top.huliawsl.slateui.api.component.Stack;
import top.huliawsl.slateui.api.component.Text;

public final class InfoRow extends SlateCompositeComponent {

    private final String label;
    private final String value;

    public InfoRow(String label, String value) {
        super(List.of(), SlateStyle.EMPTY);
        this.label = label;
        this.value = value;
    }

    @Override
    protected SlateComponent compose() {
        return new Stack(
            StackDirection.ROW,
            List.of(
                new Text(label + ":"),
                new Text(value)
            ),
            SlateStyle.builder().gap(6).build()
        );
    }
}
