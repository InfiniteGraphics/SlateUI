package top.huliawsl.slateui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.lwjgl.glfw.GLFW;
import org.junit.jupiter.api.Test;
import top.huliawsl.slateui.api.MutableStateProvider;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.SlateText;
import top.huliawsl.slateui.api.StackDirection;
import top.huliawsl.slateui.api.component.Image;
import top.huliawsl.slateui.api.component.Input;
import top.huliawsl.slateui.api.component.Button;
import top.huliawsl.slateui.api.component.Text;
import top.huliawsl.slateui.api.component.ScrollView;
import top.huliawsl.slateui.api.component.Stack;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommandDispatcher;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.DrawTextCommand;
import top.huliawsl.slateui.render.DrawTextureCommand;
import top.huliawsl.slateui.render.PopClipCommand;
import top.huliawsl.slateui.render.PushClipCommand;
import top.huliawsl.slateui.runtime.SlateClipboard;
import top.huliawsl.slateui.runtime.SlateHost;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;
import top.huliawsl.slateui.runtime.SlateRenderer;

class RuntimeComponentsTest {

    @Test
    void stackProducesStableLayoutSnapshot() {
        Stack stack = new Stack(StackDirection.COLUMN, List.of(
            new FixedComponent(60, 10),
            new FixedComponent(80, 12)
        ), SlateStyle.builder().gap(6).build());

        stack.measure(new SlateLayoutContext(null), new Size(200, 200));
        stack.layout(new SlateLayoutContext(null), new Rect(0, 0, 200, 200));

        assertEquals("Stack path=Stack rect=Rect[x=0, y=0, width=200, height=200] measured=Size[width=80, height=28] clip=false hovered=false pressed=false focused=false\n  FixedComponent path=Stack/FixedComponent[0] rect=Rect[x=0, y=0, width=60, height=10] measured=Size[width=60, height=10] clip=false hovered=false pressed=false focused=false\n  FixedComponent path=Stack/FixedComponent[1] rect=Rect[x=0, y=16, width=80, height=12] measured=Size[width=80, height=12] clip=false hovered=false pressed=false focused=false\n", stack.dumpComponentTree());
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
    void nestedScrollViewHandlesWheelBeforeOuterViewport() {
        ScrollView inner = new ScrollView(new Stack(StackDirection.COLUMN, List.of(
            new FixedComponent(60, 30),
            new FixedComponent(60, 30),
            new FixedComponent(60, 30)
        ), SlateStyle.EMPTY), SlateStyle.builder().width(60).height(40).build());
        ScrollView outer = new ScrollView(new Stack(StackDirection.COLUMN, List.of(
            new FixedComponent(60, 60),
            inner,
            new FixedComponent(60, 60)
        ), SlateStyle.EMPTY), SlateStyle.builder().width(80).height(80).build());

        SlateLayoutContext layoutContext = new SlateLayoutContext(null);
        outer.measure(layoutContext, new Size(80, 80));
        outer.layout(layoutContext, new Rect(0, 0, 80, 80));
        int innerViewportY = inner.bounds().y();
        TestScreen screen = new TestScreen(outer);

        SlateInteractionContext interaction = new SlateInteractionContext(
            new top.huliawsl.slateui.command.SlateCommandRegistry(),
            new top.huliawsl.slateui.command.CommandContext(null, screen),
            ignored -> {},
            ignored -> {},
            screen,
            top.huliawsl.slateui.api.StateProvider.EMPTY,
            top.huliawsl.slateui.api.Theme.DEFAULT
        );

        assertTrue(outer.mouseScrolled(interaction, 10, innerViewportY + 5, -1));

        outer.layout(layoutContext, new Rect(0, 0, 80, 80));

        assertEquals(innerViewportY, inner.bounds().y());
        assertEquals(innerViewportY - 16, inner.children().get(0).bounds().y());
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
    void inputSupportsSelectionPasteDeleteAndMaxLength() {
        Input input = new Input(
            top.huliawsl.slateui.api.StateProvider.EMPTY,
            "placeholder",
            ignored -> "abc",
            null,
            null,
            null,
            null,
            4,
            SlateStyle.EMPTY
        );
        input.measure(new SlateLayoutContext(null), new Size(120, 40));
        input.layout(new SlateLayoutContext(null), new Rect(0, 0, 120, 24));
        TestScreen screen = new TestScreen(input);
        SlateClipboard clipboard = new SlateClipboard() {
            @Override
            public String get() {
                return "xyz123";
            }

            @Override
            public void set(String value) {
            }
        };
        SlateInteractionContext interaction = new SlateInteractionContext(
            new top.huliawsl.slateui.command.SlateCommandRegistry(),
            new top.huliawsl.slateui.command.CommandContext(null, screen),
            ignored -> {},
            ignored -> {},
            screen,
            top.huliawsl.slateui.api.StateProvider.EMPTY,
            top.huliawsl.slateui.api.Theme.DEFAULT,
            clipboard
        );
        input.setFocused(true);

        input.keyPressed(interaction, GLFW.GLFW_KEY_A, 0, GLFW.GLFW_MOD_CONTROL);
        input.keyPressed(interaction, GLFW.GLFW_KEY_V, 0, GLFW.GLFW_MOD_CONTROL);
        input.keyPressed(interaction, GLFW.GLFW_KEY_HOME, 0, 0);
        input.keyPressed(interaction, GLFW.GLFW_KEY_DELETE, 0, 0);

        List<DrawCommand> commands = new java.util.ArrayList<>();
        input.collectDrawCommands(new SlateRenderContext(false, top.huliawsl.slateui.api.Theme.DEFAULT), commands);

        assertTrue(commands.stream()
            .filter(DrawTextCommand.class::isInstance)
            .map(DrawTextCommand.class::cast)
            .anyMatch(command -> command.text().contains("yz1")));
    }

    @Test
    void inputSyncsExternalStateWhenNotFocused() {
        MutableStateProvider provider = new MutableStateProvider().set("name", "Alex");
        Input input = new Input(provider, "placeholder", ignored -> String.valueOf(provider.get("name")), null, null, SlateStyle.EMPTY);

        input.measure(new SlateLayoutContext(null), new Size(120, 40));
        provider.set("name", "Sam");
        input.measure(new SlateLayoutContext(null), new Size(120, 40));
        input.layout(new SlateLayoutContext(null), new Rect(0, 0, 120, 24));

        List<DrawCommand> commands = new java.util.ArrayList<>();
        input.collectDrawCommands(new SlateRenderContext(false, top.huliawsl.slateui.api.Theme.DEFAULT), commands);

        assertTrue(commands.stream()
            .filter(DrawTextCommand.class::isInstance)
            .map(DrawTextCommand.class::cast)
            .anyMatch(command -> command.text().contains("Sam")));
    }

    @Test
    void drawCommandDispatcherUsesRendererAdapterAndKeepsTranslatableText() {
        SlateText.Translatable text = new SlateText.Translatable("slate.test.key", List.of("arg"));
        List<DrawCommand> commands = List.of(new DrawTextCommand(1, 2, text, 0xFFFFFFFF));
        RecordingRenderer renderer = new RecordingRenderer();

        DrawCommandDispatcher.render(commands, renderer);

        assertEquals(text, renderer.lastText);
        assertEquals(SlateRenderer.Capabilities.BASIC, renderer.capabilities());
    }

    @Test
    void interactionContextCanUseHostWithoutSlateScreen() {
        RecordingHost host = new RecordingHost();
        SlateInteractionContext interaction = new SlateInteractionContext(
            new top.huliawsl.slateui.command.SlateCommandRegistry(),
            new top.huliawsl.slateui.command.CommandContext(host),
            ignored -> {},
            ignored -> {},
            host,
            top.huliawsl.slateui.api.StateProvider.EMPTY,
            top.huliawsl.slateui.api.Theme.DEFAULT
        );
        Button button = new Button("Focus", null, SlateStyle.EMPTY);

        interaction.requestFocus(button);
        interaction.requestRebuild("test");
        interaction.logDiagnostic("diag");
        interaction.commandContext().host().closeScreen();

        assertEquals(button, host.focusedComponent());
        assertEquals("test", host.rebuildReason.value);
        assertEquals("diag", host.diagnostic.value);
        assertTrue(host.closed);
    }

    @Test
    void coreRuntimeSourcesDoNotImportMinecraft() throws Exception {
        Path sourceRoot = Path.of("src/main/java/top/huliawsl/slateui");
        List<String> corePaths = List.of(
            "api/component",
            "layout",
            "binding",
            "command/CommandContext.java",
            "runtime/SlateClipboard.java",
            "runtime/SlateHost.java",
            "runtime/SlateInteractionContext.java",
            "runtime/SlateLayoutContext.java",
            "runtime/SlateRenderContext.java",
            "runtime/SlateRenderer.java",
            "runtime/SlateTextMeasurer.java",
            "render/DrawBorderCommand.java",
            "render/DrawCommand.java",
            "render/DrawCommandDispatcher.java",
            "render/DrawDebugRectCommand.java",
            "render/DrawRectCommand.java",
            "render/DrawTextCommand.java",
            "render/DrawTextureCommand.java",
            "render/PopClipCommand.java",
            "render/PushClipCommand.java"
        );

        for (String relative : corePaths) {
            Path path = sourceRoot.resolve(relative);
            if (Files.isDirectory(path)) {
                try (var files = Files.walk(path)) {
                    for (Path file : files.filter(item -> item.toString().endsWith(".java")).toList()) {
                        assertTrue(!Files.readString(file).contains("net.minecraft"), file.toString());
                    }
                }
                continue;
            }
            assertTrue(!Files.readString(path).contains("net.minecraft"), path.toString());
        }
    }

    @Test
    void imageSupportsTextureRegions() {
        Image image = new Image("minecraft:textures/gui/widgets.png", 4, 5, 256, 128, 18, 19, SlateStyle.builder().width(18).height(19).build());
        image.measure(new SlateLayoutContext(null), new Size(40, 40));
        image.layout(new SlateLayoutContext(null), new Rect(0, 0, 18, 19));

        List<DrawCommand> commands = new java.util.ArrayList<>();
        image.collectDrawCommands(new SlateRenderContext(false, top.huliawsl.slateui.api.Theme.DEFAULT), commands);
        DrawTextureCommand texture = commands.stream()
            .filter(DrawTextureCommand.class::isInstance)
            .map(DrawTextureCommand.class::cast)
            .findFirst()
            .orElseThrow();

        assertEquals(4, texture.u());
        assertEquals(5, texture.v());
        assertEquals(18, texture.regionWidth());
        assertEquals(19, texture.regionHeight());
        assertEquals(256, texture.textureWidth());
        assertEquals(128, texture.textureHeight());
    }

    @Test
    void screenRemovesStateListenerWhenClosed() {
        MutableStateProvider provider = new MutableStateProvider();
        TestScreen screen = new TestScreen(new FixedComponent(10, 10), provider);

        provider.set("name", "before");
        assertTrue(screen.diagnostics().diagnosticsLogDump().contains("state:name"));

        screen.removed();
        String before = screen.diagnostics().diagnosticsLogDump();
        provider.set("name", "after");

        assertEquals(before, screen.diagnostics().diagnosticsLogDump());
    }

    @Test
    void screenMovesFocusWithTabAndShiftTab() {
        Button first = new Button("First", null, SlateStyle.EMPTY);
        Button second = new Button("Second", null, SlateStyle.EMPTY);
        Stack root = new Stack(StackDirection.COLUMN, List.of(first, second), SlateStyle.EMPTY);
        TestScreen screen = new TestScreen(root);

        assertTrue(screen.keyPressed(GLFW.GLFW_KEY_TAB, 0, 0));
        assertEquals(first, screen.focusedComponent());

        assertTrue(screen.keyPressed(GLFW.GLFW_KEY_TAB, 0, GLFW.GLFW_MOD_SHIFT));
        assertEquals(second, screen.focusedComponent());
    }

    @Test
    void scrollViewDoesNotDispatchClicksOutsideViewport() {
        ClickComponent content = new ClickComponent();
        ScrollView scrollView = new ScrollView(content, SlateStyle.builder().width(40).height(20).build());
        scrollView.measure(new SlateLayoutContext(null), new Size(40, 20));
        scrollView.layout(new SlateLayoutContext(null), new Rect(0, 0, 40, 20));
        TestScreen screen = new TestScreen(scrollView);
        SlateInteractionContext interaction = new SlateInteractionContext(
            new top.huliawsl.slateui.command.SlateCommandRegistry(),
            new top.huliawsl.slateui.command.CommandContext(null, screen),
            ignored -> {},
            ignored -> {},
            screen,
            top.huliawsl.slateui.api.StateProvider.EMPTY,
            top.huliawsl.slateui.api.Theme.DEFAULT
        );

        assertEquals(false, scrollView.mouseClicked(interaction, 60, 60, 0));
        assertEquals(0, content.clicks.get());
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

    private static final class ClickComponent extends SlateComponent {

        private final AtomicInteger clicks = new AtomicInteger();

        @Override
        public Size measure(SlateLayoutContext context, Size available) {
            Size size = new Size(100, 100);
            setMeasuredSize(size);
            return size;
        }

        @Override
        public void layout(SlateLayoutContext context, Rect bounds) {
            setBounds(bounds);
        }

        @Override
        public void collectDrawCommands(SlateRenderContext context, List<top.huliawsl.slateui.render.DrawCommand> commands) {
        }

        @Override
        public boolean mouseClicked(SlateInteractionContext context, double mouseX, double mouseY, int button) {
            clicks.incrementAndGet();
            return true;
        }
    }

    private static final class MutableString {
        private String value = "";
    }

    private static final class RecordingHost implements SlateHost {

        private final MutableString rebuildReason = new MutableString();
        private final MutableString diagnostic = new MutableString();
        private SlateComponent focused;
        private boolean closed;

        @Override
        public void requestRebuild(String reason) {
            rebuildReason.value = reason;
        }

        @Override
        public void requestFocus(SlateComponent component) {
            focused = component;
        }

        @Override
        public void clearFocus(SlateComponent component) {
            if (focused == component) {
                focused = null;
            }
        }

        @Override
        public SlateComponent focusedComponent() {
            return focused;
        }

        @Override
        public void closeScreen() {
            closed = true;
        }

        @Override
        public void reportDiagnostic(String entry) {
            diagnostic.value = entry;
        }
    }

    private static final class TestScreen extends top.huliawsl.slateui.api.SlateScreen {

        private TestScreen(SlateComponent root) {
            this(root, top.huliawsl.slateui.api.StateProvider.EMPTY);
        }

        private TestScreen(SlateComponent root, top.huliawsl.slateui.api.StateProvider provider) {
            super(
                net.minecraft.network.chat.Component.literal("Test"),
                root,
                new top.huliawsl.slateui.command.SlateCommandRegistry(),
                provider,
                top.huliawsl.slateui.api.Theme.DEFAULT,
                false
            );
        }
    }

    private static final class RecordingRenderer implements SlateRenderer {

        private SlateText lastText;

        @Override
        public void fill(Rect rect, int color, int radius) {
        }

        @Override
        public void drawBorder(Rect rect, int color, int thickness, int radius) {
        }

        @Override
        public void drawText(int x, int y, SlateText text, int color) {
            lastText = text;
        }

        @Override
        public void drawTexture(Rect rect, String texture, int u, int v, int textureWidth, int textureHeight, int regionWidth, int regionHeight) {
        }

        @Override
        public void pushClip(Rect rect, int radius) {
        }

        @Override
        public void popClip() {
        }
    }
}
