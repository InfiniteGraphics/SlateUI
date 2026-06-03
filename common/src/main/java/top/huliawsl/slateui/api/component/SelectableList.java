package top.huliawsl.slateui.api.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import top.huliawsl.slateui.api.SlateBorder;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;
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

public final class SelectableList<T> extends SlateComponent {

    private static final SlateStyle DEFAULT_STYLE = SlateStyle.builder()
        .padding(Insets.all(4))
        .backgroundColor(0xFF020617)
        .border(new SlateBorder(0xFF334155, 1))
        .borderRadiusToken("radius.md")
        .gap(4)
        .clipContent(true)
        .build();

    private final List<T> items;
    private final Function<T, String> keyResolver;
    private final Supplier<String> selectedKeySupplier;
    private final SelectableItemRenderer<T> itemRenderer;
    private final SelectionHandler<T> selectionHandler;
    private final SlateStyle itemStyle;
    private final SlateStyle selectedItemStyle;
    private final List<SelectableItem<T>> children;
    private final Stack stack;

    public SelectableList(
        List<T> items,
        Function<T, String> keyResolver,
        Supplier<String> selectedKeySupplier,
        SelectableItemRenderer<T> itemRenderer,
        SelectionHandler<T> selectionHandler,
        SlateStyle style,
        SlateStyle itemStyle,
        SlateStyle selectedItemStyle
    ) {
        super(SlateStyle.withDefaults(DEFAULT_STYLE, style));
        this.items = List.copyOf(items == null ? List.of() : items);
        this.keyResolver = keyResolver == null ? item -> String.valueOf(this.items.indexOf(item)) : keyResolver;
        this.selectedKeySupplier = selectedKeySupplier == null ? () -> null : selectedKeySupplier;
        this.itemRenderer = Objects.requireNonNull(itemRenderer, "itemRenderer");
        this.selectionHandler = selectionHandler;
        this.itemStyle = itemStyle == null ? SlateStyle.EMPTY : itemStyle;
        this.selectedItemStyle = selectedItemStyle == null ? SlateStyle.builder().border(new SlateBorder(0xFF60A5FA, 1)).backgroundColor(0x331D4ED8).build() : selectedItemStyle;
        this.children = buildChildren();
        this.stack = new Stack(StackDirection.COLUMN, new ArrayList<SlateComponent>(children), SlateStyle.builder().gap(resolveInitialGap()).build());
    }

    public static <T> Builder<T> builder(List<T> items) {
        return new Builder<>(items);
    }

    public List<T> items() {
        return items;
    }

    @Override
    public List<SlateComponent> children() {
        return List.of(stack);
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        syncSelectionState();
        Size measured = stack.measure(context, contentAvailable(available));
        measured = applyStyleSize(addInsets(measured, style().padding()));
        setMeasuredSize(measured);
        return measured;
    }

    @Override
    public void layout(SlateLayoutContext context, Rect bounds) {
        setBounds(bounds);
        layoutChild(context, stack, contentRect(bounds));
    }

    @Override
    public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
        emitBoxChrome(context, commands);
        pushClip(context, commands, contentRect(bounds()));
        collectChild(context, commands, stack);
        popClip(commands);
    }

    @Override
    public boolean mouseClicked(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        if (style().disabled() || !contentRect(bounds()).contains(mouseX, mouseY)) {
            return false;
        }
        return stack.mouseClicked(context, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        if (style().disabled()) {
            return false;
        }
        return stack.mouseReleased(context, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseMoved(SlateInteractionContext context, double mouseX, double mouseY) {
        if (style().disabled()) {
            return false;
        }
        return stack.mouseMoved(context, mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(SlateInteractionContext context, double mouseX, double mouseY, double delta) {
        return stack.mouseScrolled(context, mouseX, mouseY, delta);
    }

    private List<SelectableItem<T>> buildChildren() {
        List<SelectableItem<T>> built = new ArrayList<>();
        for (int index = 0; index < items.size(); index++) {
            T item = items.get(index);
            String key = safeKey(item, index);
            boolean selected = Objects.equals(key, selectedKeySupplier.get());
            SlateComponent content = itemRenderer.render(item, new SelectableItemState(key, index, selected, false));
            built.add(new SelectableItem<>(item, key, index, content, selectionHandler, itemStyle, selectedItemStyle, selected));
        }
        return built;
    }

    private void syncSelectionState() {
        String selectedKey = selectedKeySupplier.get();
        for (SelectableItem<T> child : children) {
            child.setSelected(Objects.equals(child.key(), selectedKey));
        }
    }

    private String safeKey(T item, int index) {
        String key = keyResolver.apply(item);
        return key == null || key.isBlank() ? String.valueOf(index) : key;
    }

    private int resolveInitialGap() {
        return style().directGap() == null ? style().gap() : style().directGap();
    }

    public static final class Builder<T> {

        private final List<T> items;
        private Function<T, String> keyResolver;
        private Supplier<String> selectedKeySupplier;
        private SelectableItemRenderer<T> itemRenderer;
        private SelectionHandler<T> selectionHandler;
        private SlateStyle style;
        private SlateStyle itemStyle;
        private SlateStyle selectedItemStyle;

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

        public Builder<T> style(SlateStyle style) {
            this.style = style;
            return this;
        }

        public Builder<T> itemStyle(SlateStyle itemStyle) {
            this.itemStyle = itemStyle;
            return this;
        }

        public Builder<T> selectedItemStyle(SlateStyle selectedItemStyle) {
            this.selectedItemStyle = selectedItemStyle;
            return this;
        }

        public SelectableList<T> build() {
            return new SelectableList<>(items, keyResolver, selectedKeySupplier, itemRenderer, selectionHandler, style, itemStyle, selectedItemStyle);
        }
    }

    private static final class SelectableItem<T> extends SlateComponent {

        private final T item;
        private final String key;
        private final int index;
        private final SlateComponent content;
        private final SelectionHandler<T> selectionHandler;
        private final SlateStyle selectedStyle;
        private boolean selected;

        private SelectableItem(T item, String key, int index, SlateComponent content, SelectionHandler<T> selectionHandler, SlateStyle style, SlateStyle selectedStyle, boolean selected) {
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
            context.requestInvalidation(InvalidationType.INTERACTION, "selectable-press");
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
                context.requestInvalidation(InvalidationType.LAYOUT, "selectable-select");
                return true;
            }
            return wasPressed;
        }
    }
}
