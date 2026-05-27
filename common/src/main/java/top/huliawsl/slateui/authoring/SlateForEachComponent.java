package top.huliawsl.slateui.authoring;

import com.google.gson.JsonObject;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import top.huliawsl.slateui.api.ScopedStateProvider;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.binding.BindingEvaluator;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

final class SlateForEachComponent extends SlateComponent {

    private final SlateIrRuntimeFactory runtimeFactory;
    private final JsonObject templateNode;
    private final SlateIrRuntimeFactory.RuntimeBuildContext baseContext;
    private final String alias;
    private final String sourceExpression;
    private final String keyExpression;
    private final Map<String, Entry> keyedEntries = new LinkedHashMap<>();
    private List<Entry> resolvedEntries = List.of();

    SlateForEachComponent(
        SlateIrRuntimeFactory runtimeFactory,
        JsonObject templateNode,
        SlateIrRuntimeFactory.RuntimeBuildContext baseContext,
        String alias,
        String sourceExpression,
        String keyExpression
    ) {
        super(SlateStyle.EMPTY);
        this.runtimeFactory = runtimeFactory;
        this.templateNode = templateNode;
        this.baseContext = baseContext;
        this.alias = alias == null || alias.isBlank() ? "item" : alias;
        this.sourceExpression = sourceExpression;
        this.keyExpression = keyExpression;
    }

    @Override
    public List<SlateComponent> children() {
        refreshChildren();
        return resolvedEntries.stream().map(Entry::component).toList();
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        refreshChildren();
        int width = 0;
        int height = 0;
        for (Entry entry : resolvedEntries) {
            Size childSize = entry.component.measure(context, available);
            width = Math.max(width, childSize.width());
            height += childSize.height();
        }
        Size measured = new Size(width, height);
        setMeasuredSize(measured);
        return measured;
    }

    @Override
    public void layout(SlateLayoutContext context, Rect bounds) {
        setBounds(bounds);
        int cursorY = bounds.y();
        for (Entry entry : resolvedEntries) {
            Size childSize = entry.component.layoutNode().measuredSize();
            entry.component.layout(context, new Rect(bounds.x(), cursorY, Math.min(childSize.width(), bounds.width()), childSize.height()));
            cursorY += childSize.height();
        }
    }

    @Override
    public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
        for (Entry entry : resolvedEntries) {
            entry.component.collectDrawCommands(context, commands);
        }
    }

    @Override
    public boolean mouseClicked(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        for (int index = resolvedEntries.size() - 1; index >= 0; index--) {
            if (resolvedEntries.get(index).component.mouseClicked(context, mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        for (int index = resolvedEntries.size() - 1; index >= 0; index--) {
            if (resolvedEntries.get(index).component.mouseReleased(context, mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseMoved(SlateInteractionContext context, double mouseX, double mouseY) {
        boolean handled = false;
        for (Entry entry : resolvedEntries) {
            handled |= entry.component.mouseMoved(context, mouseX, mouseY);
        }
        return handled;
    }

    @Override
    public boolean mouseScrolled(SlateInteractionContext context, double mouseX, double mouseY, double delta) {
        for (int index = resolvedEntries.size() - 1; index >= 0; index--) {
            if (resolvedEntries.get(index).component.mouseScrolled(context, mouseX, mouseY, delta)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(SlateInteractionContext context, int keyCode, int scanCode, int modifiers) {
        for (int index = resolvedEntries.size() - 1; index >= 0; index--) {
            if (resolvedEntries.get(index).component.keyPressed(context, keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(SlateInteractionContext context, char codePoint, int modifiers) {
        for (int index = resolvedEntries.size() - 1; index >= 0; index--) {
            if (resolvedEntries.get(index).component.charTyped(context, codePoint, modifiers)) {
                return true;
            }
        }
        return false;
    }

    private void refreshChildren() {
        List<?> items = toList(BindingEvaluator.evaluate(sourceExpression, baseContext.provider()));
        Map<String, Entry> nextCache = new LinkedHashMap<>();
        List<Entry> nextEntries = new ArrayList<>();
        for (int index = 0; index < items.size(); index++) {
            Object item = items.get(index);
            String cacheKey = resolveKey(item, index);
            Entry entry = keyedEntries.get(cacheKey);
            if (entry == null) {
                ScopedStateProvider scopedProvider = new ScopedStateProvider(baseContext.provider())
                    .setLocal(alias, item)
                    .setLocal(alias + "Index", index);
                SlateComponent component = runtimeFactory.buildComponentNode(templateNode.deepCopy(), baseContext.withProvider(scopedProvider));
                entry = new Entry(scopedProvider, component);
            } else {
                entry.provider.setLocal(alias, item).setLocal(alias + "Index", index);
            }
            nextCache.put(cacheKey, entry);
            nextEntries.add(entry);
        }
        keyedEntries.clear();
        keyedEntries.putAll(nextCache);
        resolvedEntries = List.copyOf(nextEntries);
    }

    private String resolveKey(Object item, int index) {
        if (keyExpression == null || keyExpression.isBlank()) {
            return String.valueOf(index);
        }
        ScopedStateProvider scopedProvider = new ScopedStateProvider(baseContext.provider())
            .setLocal(alias, item)
            .setLocal(alias + "Index", index);
        Object key = BindingEvaluator.evaluate(keyExpression, scopedProvider);
        return String.valueOf(key == null ? index : key);
    }

    private static List<?> toList(Object value) {
        if (value == null) {
            return List.of();
        }
        if (value instanceof List<?> list) {
            return list;
        }
        if (value instanceof Iterable<?> iterable) {
            List<Object> values = new ArrayList<>();
            for (Object element : iterable) {
                values.add(element);
            }
            return values;
        }
        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            List<Object> values = new ArrayList<>(length);
            for (int index = 0; index < length; index++) {
                values.add(Array.get(value, index));
            }
            return values;
        }
        return List.of(value);
    }

    private record Entry(ScopedStateProvider provider, SlateComponent component) {
    }
}
