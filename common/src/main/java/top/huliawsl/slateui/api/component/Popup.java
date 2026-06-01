package top.huliawsl.slateui.api.component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.lwjgl.glfw.GLFW;
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
    private final String closeCommand;
    private final boolean closeOnOutsideClick;
    private final boolean closeOnEscape;
    private final boolean consumeOutsidePointer;
    private Rect popupBounds = Rect.ZERO;

    public Popup(SlateComponent anchor, SlateComponent popup, Supplier<Object> openSupplier, SlateStyle style) {
        this(anchor, popup, openSupplier, null, false, false, false, style);
    }

    public Popup(
        SlateComponent anchor,
        SlateComponent popup,
        Supplier<Object> openSupplier,
        String closeCommand,
        boolean closeOnOutsideClick,
        boolean closeOnEscape,
        boolean consumeOutsidePointer,
        SlateStyle style
    ) {
        super(style);
        this.anchor = Objects.requireNonNull(anchor, "anchor");
        this.popup = Objects.requireNonNull(popup, "popup");
        this.openSupplier = Objects.requireNonNull(openSupplier, "openSupplier");
        this.closeCommand = closeCommand;
        this.closeOnOutsideClick = closeOnOutsideClick;
        this.closeOnEscape = closeOnEscape;
        this.consumeOutsidePointer = consumeOutsidePointer;
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
        if (open()) {
            if (popup.mouseClicked(context, mouseX, mouseY, button)) {
                return true;
            }
            if (popupBounds.contains(mouseX, mouseY)) {
                return true;
            }
            if (!anchor.bounds().contains(mouseX, mouseY) && closeOnOutsideClick) {
                return close(context, "outside-click") || consumeOutsidePointer;
            }
        }
        boolean handled = anchor.mouseClicked(context, mouseX, mouseY, button);
        return handled || (open() && consumeOutsidePointer);
    }

    @Override
    public boolean mouseReleased(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        if (open()) {
            if (popup.mouseReleased(context, mouseX, mouseY, button)) {
                return true;
            }
            if (popupBounds.contains(mouseX, mouseY)) {
                return true;
            }
            if (!anchor.bounds().contains(mouseX, mouseY) && closeOnOutsideClick) {
                return close(context, "outside-release") || consumeOutsidePointer;
            }
        }
        boolean handled = anchor.mouseReleased(context, mouseX, mouseY, button);
        return handled || (open() && consumeOutsidePointer);
    }

    @Override
    public boolean mouseMoved(SlateInteractionContext context, double mouseX, double mouseY) {
        if (open()) {
            boolean handled = popup.mouseMoved(context, mouseX, mouseY);
            if (!consumeOutsidePointer || anchor.bounds().contains(mouseX, mouseY)) {
                handled |= anchor.mouseMoved(context, mouseX, mouseY);
            }
            return handled;
        }
        return anchor.mouseMoved(context, mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(SlateInteractionContext context, double mouseX, double mouseY, double delta) {
        if (open()) {
            if (popup.mouseScrolled(context, mouseX, mouseY, delta)) {
                return true;
            }
            if (popupBounds.contains(mouseX, mouseY)) {
                return true;
            }
            if (consumeOutsidePointer && !anchor.bounds().contains(mouseX, mouseY)) {
                return true;
            }
        }
        return anchor.mouseScrolled(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(SlateInteractionContext context, int keyCode, int scanCode, int modifiers) {
        if (open()) {
            if (closeOnEscape && keyCode == GLFW.GLFW_KEY_ESCAPE) {
                return close(context, "escape");
            }
            if (popup.keyPressed(context, keyCode, scanCode, modifiers)) {
                return true;
            }
            return !consumeOutsidePointer && anchor.keyPressed(context, keyCode, scanCode, modifiers);
        }
        return anchor.keyPressed(context, keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(SlateInteractionContext context, char codePoint, int modifiers) {
        if (open()) {
            if (popup.charTyped(context, codePoint, modifiers)) {
                return true;
            }
            return !consumeOutsidePointer && anchor.charTyped(context, codePoint, modifiers);
        }
        return anchor.charTyped(context, codePoint, modifiers);
    }

    protected final boolean open() {
        return BindingEvaluator.isTruthy(openSupplier.get());
    }

    protected final Rect popupBounds() {
        return popupBounds;
    }

    protected final boolean close(SlateInteractionContext context, String reason) {
        if (closeCommand == null || closeCommand.isBlank()) {
            return false;
        }
        return context.commands().execute(closeCommand, context, Map.of("reason", reason, "component", debugPath()));
    }
}
