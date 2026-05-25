package top.huliawsl.slateui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import top.huliawsl.slateui.api.MutableStateProvider;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;
import top.huliawsl.slateui.api.component.ScrollView;
import top.huliawsl.slateui.api.component.Stack;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
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
}