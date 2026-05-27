package top.huliawsl.slateui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import top.huliawsl.slateui.api.MutableStateProvider;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;
import top.huliawsl.slateui.api.component.Input;
import top.huliawsl.slateui.api.component.Button;
import top.huliawsl.slateui.api.component.Text;
import top.huliawsl.slateui.api.component.ScrollView;
import top.huliawsl.slateui.api.component.Stack;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.PopClipCommand;
import top.huliawsl.slateui.render.PushClipCommand;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

class RuntimeComponentsTest {

    @Test
    void stackProducesStableLayoutSnapshot() {
        Stack stack = new Stack(StackDirection.COLUMN, List.of(
            new FixedComponent(60, 10),
            new FixedComponent(80, 12)
        ), SlateStyle.builder().gap(6).build());

        stack.measure(new SlateLayoutContext(null), new Size(200, 200));
        stack.layout(new SlateLayoutContext(null), new Rect(0, 0, 200, 200));

        assertEquals("Stack rect=Rect[x=0, y=0, width=200, height=200] hovered=false pressed=false focused=false\n  FixedComponent rect=Rect[x=0, y=0, width=60, height=10] hovered=false pressed=false focused=false\n  FixedComponent rect=Rect[x=0, y=16, width=80, height=12] hovered=false pressed=false focused=false\n", stack.dumpComponentTree());
    }

    @Test
    void scrollViewLayoutsContentWithViewportOffset() {
        ScrollView scrollView = new ScrollView(new Stack(StackDirection.COLUMN, List.of(
            new FixedComponent(100, 50),
            new FixedComponent(100, 50),
            new FixedComponent(100, 50)
        ), SlateStyle.builder().gap(4).build()), SlateStyle.builder().width(120).height(80).build());

        scrollView.measure(new SlateLayoutContext(null), new Size(120, 80));
        scrollView.layout(new SlateLayoutContext(null), new Rect(0, 0, 120, 80));

        assertTrue(scrollView.dumpComponentTree().contains("ScrollView rect=Rect[x=0, y=0, width=120, height=80]"));
    }

    @Test
    void textWrapsWithinAvailableWidth() {
        Text text = new Text("Slate Tester / Ready");

        Size measured = text.measure(new SlateLayoutContext(null), new Size(42, 100));

        assertTrue(measured.height() > 9);
        assertTrue(measured.width() <= 42);
    }

    @Test
    void inputClipsOverflowingContent() {
        Input input = new Input("placeholder", "Slate Tester sssssssssssssssssssssssssssssssss", null, SlateStyle.builder().width(80).padding(top.huliawsl.slateui.layout.Insets.all(4)).build());
        Size measured = input.measure(new SlateLayoutContext(null), new Size(80, 100));
        input.layout(new SlateLayoutContext(null), new Rect(0, 0, measured.width(), measured.height()));

        List<DrawCommand> commands = new java.util.ArrayList<>();
        input.collectDrawCommands(new SlateRenderContext(false, top.huliawsl.slateui.api.Theme.DEFAULT), commands);

        int pushIndex = -1;
        int textIndex = -1;
        int popIndex = -1;
        for (int index = 0; index < commands.size(); index++) {
            DrawCommand command = commands.get(index);
            if (command instanceof PushClipCommand) {
                pushIndex = index;
            } else if (command instanceof top.huliawsl.slateui.render.DrawTextCommand) {
                textIndex = index;
            } else if (command instanceof PopClipCommand) {
                popIndex = index;
            }
        }

        assertTrue(pushIndex >= 0);
        assertTrue(textIndex > pushIndex);
        assertTrue(popIndex > textIndex);
    }

    @Test
    void buttonHasDefaultChromeWithoutExplicitStyle() {
        Button button = new Button("Run", "demo.run", SlateStyle.EMPTY);
        Size measured = button.measure(new SlateLayoutContext(null), new Size(120, 40));
        button.layout(new SlateLayoutContext(null), new Rect(0, 0, measured.width(), measured.height()));

        List<DrawCommand> commands = new java.util.ArrayList<>();
        button.collectDrawCommands(new SlateRenderContext(false, top.huliawsl.slateui.api.Theme.DEFAULT), commands);

        assertTrue(commands.stream().anyMatch(top.huliawsl.slateui.render.DrawRectCommand.class::isInstance));
        assertTrue(commands.stream().anyMatch(top.huliawsl.slateui.render.DrawBorderCommand.class::isInstance));
        assertTrue(measured.width() > 18);
        assertTrue(measured.height() > 9);
    }

    @Test
    void inputKeepsDefaultChromeWhenOnlyWidthIsOverridden() {
        Input input = new Input("placeholder", "Alex", null, SlateStyle.builder().width(160).build());
        Size measured = input.measure(new SlateLayoutContext(null), new Size(220, 40));
        input.layout(new SlateLayoutContext(null), new Rect(0, 0, measured.width(), measured.height()));

        List<DrawCommand> commands = new java.util.ArrayList<>();
        input.collectDrawCommands(new SlateRenderContext(false, top.huliawsl.slateui.api.Theme.DEFAULT), commands);

        assertEquals(160, measured.width());
        assertTrue(commands.stream().anyMatch(top.huliawsl.slateui.render.DrawRectCommand.class::isInstance));
        assertTrue(commands.stream().anyMatch(top.huliawsl.slateui.render.DrawBorderCommand.class::isInstance));
    }

    @Test
    void inputCanStayEmptyAfterDeletingInitialValue() {
        Input input = new Input("placeholder", "Alex", null, SlateStyle.EMPTY);
        input.measure(new SlateLayoutContext(null), new Size(120, 40));
        input.layout(new SlateLayoutContext(null), new Rect(0, 0, 120, 24));
        TestScreen screen = new TestScreen(input);
        top.huliawsl.slateui.runtime.SlateInteractionContext interaction = new top.huliawsl.slateui.runtime.SlateInteractionContext(
            new top.huliawsl.slateui.command.SlateCommandRegistry(),
            new top.huliawsl.slateui.command.CommandContext(null, screen),
            ignored -> {},
            ignored -> {},
            screen,
            top.huliawsl.slateui.api.StateProvider.EMPTY,
            top.huliawsl.slateui.api.Theme.DEFAULT
        );
        input.setFocused(true);

        input.keyPressed(interaction, 259, 0, 0);
        input.keyPressed(interaction, 259, 0, 0);
        input.keyPressed(interaction, 259, 0, 0);
        input.measure(new SlateLayoutContext(null), new Size(120, 40));

        List<DrawCommand> commands = new java.util.ArrayList<>();
        input.collectDrawCommands(new SlateRenderContext(false, top.huliawsl.slateui.api.Theme.DEFAULT), commands);

        assertTrue(commands.stream()
            .filter(top.huliawsl.slateui.render.DrawTextCommand.class::isInstance)
            .map(top.huliawsl.slateui.render.DrawTextCommand.class::cast)
            .noneMatch(command -> command.text().contains("Alex")));
    }

    @Test
    void providerNotifiesListenersOnDirtyUpdate() {
        MutableStateProvider provider = new MutableStateProvider();
        MutableString dirtyPath = new MutableString();
        provider.addListener(path -> dirtyPath.value = path);

        provider.set("settings.playerName", "Alex");

        assertEquals("settings.playerName", dirtyPath.value);
        assertEquals("Alex", provider.get("settings.playerName"));
    }

    private static final class FixedComponent extends SlateComponent {

        private final Size size;

        private FixedComponent(int width, int height) {
            super(SlateStyle.EMPTY);
            this.size = new Size(width, height);
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
        public void collectDrawCommands(SlateRenderContext context, List<top.huliawsl.slateui.render.DrawCommand> commands) {
        }
    }

    private static final class MutableString {
        private String value = "";
    }

    private static final class TestScreen extends top.huliawsl.slateui.api.SlateScreen {

        private TestScreen(SlateComponent root) {
            super(
                net.minecraft.network.chat.Component.literal("Test"),
                root,
                new top.huliawsl.slateui.command.SlateCommandRegistry(),
                top.huliawsl.slateui.api.StateProvider.EMPTY,
                top.huliawsl.slateui.api.Theme.DEFAULT,
                false
            );
        }
    }
}
