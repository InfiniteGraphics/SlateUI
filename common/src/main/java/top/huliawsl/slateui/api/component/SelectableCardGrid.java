package top.huliawsl.slateui.api.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import top.huliawsl.slateui.api.SlateBorder;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.layout.Insets;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawBorderCommand;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.DrawRectCommand;
import top.huliawsl.slateui.runtime.InvalidationType;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public final class SelectableCardGrid<T> extends SlateComponent {

    private static final SlateStyle DEFAULT_STYLE = SlateStyle.builder()
        .padding(Insets.all(4))
        .gap(6)
        .clipContent(true)
        .build();

    private final List<T> items;
    private final Function<T, String> keyResolver;
    private final Supplier<String> selectedKeySupplier;
    private final SelectableItemRenderer<T> itemRenderer;
    private final SelectionHandler<T> selectionHandler;
    private final SlateStyle cardStyle;
    private final SlateStyle selectedCardStyle;
    private final int minimumCardWidth;
    private final int fixedColumns;
    private final List<CardCell<T>> children;
    private int columns = 1;
    private int cellWidth;
    private int cellHeight;

    public SelectableCardGrid(
        List<T> items,
        Function<T, String> keyResolver,
        Supplier<String> selectedKeySupplier,
        SelectableItemRenderer<T> itemRenderer,
        SelectionHandler<T> selectionHandler,
        int minimumCardWidth,
        int fixedColumns,
        SlateStyle style,
        SlateStyle cardStyle,
        SlateStyle selectedCardStyle
    ) {
        super(SlateStyle.withDefaults(DEFAULT_STYLE, style));
        this.items = List.copyOf(items == null ? List.of() : items);
        this.keyResolver = keyResolver == null ? item -> String.valueOf(this.items.indexOf(item)) : keyResolver;
        this.selectedKeySupplier = selectedKeySupplier == null ? () -> null : selectedKeySupplier;
        this.itemRenderer = Objects.requireNonNull(itemRenderer, "itemRenderer");
        this.selectionHandler = selectionHandler;
        this.minimumCardWidth = Math.max(1, minimumCardWidth);
        this.fixedColumns = Math.max(0, fixedColumns);
        this.cardStyle = cardStyle == null ? SlateStyle.EMPTY : cardStyle;
        this.selectedCardStyle = selectedCardStyle == null ? SlateStyle.builder().border(new SlateBorder(0xFF60A5FA, 1)).backgroundColor(0x331D4ED8).build() : selectedCardStyle;
        this.children = buildChildren();
    }

    public static <T> Builder<T> builder(List<T> items) {
        return new Builder<>(items);
    }

    @Override
    public List<SlateComponent> children() {
        return new ArrayList<SlateComponent>(children);
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        syncSelectionState();
        int gap = resolveGap(context.theme());
        Size contentAvailable = contentAvailable(available);
        columns = resolveColumns(contentAvailable.width(), gap);
        cellWidth = Math.max(1, columns == 0 ? contentAvailable.width() : (contentAvailable.width() - Math.max(0, columns - 1) * gap) / columns);
        cellHeight = 0;
        for (CardCell<T> child : children) {
            Size childSize = measureChild(context, child, new Size(cellWidth, contentAvailable.height()));
            cellHeight = Math.max(cellHeight, childSize.height());
        }
        int rows = children.isEmpty() ? 0 : (int) Math.ceil(children.size() / (double) columns);
        int width = contentAvailable.width();
        int height = rows * cellHeight + Math.max(0, rows - 1) * gap;
        Size measured = applyStyleSize(addInsets(new Size(width, height), style().padding()));
        setMeasuredSize(measured);
        return measured;
    }

    @Override
    public void layout(SlateLayoutContext context, Rect bounds) {
        setBounds(bounds);
        Rect content = contentRect(bounds);
        int gap = resolveGap(context.theme());
        for (int index = 0; index < children.size(); index++) {
            int row = index / columns;
            int column = index % columns;
            layoutChild(context, children.get(index), new Rect(content.x() + column * (cellWidth + gap), content.y() + row * (cellHeight + gap), cellWidth, cellHeight));
        }
    }

    @Override
    public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
        emitBoxChrome(context, commands);
        pushClip(context, commands, contentRect(bounds()));
        for (SlateComponent child : children) {
            collectChild(context, commands, child);
        }
        popClip(commands);
    }

    @Override
    public boolean mouseClicked(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        if (style().disabled() || !contentRect(bounds()).contains(mouseX, mouseY)) {
            return false;
        }
        for (int index = children.size() - 1; index >= 0; index--) {
            if (children.get(index).mouseClicked(context, mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        for (int index = children.size() - 1; index >= 0; index--) {
            if (children.get(index).mouseReleased(context, mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseMoved(SlateInteractionContext context, double mouseX, double mouseY) {
        boolean handled = false;
        for (int index = children.size() - 1; index >= 0; index--) {
            handled |= children.get(index).mouseMoved(context, mouseX, mouseY);
        }
        return handled;
    }

    private int resolveColumns(int contentWidth, int gap) {
        if (fixedColumns > 0) {
            return fixedColumns;
        }
        return Math.max(1, (contentWidth + gap) / Math.max(1, minimumCardWidth + gap));
    }

    private List<CardCell<T>> buildChildren() {
        List<CardCell<T>> built = new ArrayList<>();
        for (int index = 0; index < items.size(); index++) {
            T item = items.get(index);
            String key = safeKey(item, index);
            boolean selected = Objects.equals(key, selectedKeySupplier.get());
            SlateComponent content = itemRenderer.render(item, new SelectableItemState(key, index, selected, false));
            built.add(new CardCell<>(item, key, index, content, selectionHandler, cardStyle, selectedCardStyle, selected));
        }
        return built;
    }

    private void syncSelectionState() {
        String selectedKey = selectedKeySupplier.get();
        for (CardCell<T> child : children) {
            child.setSelected(Objects.equals(child.key(), selectedKey));
        }
    }

    private String safeKey(T item, int index) {
        String key = keyResolver.apply(item);
        return key == null || key.isBlank() ? String.valueOf(index) : key;
    }

    public static final class Builder<T> {

        private final List<T> items;
        private Function<T, String> keyResolver;
        private Supplier<String> selectedKeySupplier;
        private SelectableItemRenderer<T> itemRenderer;
        private SelectionHandler<T> selectionHandler;
        private int minimumCardWidth = 120;
        private int fixedColumns;
        private SlateStyle style;
        private SlateStyle cardStyle;
        private SlateStyle selectedCardStyle;

        private Builder(List<T> items) {
            this.items = items;
        }

        public Builder<T> key(Function<T, String> keyResolver) {
            this.keyResolver = keyResolver;
            return this;
        }

        public Builder<T> selectedKey(Supplier<String> selectedKeySupplier) {
            this.selectedKeySupplier = selectedKeySupplier;
            return this;
        }

        public Builder<T> renderer(SelectableItemRenderer<T> itemRenderer) {
            this.itemRenderer = itemRenderer;
            return this;
        }

        public Builder<T> onSelect(SelectionHandler<T> selectionHandler) {
            this.selectionHandler = selectionHandler;
            return this;
        }

        public Builder<T> minimumCardWidth(int minimumCardWidth) {
            this.minimumCardWidth = minimumCardWidth;
            return this;
        }

        public Builder<T> fixedColumns(int fixedColumns) {
            this.fixedColumns = fixedColumns;
            return this;
        }

        public Builder<T> style(SlateStyle style) {
            this.style = style;
            return this;
        }

        public Builder<T> cardStyle(SlateStyle cardStyle) {
            this.cardStyle = cardStyle;
            return this;
        }

        public Builder<T> selectedCardStyle(SlateStyle selectedCardStyle) {
            this.selectedCardStyle = selectedCardStyle;
            return this;
        }

        public SelectableCardGrid<T> build() {
            return new SelectableCardGrid<>(items, keyResolver, selectedKeySupplier, itemRenderer, selectionHandler, minimumCardWidth, fixedColumns, style, cardStyle, selectedCardStyle);
        }
    }

    private static final class CardCell<T> extends SlateComponent {

        private final T item;
        private final String key;
        private final int index;
        private final SlateComponent content;
        private final SelectionHandler<T> selectionHandler;
        private final SlateStyle selectedStyle;
        private boolean selected;

        private CardCell(T item, String key, int index, SlateComponent content, SelectionHandler<T> selectionHandler, SlateStyle style, SlateStyle selectedStyle, boolean selected) {
            super(style == null ? SlateStyle.EMPTY : style);
            this.item = item;
            this.key = key;
            this.index = index;
            this.content = content == null ? new Text("") : content;
            this.selectionHandler = selectionHandler;
            this.selectedStyle = selectedStyle == null ? SlateStyle.EMPTY : selectedStyle;
            this.selected = selected;
        }

        String key() {
            return key;
        }

        void setSelected(boolean selected) {
            this.selected = selected;
        }

        @Override
        public List<SlateComponent> children() {
            return List.of(content);
        }

        @Override
        public Size measure(SlateLayoutContext context, Size available) {
            Size childSize = measureChild(context, content, contentAvailable(available));
            Size measured = applyStyleSize(addInsets(childSize, style().padding()));
            setMeasuredSize(measured);
            return measured;
        }

        @Override
        public void layout(SlateLayoutContext context, Rect bounds) {
            setBounds(bounds);
            layoutChild(context, content, contentRect(bounds));
        }

        @Override
        public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
            emitBoxChrome(context, commands);
            if (selected) {
                Integer background = selectedStyle.backgroundColor();
                if (background != null) {
                    commands.add(new DrawRectCommand(bounds(), background, context.theme().resolveRadius(selectedStyle.borderRadius(), selectedStyle.borderRadiusToken(), 0)));
                }
                SlateBorder border = selectedStyle.border();
                if (border.thickness() > 0) {
                    commands.add(new DrawBorderCommand(bounds(), border.color(), border.thickness(), context.theme().resolveRadius(selectedStyle.borderRadius(), selectedStyle.borderRadiusToken(), 0)));
                }
            }
            collectChild(context, commands, content);
        }

        @Override
        public boolean mouseClicked(SlateInteractionContext context, double mouseX, double mouseY, int button) {
            if (style().disabled() || !bounds().contains(mouseX, mouseY)) {
                return false;
            }
            setPressed(true);
            context.requestInvalidation(InvalidationType.INTERACTION, "card-press");
            return true;
        }

        @Override
        public boolean mouseReleased(SlateInteractionContext context, double mouseX, double mouseY, int button) {
            boolean wasPressed = isPressed();
            setPressed(false);
            if (wasPressed && bounds().contains(mouseX, mouseY)) {
                if (selectionHandler != null) {
                    selectionHandler.onSelect(context, item, key, index);
                }
                context.requestInvalidation(InvalidationType.LAYOUT, "card-select");
                return true;
            }
            return wasPressed;
        }
    }
}
