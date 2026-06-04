package top.huliawsl.slateui.render;

import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.runtime.SlateRenderer;

@FunctionalInterface
public interface RawDrawCallback {

    void draw(SlateRenderer renderer, Rect bounds);
}
