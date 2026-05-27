package top.huliawsl.slateui.kotlin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import top.huliawsl.slateui.api.StackDirection
import top.huliawsl.slateui.api.component.Box
import top.huliawsl.slateui.api.component.OverlayRoot
import top.huliawsl.slateui.api.component.Stack

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
}
