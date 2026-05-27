package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.HorizontalAlign;
import top.huliawsl.slateui.api.SlateBorder;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.VerticalAlign;
import top.huliawsl.slateui.layout.Insets;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.debug.SlateRuntimeException;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public class Button extends SlateComponent {

    private static final SlateStyle DEFAULT_STYLE = SlateStyle.builder()
        .padding(Insets.symmetric(10, 6))
        .backgroundToken("color.primary")
        .hoverBackgroundToken("color.primaryHover")
        .activeBackgroundToken("color.primaryActive")
        .border(new SlateBorder(0xFFBFDBFE, 1))
        .focusBorder(new SlateBorder(0xFFFFFFFF, 1))
        .borderRadiusToken("radius.sm")
        .horizontalAlign(HorizontalAlign.CENTER)
        .verticalAlign(VerticalAlign.CENTER)
        .clipContent(true)
        .build();

    private final String commandId;
    private final List<SlateComponent> children;

    public Button(String label, String commandId, SlateStyle style) {
        this(List.of(new Text(label)), commandId, style);
    }

    public Button(List<SlateComponent> children, String commandId, SlateStyle style) {
        super(SlateStyle.withDefaults(DEFAULT_STYLE, style));
        this.children = List.copyOf(children);
        this.commandId = commandId;
    }

    public String commandId() {
        return commandId;
    }

    @Override
    public boolean focusable() {
        return true;
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
            Size childSize = measureChild(context, child, contentAvailable);
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
            layoutChild(context, child, alignChild(contentRect, child.layoutNode().measuredSize()));
        }
    }

    @Override
    public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
        emitBoxChrome(context, commands);
        Rect contentRect = contentRect(bounds());
        pushClip(context, commands, contentRect);
        for (SlateComponent child : children) {
            collectChild(context, commands, child);
        }
        popClip(commands);
    }

    @Override
    public boolean mouseReleased(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        boolean handled = super.mouseReleased(context, mouseX, mouseY, button);
        if (handled && bounds().contains(mouseX, mouseY)) {
            try {
                boolean executed = context.commands().execute(commandId, context);
                context.commandLogger().accept((executed ? "EXEC " : "MISS ") + commandId + " component=" + debugPath());
                if (!executed) {
                    context.logDiagnostic("COMMAND missing id=" + commandId + " component=" + debugPath());
                }
                return executed;
            } catch (Throwable throwable) {
                throw SlateRuntimeException.command(this, commandId, throwable);
            }
        }
        return handled;
    }
}
