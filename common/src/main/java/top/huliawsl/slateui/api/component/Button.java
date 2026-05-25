package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public class Button extends SlateComponent {

    private final String commandId;
    private final List<SlateComponent> children;

    public Button(String label, String commandId, SlateStyle style) {
        this(List.of(new Text(label)), commandId, style);
    }

    public Button(List<SlateComponent> children, String commandId, SlateStyle style) {
        super(style);
        this.children = List.copyOf(children);
        this.commandId = commandId;
    }

    public String commandId() {
        return commandId;
    }

    @Override
    public List<SlateComponent> children() {
        return children;
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        Size contentAvailable = contentAvailable(available);
        int maxWidth = 0;
        int maxHeight = 0;
        for (SlateComponent child : children) {
            Size childSize = child.measure(context, contentAvailable);
            maxWidth = Math.max(maxWidth, childSize.width());
            maxHeight = Math.max(maxHeight, childSize.height());
        }
        Size measured = applyStyleSize(addInsets(new Size(maxWidth, maxHeight), style().padding()));
        setMeasuredSize(measured);
        return measured;
    }

    @Override
    public void layout(SlateLayoutContext context, Rect bounds) {
        setBounds(bounds);
        Rect contentRect = contentRect(bounds);
        for (SlateComponent child : children) {
            child.layout(context, alignChild(contentRect, child.layoutNode().measuredSize()));
        }
    }

    @Override
    public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
        emitBoxChrome(context, commands);
        for (SlateComponent child : children) {
            child.collectDrawCommands(context, commands);
        }
    }

    @Override
    public boolean mouseClicked(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        if (bounds().contains(mouseX, mouseY)) {
            boolean handled = context.commands().execute(commandId, context.commandContext());
            context.commandLogger().accept((handled ? "EXEC " : "MISS ") + commandId);
            return handled;
        }
        return super.mouseClicked(context, mouseX, mouseY, button);
    }
}
