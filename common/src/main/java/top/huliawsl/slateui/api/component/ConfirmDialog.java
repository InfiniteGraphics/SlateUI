package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public final class ConfirmDialog extends SlateComponent {

    private final Modal modal;

    public ConfirmDialog(String title, String message, String confirmCommand, String cancelCommand, SlateStyle style) {
        super(style);
        this.modal = new Modal(
            new Box(List.of(), SlateStyle.EMPTY),
            new Panel(title, List.of(
                new Text(message),
                new Stack(StackDirection.ROW, List.of(
                    new Button("Cancel", cancelCommand, SlateStyle.EMPTY),
                    new Button("OK", confirmCommand, SlateStyle.EMPTY)
                ), SlateStyle.builder().gap(6).build())
            ), SlateStyle.EMPTY),
            () -> true,
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

    @Override
    public boolean mouseClicked(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        return modal.mouseClicked(context, mouseX, mouseY, button);
    }
}
