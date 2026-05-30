package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public final class CommandPalette extends SlateComponent {

    private final Modal modal;

    public CommandPalette(String query, List<SlateComponent> results, boolean open, SlateStyle style) {
        super(style);
        this.modal = new Modal(
            new Box(List.of(), SlateStyle.EMPTY),
            new Panel("Commands", List.of(
                new SearchBox(query, null, SlateStyle.builder().width(180).build()),
                new Stack(StackDirection.COLUMN, results == null ? List.of() : results, SlateStyle.builder().gap(4).build())
            ), SlateStyle.EMPTY),
            () -> open,
            style
        );
    }

    @Override
    public List<SlateComponent> children() {
        return List.of(modal);
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        Size measured = measureChild(context, modal, available);
        setMeasuredSize(measured);
        return measured;
    }

    @Override
    public void layout(SlateLayoutContext context, Rect bounds) {
        setBounds(bounds);
        layoutChild(context, modal, bounds);
    }

    @Override
    public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
        collectChild(context, commands, modal);
    }
}
