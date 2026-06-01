package top.huliawsl.slateui.api.component;

import java.util.List;
import java.util.function.Supplier;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateCompositeComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;

public final class ConfirmDialog extends SlateCompositeComponent {

    private final String title;
    private final String message;
    private final String confirmCommand;
    private final String cancelCommand;
    private final Supplier<Object> openSupplier;

    public ConfirmDialog(String title, String message, String confirmCommand, String cancelCommand, SlateStyle style) {
        this(title, message, confirmCommand, cancelCommand, () -> true, style);
    }

    public ConfirmDialog(String title, String message, String confirmCommand, String cancelCommand, Supplier<Object> openSupplier, SlateStyle style) {
        super(List.of(), style);
        this.title = title == null ? "" : title;
        this.message = message == null ? "" : message;
        this.confirmCommand = confirmCommand;
        this.cancelCommand = cancelCommand;
        this.openSupplier = openSupplier == null ? () -> true : openSupplier;
    }

    @Override
    protected SlateComponent compose() {
        return new Modal(
            new Box(List.of(), SlateStyle.EMPTY),
            new Panel(title, List.of(
                new Text(message),
                new Stack(StackDirection.ROW, List.of(
                    new Button("Cancel", cancelCommand, java.util.Map.of("action", "cancel"), SlateStyle.EMPTY),
                    new Button("OK", confirmCommand, java.util.Map.of("action", "confirm"), SlateStyle.EMPTY)
                ), SlateStyle.builder().gap(6).build())
            ), SlateStyle.EMPTY),
            openSupplier,
            cancelCommand,
            true,
            true,
            style()
        );
    }
}
