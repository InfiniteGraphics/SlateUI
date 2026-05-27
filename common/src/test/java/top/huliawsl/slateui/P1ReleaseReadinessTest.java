package top.huliawsl.slateui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;
import top.huliawsl.slateui.api.HorizontalAlign;
import top.huliawsl.slateui.api.SlateBorder;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateScreen;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;
import top.huliawsl.slateui.api.StateProvider;
import top.huliawsl.slateui.api.Theme;
import top.huliawsl.slateui.api.VerticalAlign;
import top.huliawsl.slateui.api.component.Box;
import top.huliawsl.slateui.api.component.Button;
import top.huliawsl.slateui.api.component.Stack;
import top.huliawsl.slateui.authoring.SlateIrRuntimeFactory;
import top.huliawsl.slateui.command.CommandContext;
import top.huliawsl.slateui.command.SlateCommandRegistry;
import top.huliawsl.slateui.debug.SlateDiagnostics;
import top.huliawsl.slateui.debug.SlateRuntimeException;
import top.huliawsl.slateui.layout.Insets;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.PushClipCommand;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

class P1ReleaseReadinessTest {

    @Test
    void stackAlignsMainAxisAndClipsNestedChildren() {
        Stack stack = new Stack(StackDirection.ROW, List.of(
            new FixedComponent("left", 20, 10),
            new FixedComponent("right", 20, 10)
        ), SlateStyle.builder()
            .padding(Insets.all(2))
            .gap(4)
            .horizontalAlign(HorizontalAlign.CENTER)
            .verticalAlign(VerticalAlign.END)
            .clipContent(true)
            .build());

        stack.measure(new SlateLayoutContext(null), new Size(100, 40));
        stack.layout(new SlateLayoutContext(null), new Rect(0, 0, 100, 40));
        List<DrawCommand> commands = collect(stack);

        assertEquals(new Rect(28, 28, 20, 10), stack.children().get(0).bounds());
        assertEquals(new Rect(52, 28, 20, 10), stack.children().get(1).bounds());
        PushClipCommand clip = assertInstanceOf(PushClipCommand.class, commands.stream().filter(PushClipCommand.class::isInstance).findFirst().orElseThrow());
        assertEquals(new Rect(2, 2, 96, 36), clip.rect());
    }

    @Test
    void authoredStyleParsesAlignmentClipAndSizeConstraints() {
        JsonObject root = new JsonObject();
        root.addProperty("componentType", "Box");
        root.add("children", new JsonArray());
        root.add("bindings", new JsonObject());
        JsonObject props = new JsonObject();
        props.addProperty("style-width", "400");
        props.addProperty("style-max-width", "120");
        props.addProperty("style-min-height", "24");
        props.addProperty("style-horizontal-align", "center");
        props.addProperty("style-vertical-align", "end");
        props.addProperty("style-clip-content", "true");
        root.add("props", props);

        SlateComponent component = new SlateIrRuntimeFactory().buildComponentTree(root, StateProvider.EMPTY, Theme.DEFAULT);
        Size measured = component.measure(new SlateLayoutContext(null), new Size(300, 300));

        assertEquals(new Size(120, 24), measured);
        assertEquals(HorizontalAlign.CENTER, component.style().horizontalAlign());
        assertEquals(VerticalAlign.END, component.style().verticalAlign());
        assertTrue(component.style().clipContent());
    }

    @Test
    void diagnosticsExposeHitRegionsChromeAndBindingResults() {
        Box root = new Box(List.of(new FixedComponent("leaf", 20, 10)), SlateStyle.builder()
            .padding(Insets.all(2))
            .border(new SlateBorder(0xFFFFFFFF, 1))
            .borderRadius(4)
            .clipContent(true)
            .build());
        root.measure(new SlateLayoutContext(null), new Size(100, 100));
        root.layout(new SlateLayoutContext(null), new Rect(0, 0, 80, 40));
        List<DrawCommand> commands = collect(root);
        SlateDiagnostics diagnostics = new SlateDiagnostics();

        diagnostics.capture(root, commands, "<none>", "binding root Text.value <- user.name => Alex", "user.name=Alex", Theme.DEFAULT);
        diagnostics.capturePointer(4, 4);

        assertTrue(diagnostics.hitRegionDump().contains("radius=4"));
        assertTrue(diagnostics.hitRegionDump().contains("clip=true"));
        assertTrue(diagnostics.bindingDump().contains("user.name"));
        assertTrue(diagnostics.hitTestDump().contains("Box > leaf"));
        assertTrue(diagnostics.drawCommandDump().contains("color=#"));
    }

    @Test
    void commandFailuresCarryComponentPathAndCommandId() {
        Button button = new Button("Boom", "demo.boom", SlateStyle.EMPTY);
        button.measure(new SlateLayoutContext(null), new Size(100, 40));
        button.layout(new SlateLayoutContext(null), new Rect(0, 0, 80, 24));
        SlateCommandRegistry commands = new SlateCommandRegistry().register("demo.boom", ignored -> {
            throw new IllegalStateException("boom");
        });
        TestScreen screen = new TestScreen(button, commands);
        SlateDiagnostics diagnostics = new SlateDiagnostics();
        diagnostics.capture(button, collect(button), "<none>", "<none>", "<empty>", Theme.DEFAULT);
        SlateInteractionContext interaction = interaction(screen, commands);

        button.mouseClicked(interaction, 4, 4, 0);
        SlateRuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(SlateRuntimeException.class, () ->
            button.mouseReleased(interaction, 4, 4, 0));

        assertEquals("command", exception.stage());
        assertTrue(exception.componentPath().contains("Button"));
        assertEquals("demo.boom", exception.detail());
    }

    private static List<DrawCommand> collect(SlateComponent component) {
        List<DrawCommand> commands = new ArrayList<>();
        component.collectDrawCommands(new SlateRenderContext(false, Theme.DEFAULT), commands);
        return commands;
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

    private static final class TestScreen extends SlateScreen {

        private TestScreen(SlateComponent root, SlateCommandRegistry commands) {
            super(Component.literal("Test"), root, commands, StateProvider.EMPTY, Theme.DEFAULT, false);
        }
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
            setBounds(new Rect(bounds.x(), bounds.y(), Math.min(size.width(), bounds.width()), Math.min(size.height(), bounds.height())));
        }

        @Override
        public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
            emitBoxChrome(context, commands);
        }
    }
}
