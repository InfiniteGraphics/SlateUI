package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.DrawRectCommand;
import top.huliawsl.slateui.render.PopClipCommand;
import top.huliawsl.slateui.render.PushClipCommand;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public class ScrollView extends SlateComponent {

    private final SlateComponent content;
    private final int scrollStep;
    private int scrollOffset;
    private int contentHeight;

    public ScrollView(SlateComponent content, SlateStyle style) {
        this(content, 16, style);
    }

    public ScrollView(SlateComponent content, int scrollStep, SlateStyle style) {
        super(style);
        this.content = content;
        this.scrollStep = Math.max(4, scrollStep);
    }

    @Override
    public List<SlateComponent> children() {
        return List.of(content);
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        Size contentAvailable = contentAvailable(available);
        Size contentSize = measureChild(context, content, new Size(contentAvailable.width(), Math.max(contentAvailable.height(), 10000)));
        contentHeight = contentSize.height();
        int width = style().width() != null ? style().width() : contentSize.width() + style().padding().horizontal();
        int height = style().height() != null ? style().height() : Math.min(contentSize.height() + style().padding().vertical(), available.height());
        Size measured = applyStyleSize(new Size(width, height));
        setMeasuredSize(measured);
        return measured;
    }

    @Override
    public void layout(SlateLayoutContext context, Rect bounds) {
        setBounds(bounds);
        Rect viewport = contentRect(bounds);
        int maxOffset = Math.max(0, contentHeight - viewport.height());
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxOffset));
        layoutChild(context, content, new Rect(viewport.x(), viewport.y() - scrollOffset, viewport.width(), contentHeight));
    }

    @Override
    public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
        emitBoxChrome(context, commands);
        Rect viewport = contentRect(bounds());
        commands.add(new PushClipCommand(viewport, contentClipRadius(context.theme())));
        collectChild(context, commands, content);
        commands.add(new PopClipCommand());
        if (contentHeight > viewport.height() && viewport.height() > 0) {
            int trackHeight = viewport.height();
            int thumbHeight = Math.max(12, trackHeight * trackHeight / Math.max(trackHeight, contentHeight));
            int maxOffset = Math.max(1, contentHeight - trackHeight);
            int thumbY = viewport.y() + (trackHeight - thumbHeight) * scrollOffset / maxOffset;
            commands.add(new DrawRectCommand(new Rect(viewport.right() - 3, thumbY, 2, thumbHeight), 0x99CBD5E1, 1));
        }
    }

    @Override
    public boolean mouseClicked(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        Rect viewport = contentRect(bounds());
        return viewport.contains(mouseX, mouseY) && content.mouseClicked(context, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        Rect viewport = contentRect(bounds());
        return viewport.contains(mouseX, mouseY) && content.mouseReleased(context, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseMoved(SlateInteractionContext context, double mouseX, double mouseY) {
        Rect viewport = contentRect(bounds());
        if (!viewport.contains(mouseX, mouseY)) {
            return false;
        }
        return content.mouseMoved(context, mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(SlateInteractionContext context, double mouseX, double mouseY, double delta) {
        Rect viewport = contentRect(bounds());
        if (!viewport.contains(mouseX, mouseY)) {
            return false;
        }
        if (content.mouseScrolled(context, mouseX, mouseY, delta)) {
            return true;
        }
        int viewportHeight = viewport.height();
        int maxOffset = Math.max(0, contentHeight - viewportHeight);
        int nextOffset = Math.max(0, Math.min(scrollOffset - (int) Math.signum(delta) * scrollStep, maxOffset));
        if (nextOffset != scrollOffset) {
            scrollOffset = nextOffset;
            context.logDiagnostic("SCROLL offset=" + scrollOffset);
            context.screen().requestRebuild("scroll");
        }
        return true;
    }
}
