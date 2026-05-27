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

public final class Conditional extends SlateComponent {

    private final Supplier<Object> predicate;
    private final SlateComponent child;

    public Conditional(Supplier<Object> predicate, SlateComponent child) {
        this(predicate, child, SlateStyle.EMPTY);
    }

    public Conditional(Supplier<Object> predicate, SlateComponent child, SlateStyle style) {
        super(style);
        this.predicate = Objects.requireNonNull(predicate, "predicate");
        this.child = Objects.requireNonNull(child, "child");
    }

    @Override
    public List<SlateComponent> children() {
        return visible() ? List.of(child) : List.of();
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        if (!visible()) {
            setMeasuredSize(Size.ZERO);
            return Size.ZERO;
        }
        Size measured = child.measure(context, available);
        setMeasuredSize(measured);
        return measured;
    }

    @Override
    public void layout(SlateLayoutContext context, Rect bounds) {
        setBounds(bounds);
        if (visible()) {
            child.layout(context, bounds);
        }
    }

    @Override
    public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
        if (visible()) {
            child.collectDrawCommands(context, commands);
        }
    }

    @Override
    public boolean mouseClicked(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        return visible() && child.mouseClicked(context, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        return visible() && child.mouseReleased(context, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseMoved(SlateInteractionContext context, double mouseX, double mouseY) {
        return visible() && child.mouseMoved(context, mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(SlateInteractionContext context, double mouseX, double mouseY, double delta) {
        return visible() && child.mouseScrolled(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(SlateInteractionContext context, int keyCode, int scanCode, int modifiers) {
        return visible() && child.keyPressed(context, keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(SlateInteractionContext context, char codePoint, int modifiers) {
        return visible() && child.charTyped(context, codePoint, modifiers);
    }

    private boolean visible() {
        return BindingEvaluator.isTruthy(predicate.get());
    }
}
