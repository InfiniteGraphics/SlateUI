package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
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
    }

    @Override
    public boolean mouseScrolled(SlateInteractionContext context, double mouseX, double mouseY, double delta) {
        if (!bounds().contains(mouseX, mouseY)) {
            return false;
        }
        int viewportHeight = contentRect(bounds()).height();
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
