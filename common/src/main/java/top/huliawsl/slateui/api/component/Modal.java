package top.huliawsl.slateui.api.component;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.binding.BindingEvaluator;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.DrawRectCommand;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

/**
 * Experimental component. It is available for lightweight overlays, but it is not part of the stable core component contract.
 */
public final class Modal extends SlateComponent {

    private static final int BACKDROP_COLOR = 0xAA020617;

    private final SlateComponent content;
    private final SlateComponent modal;
    private final Supplier<Object> openSupplier;

    public Modal(SlateComponent content, SlateComponent modal, Supplier<Object> openSupplier, SlateStyle style) {
        super(style);
        this.content = Objects.requireNonNull(content, "content");
        this.modal = Objects.requireNonNull(modal, "modal");
        this.openSupplier = Objects.requireNonNull(openSupplier, "openSupplier");
    }

    @Override
    public List<SlateComponent> children() {
        return open() ? List.of(content, modal) : List.of(content);
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        Size measured = measureChild(context, content, available);
        if (open()) {
            measureChild(context, modal, available);
            setMeasuredSize(available);
            return available;
        }
        setMeasuredSize(measured);
        return measured;
    }

    @Override
    public void layout(SlateLayoutContext context, Rect bounds) {
        setBounds(bounds);
        Size anchorSize = content.layoutNode().measuredSize();
        Rect anchorBounds = new Rect(bounds.x(), bounds.y(), anchorSize.width(), anchorSize.height());
        layoutChild(context, content, anchorBounds);
        if (open()) {
            Size modalSize = modal.layoutNode().measuredSize();
            int x = bounds.x() + Math.max(0, (bounds.width() - modalSize.width()) / 2);
            int y = bounds.y() + Math.max(0, (bounds.height() - modalSize.height()) / 2);
            layoutChild(context, modal, new Rect(x, y, modalSize.width(), modalSize.height()));
        }
    }

    @Override
    public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
        collectChild(context, commands, content);
        if (open()) {
            commands.add(new DrawRectCommand(bounds(), BACKDROP_COLOR));
            collectChild(context, commands, modal);
        }
    }

    @Override
    public boolean mouseClicked(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        if (!open()) {
            return content.mouseClicked(context, mouseX, mouseY, button);
        }
        return modal.mouseClicked(context, mouseX, mouseY, button) || bounds().contains(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        if (!open()) {
            return content.mouseReleased(context, mouseX, mouseY, button);
        }
        return modal.mouseReleased(context, mouseX, mouseY, button) || bounds().contains(mouseX, mouseY);
    }

    @Override
    public boolean mouseMoved(SlateInteractionContext context, double mouseX, double mouseY) {
        return open() ? modal.mouseMoved(context, mouseX, mouseY) : content.mouseMoved(context, mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(SlateInteractionContext context, double mouseX, double mouseY, double delta) {
        if (!open()) {
            return content.mouseScrolled(context, mouseX, mouseY, delta);
        }
        return modal.mouseScrolled(context, mouseX, mouseY, delta) || bounds().contains(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(SlateInteractionContext context, int keyCode, int scanCode, int modifiers) {
        return open() ? modal.keyPressed(context, keyCode, scanCode, modifiers) : content.keyPressed(context, keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(SlateInteractionContext context, char codePoint, int modifiers) {
        return open() ? modal.charTyped(context, codePoint, modifiers) : content.charTyped(context, codePoint, modifiers);
    }

    private boolean open() {
        return BindingEvaluator.isTruthy(openSupplier.get());
    }
}
