package top.huliawsl.slateui.runtime;

import top.huliawsl.slateui.layout.Rect;

public interface SlateRenderer {

    void fill(Rect rect, int color, int radius);

    void drawText(int x, int y, String text, int color);

    void drawTexture(Rect rect, String texture, int u, int v, int textureWidth, int textureHeight, int regionWidth, int regionHeight);

    void pushClip(Rect rect, int radius);

    void popClip();
}
