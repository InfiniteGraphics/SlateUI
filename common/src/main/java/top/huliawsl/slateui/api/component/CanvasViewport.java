package top.huliawsl.slateui.api.component;

import top.huliawsl.slateui.layout.Point;
import top.huliawsl.slateui.layout.Rect;

public final class CanvasViewport {

    private double panX;
    private double panY;
    private double zoom;
    private double minZoom;
    private double maxZoom;

    public CanvasViewport() {
        this(0D, 0D, 1D);
    }

    public CanvasViewport(double panX, double panY, double zoom) {
        this.panX = panX;
        this.panY = panY;
        this.zoom = sanitizeZoom(zoom);
        this.minZoom = 0.1D;
        this.maxZoom = 8D;
    }

    public double panX() {
        return panX;
    }

    public double panY() {
        return panY;
    }

    public double zoom() {
        return zoom;
    }

    public double minZoom() {
        return minZoom;
    }

    public double maxZoom() {
        return maxZoom;
    }

    public CanvasViewport pan(double deltaX, double deltaY) {
        panX += deltaX;
        panY += deltaY;
        return this;
    }

    public CanvasViewport panTo(double panX, double panY) {
        this.panX = panX;
        this.panY = panY;
        return this;
    }

    public CanvasViewport zoomTo(double zoom) {
        this.zoom = clampZoom(zoom);
        return this;
    }

    public CanvasViewport zoomRange(double minZoom, double maxZoom) {
        this.minZoom = Math.max(0.001D, Math.min(minZoom, maxZoom));
        this.maxZoom = Math.max(this.minZoom, maxZoom);
        this.zoom = clampZoom(this.zoom);
        return this;
    }

    public CanvasViewport zoomAround(double localX, double localY, double nextZoom) {
        double beforeX = screenToWorldX(localX);
        double beforeY = screenToWorldY(localY);
        this.zoom = clampZoom(nextZoom);
        this.panX = localX - beforeX * this.zoom;
        this.panY = localY - beforeY * this.zoom;
        return this;
    }

    public double screenToWorldX(double localX) {
        return (localX - panX) / zoom;
    }

    public double screenToWorldY(double localY) {
        return (localY - panY) / zoom;
    }

    public double worldToScreenX(double worldX) {
        return worldX * zoom + panX;
    }

    public double worldToScreenY(double worldY) {
        return worldY * zoom + panY;
    }

    public Point screenToWorld(double localX, double localY) {
        return new Point((int) Math.round(screenToWorldX(localX)), (int) Math.round(screenToWorldY(localY)));
    }

    public Point worldToScreen(double worldX, double worldY) {
        return new Point((int) Math.round(worldToScreenX(worldX)), (int) Math.round(worldToScreenY(worldY)));
    }

    public Rect worldToScreen(Rect rect) {
        if (rect == null) {
            return Rect.ZERO;
        }
        int x = (int) Math.round(worldToScreenX(rect.x()));
        int y = (int) Math.round(worldToScreenY(rect.y()));
        int width = Math.max(0, (int) Math.round(rect.width() * zoom));
        int height = Math.max(0, (int) Math.round(rect.height() * zoom));
        return new Rect(x, y, width, height);
    }

    public Rect screenToWorld(Rect rect) {
        if (rect == null) {
            return Rect.ZERO;
        }
        int x = (int) Math.round(screenToWorldX(rect.x()));
        int y = (int) Math.round(screenToWorldY(rect.y()));
        int width = Math.max(0, (int) Math.round(rect.width() / zoom));
        int height = Math.max(0, (int) Math.round(rect.height() / zoom));
        return new Rect(x, y, width, height);
    }

    public CanvasViewport copy() {
        return new CanvasViewport(panX, panY, zoom).zoomRange(minZoom, maxZoom);
    }

    private double clampZoom(double value) {
        return Math.max(minZoom, Math.min(maxZoom, sanitizeZoom(value)));
    }

    private static double sanitizeZoom(double value) {
        if (!Double.isFinite(value) || value <= 0D) {
            return 1D;
        }
        return value;
    }
}
