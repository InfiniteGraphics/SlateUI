package top.huliawsl.slateui.api;

import java.util.List;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public abstract class SlateCompositeComponent extends SlateComponent {

    private final List<SlateComponent> slotChildren;
    private SlateComponent resolved;

    protected SlateCompositeComponent(List<SlateComponent> slotChildren, SlateStyle style) {
        super(style);
        this.slotChildren = List.copyOf(slotChildren);
    }

    protected final List<SlateComponent> slotChildren() {
        return slotChildren;
    }

    protected abstract SlateComponent compose();

    protected final SlateComponent resolved() {
        if (resolved == null) {
            resolved = compose();
        }
        return resolved;
    }

    @Override
    public List<SlateComponent> children() {
        return List.of(resolved());
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        Size measured = resolved().measure(context, available);
        setMeasuredSize(measured);
        return measured;
    }

    @Override
    public void layout(SlateLayoutContext context, Rect bounds) {
        setBounds(bounds);
        resolved().layout(context, bounds);
    }

    @Override
    public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
        resolved().collectDrawCommands(context, commands);
    }

    @Override
    public boolean mouseClicked(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        return resolved().mouseClicked(context, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        return resolved().mouseReleased(context, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseMoved(SlateInteractionContext context, double mouseX, double mouseY) {
        return resolved().mouseMoved(context, mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(SlateInteractionContext context, double mouseX, double mouseY, double delta) {
        return resolved().mouseScrolled(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(SlateInteractionContext context, int keyCode, int scanCode, int modifiers) {
        return resolved().keyPressed(context, keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(SlateInteractionContext context, char codePoint, int modifiers) {
        return resolved().charTyped(context, codePoint, modifiers);
    }
}
