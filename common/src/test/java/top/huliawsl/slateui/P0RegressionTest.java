package top.huliawsl.slateui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;
import top.huliawsl.slateui.api.SlateBorder;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateScreen;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;
import top.huliawsl.slateui.api.StateProvider;
import top.huliawsl.slateui.api.Theme;
import top.huliawsl.slateui.api.component.Box;
import top.huliawsl.slateui.api.component.Button;
import top.huliawsl.slateui.api.component.SlotGrid;
import top.huliawsl.slateui.api.component.Stack;
import top.huliawsl.slateui.api.container.ContainerSlot;
import top.huliawsl.slateui.api.container.StaticContainerSlotProvider;
import top.huliawsl.slateui.authoring.SlateIrRuntimeFactory;
import top.huliawsl.slateui.command.CommandContext;
import top.huliawsl.slateui.command.SlateCommandRegistry;
import top.huliawsl.slateui.layout.Insets;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawBorderCommand;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.DrawRectCommand;
import top.huliawsl.slateui.render.PushClipCommand;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

class P0RegressionTest {

    @Test
    void stackLayoutKeepsGapAndChildOrder() {
        Stack stack = new Stack(StackDirection.COLUMN, List.of(
            new FixedComponent("first", 20, 8),
            new FixedComponent("second", 30, 10)
        ), SlateStyle.builder().gap(3).build());

        stack.measure(new SlateLayoutContext(null), new Size(100, 100));
        stack.layout(new SlateLayoutContext(null), new Rect(4, 5, 80, 60));

        assertEquals(new Rect(4, 5, 20, 8), stack.children().get(0).bounds());
        assertEquals(new Rect(4, 16, 30, 10), stack.children().get(1).bounds());
    }

    @Test
    void borderRadiusFlowsFromStyleToChromeAndClipCommands() {
        Box box = new Box(List.of(new FixedComponent("child", 10, 10)), SlateStyle.builder()
            .padding(Insets.all(2))
            .backgroundColor(0xFF111111)
            .border(new SlateBorder(0xFFFFFFFF, 1))
            .borderRadius(6)
            .clipContent(true)
            .build());

        box.measure(new SlateLayoutContext(null), new Size(80, 80));
        box.layout(new SlateLayoutContext(null), new Rect(0, 0, 40, 24));
        List<DrawCommand> commands = collect(box);

        DrawRectCommand background = first(commands, DrawRectCommand.class);
        DrawBorderCommand border = first(commands, DrawBorderCommand.class);
        PushClipCommand clip = first(commands, PushClipCommand.class);

        assertEquals(6, background.radius());
        assertEquals(6, border.radius());
        assertEquals(new Rect(2, 2, 36, 20), clip.rect());
        assertEquals(4, clip.radius());
    }

    @Test
    void authoredStyleCanExpressBorderRadius() {
        JsonObject root = new JsonObject();
        root.addProperty("componentType", "Box");
        root.add("children", new JsonArray());
        root.add("bindings", new JsonObject());
        JsonObject props = new JsonObject();
        props.addProperty("style-backgroundToken", "color.surface");
        props.addProperty("style-borderThickness", "1");
        props.addProperty("style-border-radius", "7");
        root.add("props", props);

        SlateComponent component = new SlateIrRuntimeFactory().buildComponentTree(root, StateProvider.EMPTY, Theme.DEFAULT);
        component.measure(new SlateLayoutContext(null), new Size(80, 80));
        component.layout(new SlateLayoutContext(null), new Rect(0, 0, 40, 20));
        List<DrawCommand> commands = collect(component);

        assertEquals(7, first(commands, DrawRectCommand.class).radius());
        assertEquals(7, first(commands, DrawBorderCommand.class).radius());
    }

    @Test
    void focusAndClickRegressionForButton() {
        AtomicInteger clicks = new AtomicInteger();
        Button button = new Button("Run", "test.run", SlateStyle.EMPTY);
        button.measure(new SlateLayoutContext(null), new Size(100, 40));
        button.layout(new SlateLayoutContext(null), new Rect(0, 0, 80, 24));
        SlateCommandRegistry commands = new SlateCommandRegistry().register("test.run", ignored -> clicks.incrementAndGet());
        TestScreen screen = new TestScreen(button, commands);
        SlateInteractionContext interaction = interaction(screen, commands);

        button.mouseClicked(interaction, 4, 4, 0);
        assertEquals(button, screen.focusedComponent());
        assertTrue(button.mouseReleased(interaction, 4, 4, 0));

        assertEquals(1, clicks.get());
        DrawBorderCommand focusBorder = first(collect(button), DrawBorderCommand.class);
        assertTrue(focusBorder.radius() > 0);
    }

    @Test
    void slotGridMeasuresHitTestsFocusesAndDispatchesClick() {
        AtomicInteger slotClicked = new AtomicInteger(-1);
        SlotGrid grid = new SlotGrid(new StaticContainerSlotProvider(List.of(
            new ContainerSlot(0, "minecraft:stone", 64, true),
            new ContainerSlot(1, "minecraft:dirt", 1, true),
            ContainerSlot.empty(2)
        )), 2, 18, 2, "slot.click", SlateStyle.EMPTY);
        Size measured = grid.measure(new SlateLayoutContext(null), new Size(100, 100));
        grid.layout(new SlateLayoutContext(null), new Rect(0, 0, measured.width(), measured.height()));
        SlateCommandRegistry commands = new SlateCommandRegistry().register("slot.click", ignored -> slotClicked.set(1));
        TestScreen screen = new TestScreen(grid, commands);
        SlateInteractionContext interaction = interaction(screen, commands);

        assertEquals(new Size(46, 46), measured);
        assertNotNull(grid.slotAt(25, 6));
        assertEquals(1, grid.slotAt(25, 6).index());
        assertTrue(grid.mouseClicked(interaction, 25, 6, 0));
        assertEquals(1, slotClicked.get());
        assertEquals(grid, screen.focusedComponent());
    }

    private static List<DrawCommand> collect(SlateComponent component) {
        List<DrawCommand> commands = new ArrayList<>();
        component.collectDrawCommands(new SlateRenderContext(false, Theme.DEFAULT), commands);
        return commands;
    }

    private static <T extends DrawCommand> T first(List<DrawCommand> commands, Class<T> type) {
        return assertInstanceOf(type, commands.stream().filter(type::isInstance).findFirst().orElseThrow());
    }

    private static SlateInteractionContext interaction(SlateScreen screen, SlateCommandRegistry commands) {
        return new SlateInteractionContext(
            commands,
            new CommandContext(null, screen),
            ignored -> {},
            ignored -> {},
            screen,
            StateProvider.EMPTY,
            Theme.DEFAULT
        );
    }

    private static final class FixedComponent extends SlateComponent {

        private final String name;
        private final Size size;

        private FixedComponent(String name, int width, int height) {
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

    private static final class TestScreen extends SlateScreen {

        private TestScreen(SlateComponent root, SlateCommandRegistry commands) {
            super(Component.literal("Test"), root, commands, StateProvider.EMPTY, Theme.DEFAULT, false);
        }
    }
}
