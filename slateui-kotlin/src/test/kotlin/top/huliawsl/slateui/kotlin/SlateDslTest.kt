package top.huliawsl.slateui.kotlin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import top.huliawsl.slateui.api.StackDirection
import top.huliawsl.slateui.api.component.Box
import top.huliawsl.slateui.api.component.Button
import top.huliawsl.slateui.api.component.OverlayRoot
import top.huliawsl.slateui.api.component.Stack
import top.huliawsl.slateui.api.component.Text

class SlateDslTest {

    @Test
    fun buildsRuntimeTree() {
        val root: OverlayRoot = overlayRoot {
            box {
                stack(StackDirection.COLUMN) {
                    text("Hello")
                    button("Close", "screen.close")
                }
            }
        }

        assertEquals(1, root.children().size)
        assertTrue(root.children().first() is Box)
        val box = root.children().first() as Box
        assertTrue(box.children().first() is Stack)
    }

    @Test
    fun dslMatchesJavaComponentApi() {
        val root = overlayRoot {
            stack(StackDirection.COLUMN) {
                text("Settings")
                button("Done", "screen.close")
            }
        }

        val stack = root.children().first() as Stack
        assertTrue(stack.children()[0] is Text)
        assertTrue(stack.children()[1] is Button)
    }

    @Test
    fun dslShapeCanMatchSlateAuthoring() {
        val root = overlayRoot {
            text("Settings")
            button("Done", "screen.close")
        }

        assertEquals(listOf("Text", "Button"), root.children().map { it.debugName() })
    }
}
