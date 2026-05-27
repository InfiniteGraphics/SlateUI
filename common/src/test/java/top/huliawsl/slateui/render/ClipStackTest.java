package top.huliawsl.slateui.render;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import top.huliawsl.slateui.layout.Rect;

class ClipStackTest {

    @Test
    void singleClipPushAndPopStayBalanced() {
        ClipStack clipStack = new ClipStack();

        ClipStack.Entry entry = clipStack.push(new Rect(0, 0, 40, 40));

        assertTrue(entry.enabled());
        assertFalse(clipStack.shouldSkip(new DrawRectCommand(new Rect(0, 0, 10, 10), 0xFFFFFFFF)));
        assertTrue(clipStack.popEnabled());
        assertTrue(clipStack.isEmpty());
    }

    @Test
    void nestedClipWithIntersectionStaysRenderable() {
        ClipStack clipStack = new ClipStack();

        clipStack.push(new Rect(0, 0, 40, 40));
        ClipStack.Entry entry = clipStack.push(new Rect(10, 10, 20, 20));

        assertTrue(entry.enabled());
        assertFalse(clipStack.isBlocked());
        assertFalse(clipStack.shouldSkip(new DrawTextCommand(12, 12, "ok", 0xFFFFFFFF)));
        assertTrue(clipStack.popEnabled());
        assertTrue(clipStack.popEnabled());
    }

    @Test
    void emptyIntersectionBlocksDrawCommandsUntilPop() {
        ClipStack clipStack = new ClipStack();

        clipStack.push(new Rect(0, 0, 20, 20));
        ClipStack.Entry entry = clipStack.push(new Rect(30, 30, 10, 10));

        assertFalse(entry.enabled());
        assertTrue(clipStack.isBlocked());
        assertTrue(clipStack.shouldSkip(new DrawRectCommand(new Rect(0, 0, 10, 10), 0xFFFFFFFF)));
        assertFalse(clipStack.shouldSkip(new PushClipCommand(new Rect(0, 0, 1, 1))));
        assertFalse(clipStack.popEnabled());
        assertFalse(clipStack.isBlocked());
        assertFalse(clipStack.shouldSkip(new DrawRectCommand(new Rect(0, 0, 10, 10), 0xFFFFFFFF)));
        assertTrue(clipStack.popEnabled());
        assertTrue(clipStack.isEmpty());
    }

    @Test
    void popOnEmptyStackDoesNotUnderflow() {
        ClipStack clipStack = new ClipStack();

        assertFalse(clipStack.popEnabled());
        assertTrue(clipStack.isEmpty());
    }
}
