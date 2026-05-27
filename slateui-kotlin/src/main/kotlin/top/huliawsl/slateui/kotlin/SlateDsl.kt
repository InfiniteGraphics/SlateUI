package top.huliawsl.slateui.kotlin

import top.huliawsl.slateui.api.SlateComponent
import top.huliawsl.slateui.api.SlateStyle
import top.huliawsl.slateui.api.StackDirection
import top.huliawsl.slateui.api.component.Box
import top.huliawsl.slateui.api.component.Button
import top.huliawsl.slateui.api.component.OverlayRoot
import top.huliawsl.slateui.api.component.Stack
import top.huliawsl.slateui.api.component.Text

class ComponentScope {
    private val children = mutableListOf<SlateComponent>()

    fun text(value: String, style: SlateStyle = SlateStyle.EMPTY) {
        children += Text(value, style)
    }

    fun button(label: String, command: String, style: SlateStyle = SlateStyle.EMPTY) {
        children += Button(label, command, style)
    }

    fun box(style: SlateStyle = SlateStyle.EMPTY, build: ComponentScope.() -> Unit) {
        children += Box(ComponentScope().apply(build).build(), style)
    }

    fun stack(direction: StackDirection, style: SlateStyle = SlateStyle.EMPTY, build: ComponentScope.() -> Unit) {
        children += Stack(direction, ComponentScope().apply(build).build(), style)
    }

    internal fun build(): List<SlateComponent> = children.toList()
}

fun overlayRoot(style: SlateStyle = SlateStyle.EMPTY, build: ComponentScope.() -> Unit): OverlayRoot {
    return OverlayRoot(ComponentScope().apply(build).build(), style)
}
