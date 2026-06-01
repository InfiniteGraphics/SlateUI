package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.SlateBorder;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.layout.Insets;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.DrawRectCommand;
import top.huliawsl.slateui.render.PopClipCommand;
import top.huliawsl.slateui.render.PushClipCommand;
import top.huliawsl.slateui.runtime.InvalidationType;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public final class VirtualList extends SlateComponent {

    private static final int DEFAULT_ITEM_HEIGHT = 24;
    private static final SlateStyle DEFAULT_STYLE = SlateStyle.builder()
        .padding(Insets.all(6))
        .backgroundColor(0xFF020617)
        .border(new SlateBorder(0xFF334155, 1))
        .borderRadiusToken("radius.md")
        .clipContent(true)
        .build();

    private final List<SlateComponent> items;
    private final int itemHeight;
    private final int gap;
    private int scrollOffset;
    private int contentHeight;
    private int firstVisible;
    private int lastVisibleExclusive;

    public VirtualList(List<SlateComponent> visibleChildren, SlateStyle style) {
        this(visibleChildren, DEFAULT_ITEM_HEIGHT, 6, style);
    }

    public VirtualList(List<SlateComponent> items, int itemHeight, int gap, SlateStyle style) {
        super(SlateStyle.withDefaults(DEFAULT_STYLE, style));
        this.items = List.copyOf(items == null ? List.of() : items);
        this.itemHeight = Math.max(1, itemHeight);
        this.gap = Math.max(0, gap);
    }

    @Override
    public List<SlateComponent> children() {
        return items;
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        int contentWidth = Math.max(0, available.width() - style().padding().horizontal());
        int viewportHeight = style().height() != null
            ? Math.max(0, style().height() - style().padding().vertical())
            : Math.max(0, available.height() - style().padding().vertical());
        contentHeight = items.isEmpty() ? 0 : items.size() * itemHeight + (items.size() - 1) * gap;
        updateVisibleRange(viewportHeight);
        for (int index = firstVisible; index < lastVisibleExclusive; index++) {
            measureChild(context, items.get(index), new Size(contentWidth, itemHeight));
        }
        int width = style().width() != null ? style().width() : available.width();
        int height = style().height() != null ? style().height() : Math.min(contentHeight + style().padding().vertical(), available.height());
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
        updateVisibleRange(viewport.height());
        for (int index = 0; index < items.size(); index++) {
            SlateComponent item = items.get(index);
            if (index < firstVisible || index >= lastVisibleExclusive) {
                layoutChild(context, item, Rect.ZERO);
                continue;
            }
            int y = viewport.y() - scrollOffset + index * (itemHeight + gap);
            layoutChild(context, item, new Rect(viewport.x(), y, viewport.width(), itemHeight));
        }
    }

    @Override
    public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
        emitBoxChrome(context, commands);
        Rect viewport = contentRect(bounds());
        commands.add(new PushClipCommand(viewport, contentClipRadius(context.theme())));
        for (int index = firstVisible; index < lastVisibleExclusive; index++) {
            collectChild(context, commands, items.get(index));
        }
        commands.add(new PopClipCommand());
        if (contentHeight > viewport.height() && viewport.height() > 0) {
            int thumbHeight = Math.max(12, viewport.height() * viewport.height() / Math.max(viewport.height(), contentHeight));
            int maxOffset = Math.max(1, contentHeight - viewport.height());
            int thumbY = viewport.y() + (viewport.height() - thumbHeight) * scrollOffset / maxOffset;
            commands.add(new DrawRectCommand(new Rect(viewport.right() - 3, thumbY, 2, thumbHeight), 0x99CBD5E1, 1));
        }
    }

    @Override
    public boolean mouseScrolled(SlateInteractionContext context, double mouseX, double mouseY, double delta) {
        Rect viewport = contentRect(bounds());
        if (style().disabled() || !viewport.contains(mouseX, mouseY)) {
            return false;
        }
        int maxOffset = Math.max(0, contentHeight - viewport.height());
        int step = itemHeight + gap;
        int nextOffset = Math.max(0, Math.min(scrollOffset - (int) Math.signum(delta) * step, maxOffset));
        if (nextOffset != scrollOffset) {
            scrollOffset = nextOffset;
            context.requestInvalidation(InvalidationType.LAYOUT, "virtual-list-scroll");
        }
        return true;
    }

    private void updateVisibleRange(int viewportHeight) {
        if (items.isEmpty() || viewportHeight <= 0) {
            firstVisible = 0;
            lastVisibleExclusive = 0;
            return;
        }
        int stride = itemHeight + gap;
        firstVisible = Math.max(0, Math.min(items.size() - 1, scrollOffset / stride));
        int visibleCount = Math.max(1, (viewportHeight / stride) + 2);
        lastVisibleExclusive = Math.min(items.size(), firstVisible + visibleCount);
    }
}
