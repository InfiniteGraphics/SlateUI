package top.huliawsl.slateui.api.component;

import java.util.List;
import java.util.Objects;
import top.huliawsl.slateui.api.SlateText;
import top.huliawsl.slateui.layout.Point;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.render.DrawBorderCommand;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.DrawLineCommand;
import top.huliawsl.slateui.render.DrawPolylineCommand;
import top.huliawsl.slateui.render.DrawRawCommand;
import top.huliawsl.slateui.render.DrawRectCommand;
import top.huliawsl.slateui.render.DrawTextCommand;
import top.huliawsl.slateui.render.DrawTextureCommand;
import top.huliawsl.slateui.render.RawDrawCallback;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public final class CanvasDrawContext {

    private final SlateRenderContext renderContext;
    private final Rect bounds;
    private final List<DrawCommand> commands;

    CanvasDrawContext(SlateRenderContext renderContext, Rect bounds, List<DrawCommand> commands) {
        this.renderContext = Objects.requireNonNull(renderContext, "renderContext");
        this.bounds = bounds == null ? Rect.ZERO : bounds;
        this.commands = Objects.requireNonNull(commands, "commands");
    }

    public SlateRenderContext renderContext() {
        return renderContext;
    }

    public Rect bounds() {
        return bounds;
    }

    public Point point(int localX, int localY) {
        return new Point(bounds.x() + localX, bounds.y() + localY);
    }

    public Rect rect(int localX, int localY, int width, int height) {
        return new Rect(bounds.x() + localX, bounds.y() + localY, Math.max(0, width), Math.max(0, height));
    }

    public void fill(Rect rect, int color) {
        fill(rect, color, 0);
    }

    public void fill(Rect rect, int color, int radius) {
        commands.add(new DrawRectCommand(rect, color, radius));
    }

    public void fillLocal(int x, int y, int width, int height, int color) {
        fill(rect(x, y, width, height), color, 0);
    }

    public void border(Rect rect, int color, int thickness, int radius) {
        commands.add(new DrawBorderCommand(rect, color, thickness, radius));
    }

    public void line(Point start, Point end, int color, int thickness) {
        commands.add(new DrawLineCommand(start, end, color, thickness));
    }

    public void lineLocal(int startX, int startY, int endX, int endY, int color, int thickness) {
        line(point(startX, startY), point(endX, endY), color, thickness);
    }

    public void polyline(List<Point> points, int color, int thickness) {
        commands.add(new DrawPolylineCommand(points, color, thickness));
    }

    public void text(int x, int y, String text, int color) {
        commands.add(new DrawTextCommand(bounds.x() + x, bounds.y() + y, text, color));
    }

    public void text(int x, int y, SlateText text, int color) {
        commands.add(new DrawTextCommand(bounds.x() + x, bounds.y() + y, text, color));
    }

    public void texture(Rect rect, String texture, int u, int v, int textureWidth, int textureHeight, int regionWidth, int regionHeight) {
        commands.add(new DrawTextureCommand(rect, texture, u, v, textureWidth, textureHeight, regionWidth, regionHeight, false));
    }

    public void raw(String description, RawDrawCallback callback) {
        commands.add(new DrawRawCommand(bounds, callback, description));
    }
}
