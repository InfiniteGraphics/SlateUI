package top.huliawsl.slateui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;
import top.huliawsl.slateui.api.ComputedStateProvider;
import top.huliawsl.slateui.api.SlateCompositeComponent;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateScreen;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StateProvider;
import top.huliawsl.slateui.api.Theme;
import top.huliawsl.slateui.api.component.Conditional;
import top.huliawsl.slateui.api.component.Modal;
import top.huliawsl.slateui.api.component.ScrollView;
import top.huliawsl.slateui.api.component.Stack;
import top.huliawsl.slateui.authoring.SlateComponentRegistry;
import top.huliawsl.slateui.authoring.SlateIrRuntimeFactory;
import top.huliawsl.slateui.binding.BindingEvaluator;
import top.huliawsl.slateui.command.CommandContext;
import top.huliawsl.slateui.command.SlateCommandRegistry;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

class Mvp3RuntimeTest {

    @Test
    void conditionalComponentDropsOutOfLayoutWhenFalse() {
        var visible = new boolean[] {false};
        Conditional conditional = new Conditional(() -> visible[0], new FixedComponent("visible", 32, 12));

        assertEquals(Size.ZERO, conditional.measure(new SlateLayoutContext(null), new Size(100, 100)));

        visible[0] = true;
        assertEquals(new Size(32, 12), conditional.measure(new SlateLayoutContext(null), new Size(100, 100)));
    }

    @Test
    void computedProviderInvalidatesDependents() {
        ComputedStateProvider provider = new ComputedStateProvider()
            .set("player.first", "Alex")
            .set("player.last", "Stone")
            .registerComputed("player.full", List.of("player.first", "player.last"), state ->
                state.get("player.first") + " " + state.get("player.last"));

        List<String> dirtyPaths = new ArrayList<>();
        provider.addListener(dirtyPaths::add);
        provider.set("player.first", "Sam");

        assertEquals("Sam Stone", provider.get("player.full"));
        assertTrue(dirtyPaths.contains("player.full"));
    }

    @Test
    void keyedForEachReusesComponentInstancesAcrossReorder() {
        SlateComponentRegistry registry = new SlateComponentRegistry()
            .register("ItemCell", (node, children, namedSlots, context) -> new MarkerComponent(context.provider()));
        SlateIrRuntimeFactory factory = new SlateIrRuntimeFactory(registry);
        JsonObject root = component("ItemCell");
        JsonObject directives = new JsonObject();
        directives.addProperty("alias", "item");
        directives.addProperty("for", "items");
        directives.addProperty("key", "item.id");
        root.add("directives", directives);
        root.add("children", new JsonArray());

        top.huliawsl.slateui.api.MutableStateProvider provider = new top.huliawsl.slateui.api.MutableStateProvider()
            .set("items", List.of(Map.of("id", "a"), Map.of("id", "b")));
        SlateComponent list = factory.buildComponentTree(root, provider, Theme.DEFAULT);

        List<SlateComponent> first = list.children();
        provider.set("items", List.of(Map.of("id", "b"), Map.of("id", "a")));
        List<SlateComponent> reordered = list.children();

        assertSame(first.get(0), reordered.get(1));
        assertSame(first.get(1), reordered.get(0));
        assertEquals("b", ((MarkerComponent) reordered.get(0)).currentId());
        assertEquals("a", ((MarkerComponent) reordered.get(1)).currentId());
    }

    @Test
    void forEachLaysOutItemsVerticallyWithoutOverlap() {
        SlateComponentRegistry registry = new SlateComponentRegistry()
            .register("ItemCell", (node, children, namedSlots, context) -> new MarkerComponent(context.provider()));
        SlateIrRuntimeFactory factory = new SlateIrRuntimeFactory(registry);
        JsonObject root = component("ItemCell");
        JsonObject directives = new JsonObject();
        directives.addProperty("alias", "item");
        directives.addProperty("for", "items");
        directives.addProperty("key", "item.id");
        root.add("directives", directives);
        root.add("children", new JsonArray());

        top.huliawsl.slateui.api.MutableStateProvider provider = new top.huliawsl.slateui.api.MutableStateProvider()
            .set("items", List.of(Map.of("id", "a"), Map.of("id", "b")));
        SlateComponent list = factory.buildComponentTree(root, provider, Theme.DEFAULT);

        list.measure(new SlateLayoutContext(null), new Size(100, 100));
        list.layout(new SlateLayoutContext(null), new Rect(0, 0, 100, 100));

        List<SlateComponent> children = list.children();
        assertEquals(0, children.get(0).bounds().y());
        assertEquals(8, children.get(1).bounds().y());
    }

    @Test
    void namedSlotsProjectIntoCustomComponentAndModalConsumesBackdropClicks() {
        SlateComponentRegistry registry = new SlateComponentRegistry()
            .register("Leaf", (node, children, namedSlots, context) -> new FixedComponent(
                node.getAsJsonObject("props").get("id").getAsString(),
                16,
                8
            ))
            .register("Panel", (node, children, namedSlots, context) -> new SlotPanel(children, namedSlots));
        SlateIrRuntimeFactory factory = new SlateIrRuntimeFactory(registry);

        JsonObject panel = component("Panel");
        panel.add("children", arrayOf(componentWithId("Leaf", "body")));
        JsonObject slots = new JsonObject();
        slots.add("header", arrayOf(componentWithId("Leaf", "header")));
        slots.add("footer", arrayOf(componentWithId("Leaf", "footer")));
        panel.add("slots", slots);

        SlateComponent built = factory.buildComponentTree(panel, StateProvider.EMPTY, Theme.DEFAULT);
        built.measure(new SlateLayoutContext(null), new Size(100, 100));
        built.layout(new SlateLayoutContext(null), new Rect(0, 0, 100, 100));
        String tree = built.dumpComponentTree();
        assertTrue(tree.contains("header"));
        assertTrue(tree.contains("body"));
        assertTrue(tree.contains("footer"));

        FixedClickableComponent content = new FixedClickableComponent("content", 100, 100);
        FixedClickableComponent modalContent = new FixedClickableComponent("modal", 20, 20);
        Modal modal = new Modal(content, modalContent, () -> true, SlateStyle.EMPTY);
        modal.measure(new SlateLayoutContext(null), new Size(100, 100));
        modal.layout(new SlateLayoutContext(null), new Rect(0, 0, 100, 100));
        SlateScreen screen = new SlateScreen(Component.literal("Test"), modal, new SlateCommandRegistry(), StateProvider.EMPTY, Theme.DEFAULT, false);
        SlateInteractionContext interaction = new SlateInteractionContext(
            new SlateCommandRegistry(),
            new CommandContext(null, screen),
            ignored -> {},
            ignored -> {},
            screen,
            StateProvider.EMPTY,
            Theme.DEFAULT
        );

        assertTrue(modal.mouseClicked(interaction, 5, 5, 0));
        assertEquals(0, content.clicks);
        assertEquals(0, modalContent.clicks);
        assertTrue(modal.mouseClicked(interaction, 50, 50, 0));
        assertEquals(1, modalContent.clicks);
    }

    @Test
    void openModalConsumesScrollOverBackdrop() {
        ScrollView scrollView = new ScrollView(new Stack(top.huliawsl.slateui.api.StackDirection.COLUMN, List.of(
            new FixedComponent("a", 40, 60),
            new FixedComponent("b", 40, 60)
        ), SlateStyle.EMPTY), SlateStyle.builder().width(80).height(40).build());
        Modal modal = new Modal(scrollView, new FixedComponent("modal", 20, 20), () -> true, SlateStyle.EMPTY);
        modal.measure(new SlateLayoutContext(null), new Size(100, 100));
        modal.layout(new SlateLayoutContext(null), new Rect(0, 0, 100, 100));
        SlateScreen screen = new SlateScreen(Component.literal("Test"), modal, new SlateCommandRegistry(), StateProvider.EMPTY, Theme.DEFAULT, false);
        List<String> diagnostics = new ArrayList<>();
        SlateInteractionContext interaction = new SlateInteractionContext(
            new SlateCommandRegistry(),
            new CommandContext(null, screen),
            ignored -> {},
            diagnostics::add,
            screen,
            StateProvider.EMPTY,
            Theme.DEFAULT
        );

        assertTrue(modal.mouseScrolled(interaction, 5, 5, -1));
        assertTrue(diagnostics.stream().noneMatch(entry -> entry.startsWith("SCROLL")));
    }

    private static JsonObject component(String type) {
        JsonObject node = new JsonObject();
        node.addProperty("componentType", type);
        node.add("props", new JsonObject());
        node.add("bindings", new JsonObject());
        node.add("children", new JsonArray());
        return node;
    }

    private static JsonObject componentWithId(String type, String id) {
        JsonObject node = component(type);
        node.getAsJsonObject("props").addProperty("id", id);
        return node;
    }

    private static JsonArray arrayOf(JsonObject... nodes) {
        JsonArray array = new JsonArray();
        for (JsonObject node : nodes) {
            array.add(node);
        }
        return array;
    }

    private static class FixedComponent extends SlateComponent {

        private final String name;
        private final Size size;

        private FixedComponent(String name, int width, int height) {
            super(SlateStyle.EMPTY);
            this.name = name;
            this.size = new Size(width, height);
        }

        @Override
        public String debugName() {
            return name;
        }

        @Override
        public Size measure(SlateLayoutContext context, Size available) {
            setMeasuredSize(size);
            return size;
        }

        @Override
        public void layout(SlateLayoutContext context, Rect bounds) {
            setBounds(new Rect(bounds.x(), bounds.y(), size.width(), size.height()));
        }

        @Override
        public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
        }
    }

    private static final class FixedClickableComponent extends FixedComponent {

        private int clicks;

        private FixedClickableComponent(String name, int width, int height) {
            super(name, width, height);
        }

        @Override
        public boolean mouseClicked(SlateInteractionContext context, double mouseX, double mouseY, int button) {
            if (!bounds().contains(mouseX, mouseY)) {
                return false;
            }
            clicks++;
            return true;
        }
    }

    private static final class MarkerComponent extends FixedComponent {

        private final StateProvider provider;

        private MarkerComponent(StateProvider provider) {
            super("marker", 12, 8);
            this.provider = provider;
        }

        private String currentId() {
            return String.valueOf(BindingEvaluator.evaluate("{item.id}", provider));
        }
    }

    private static final class SlotPanel extends SlateCompositeComponent {

        private SlotPanel(List<SlateComponent> children, Map<String, List<SlateComponent>> namedSlots) {
            super(children, namedSlots, SlateStyle.EMPTY);
        }

        @Override
        protected SlateComponent compose() {
            List<SlateComponent> ordered = new ArrayList<>();
            ordered.addAll(slotChildren("header"));
            ordered.addAll(slotChildren());
            ordered.addAll(slotChildren("footer"));
            return new top.huliawsl.slateui.api.component.Stack(top.huliawsl.slateui.api.StackDirection.COLUMN, ordered, SlateStyle.builder().gap(2).build());
        }
    }
}
