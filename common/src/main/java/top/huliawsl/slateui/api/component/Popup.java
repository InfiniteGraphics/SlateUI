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
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

/**
 * Experimental component. It is available for lightweight overlays, but it is not part of the stable core component contract.
 */
public class Popup extends SlateComponent {

    private final SlateComponent anchor;
    private final SlateComponent popup;
    private final Supplier<Object> openSupplier;
    private Rect popupBounds = Rect.ZERO;

    public Popup(SlateComponent anchor, SlateComponent popup, Supplier<Object> openSupplier, SlateStyle style) {
        super(style);
        this.anchor = Objects.requireNonNull(anchor, "anchor");
        this.popup = Objects.requireNonNull(popup, "popup");
        this.openSupplier = Objects.requireNonNull(openSupplier, "openSupplier");
    }

    @Override
    public List<SlateComponent> children() {
        return open() ? List.of(anchor, popup) : List.of(anchor);
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        Size measured = measureChild(context, anchor, available);
        if (open()) {
            measureChild(context, popup, available);
        }
        setMeasuredSize(measured);
        return measured;
    }

    @Override
    public void layout(SlateLayoutContext context, Rect bounds) {
        setBounds(bounds);
        Size anchorSize = anchor.layoutNode().measuredSize();
        Rect anchorBounds = new Rect(bounds.x(), bounds.y(), anchorSize.width(), anchorSize.height());
        layoutChild(context, anchor, anchorBounds);
        if (open()) {
            Size popupSize = popup.layoutNode().measuredSize();
            int popupX = anchorBounds.x();
            int popupY = anchorBounds.bottom() + 4;
            popupBounds = new Rect(popupX, popupY, popupSize.width(), popupSize.height());
            layoutChild(context, popup, popupBounds);
        } else {
            popupBounds = Rect.ZERO;
        }
    }

    @Override
    public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
        collectChild(context, commands, anchor);
        if (open()) {
            collectChild(context, commands, popup);
        }
    }

    @Override
    public boolean mouseClicked(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        if (open() && popup.mouseClicked(context, mouseX, mouseY, button)) {
            return true;
        }
        return anchor.mouseClicked(context, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        if (open() && popup.mouseReleased(context, mouseX, mouseY, button)) {
            return true;
        }
        return anchor.mouseReleased(context, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseMoved(SlateInteractionContext context, double mouseX, double mouseY) {
        boolean handled = anchor.mouseMoved(context, mouseX, mouseY);
        if (open()) {
            handled |= popup.mouseMoved(context, mouseX, mouseY);
        }
        return handled;
    }

    @Override
    public boolean mouseScrolled(SlateInteractionContext context, double mouseX, double mouseY, double delta) {
        if (open() && popup.mouseScrolled(context, mouseX, mouseY, delta)) {
            return true;
        }
        return anchor.mouseScrolled(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(SlateInteractionContext context, int keyCode, int scanCode, int modifiers) {
        if (open() && popup.keyPressed(context, keyCode, scanCode, modifiers)) {
            return true;
        }
        return anchor.keyPressed(context, keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(SlateInteractionContext context, char codePoint, int modifiers) {
        if (open() && popup.charTyped(context, codePoint, modifiers)) {
            return true;
        }
        return anchor.charTyped(context, codePoint, modifiers);
    }

    protected final boolean open() {
        return BindingEvaluator.isTruthy(openSupplier.get());
    }

    protected final Rect popupBounds() {
        return popupBounds;
    }
}
