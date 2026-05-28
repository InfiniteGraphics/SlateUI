package top.huliawsl.slateui.render;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import top.huliawsl.slateui.api.SlateText;
import top.huliawsl.slateui.layout.Insets;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.runtime.SlateRenderer;

public final class MinecraftSlateRenderer implements SlateRenderer {

    private static final Capabilities CAPABILITIES = new Capabilities(true, true, true, false, true);

    private final GuiGraphics graphics;
    private final Font font;
    private final ClipStack clipStack = new ClipStack();

    public MinecraftSlateRenderer(GuiGraphics graphics, Font font) {
        this.graphics = graphics;
        this.font = font;
    }

    @Override
    public Capabilities capabilities() {
        return CAPABILITIES;
    }

    @Override
    public void fill(Rect rect, int color, int radius) {
        Rect target = clipStack.current() == null ? rect : rect.intersect(clipStack.current().rect());
        if (target.width() <= 0 || target.height() <= 0) {
            return;
        }
        int clampedRadius = clampRadius(rect, radius);
        if (clampedRadius == 0 && (clipStack.current() == null || clipStack.current().radius() == 0)) {
            graphics.fill(target.x(), target.y(), target.right(), target.bottom(), color);
            return;
        }
        fillRoundedRows(rect, color, clampedRadius, target);
    }

    @Override
    public void drawBorder(Rect rect, int color, int thickness, int radius) {
        if (thickness <= 0 || rect.width() == 0 || rect.height() == 0) {
            return;
        }
        Rect target = clipStack.current() == null ? rect : rect.intersect(clipStack.current().rect());
        if (target.width() <= 0 || target.height() <= 0) {
            return;
        }
        int clampedThickness = Math.min(thickness, Math.min(rect.width(), rect.height()));
        Rect inner = rect.inset(Insets.all(clampedThickness));
        int outerRadius = clampRadius(rect, radius);
        int innerRadius = clampRadius(inner, Math.max(0, outerRadius - clampedThickness));
        for (int y = target.y(); y < target.bottom(); y++) {
            RowRange row = intersect(rowRange(rect, outerRadius, y), new RowRange(target.x(), target.right()));
            if (clipStack.current() != null) {
                row = intersect(row, rowRange(clipStack.current().rect(), clipStack.current().radius(), y));
            }
            if (row == null) {
                continue;
            }
            RowRange innerRow = inner.width() <= 0 || inner.height() <= 0 ? null : rowRange(inner, innerRadius, y);
            innerRow = intersect(row, innerRow);
            if (innerRow == null) {
                fillRow(row, y, color);
                continue;
            }
            fillRow(new RowRange(row.left(), innerRow.left()), y, color);
            fillRow(new RowRange(innerRow.right(), row.right()), y, color);
        }
    }

    @Override
    public void drawText(int x, int y, SlateText text, int color) {
        if (clipStack.current() != null && clipStack.shouldSkip(new DrawTextCommand(x, y, text, color))) {
            return;
        }
        graphics.drawString(font, toComponent(text), x, y, color, false);
    }

    @Override
    public void drawTexture(Rect rect, String texture, int u, int v, int textureWidth, int textureHeight, int regionWidth, int regionHeight) {
        Rect target = clipStack.current() == null ? rect : rect.intersect(clipStack.current().rect());
        ResourceLocation resource = ResourceLocation.tryParse(texture);
        if (target.width() > 0 && target.height() > 0 && resource != null && !"minecraft:missingno".equals(texture)) {
            graphics.blit(resource, rect.x(), rect.y(), u, v, regionWidth, regionHeight, textureWidth, textureHeight);
            return;
        }
        fill(rect, 0xFF7F1D1D, 0);
        drawBorder(rect, 0xFFFCA5A5, 1, 0);
        drawText(rect.x() + 4, rect.y() + 4, new SlateText.Literal(texture), 0xFFFFFFFF);
    }

    @Override
    public void pushClip(Rect rect, int radius) {
        ClipStack.Entry entry = clipStack.push(rect, radius);
        if (entry.enabled()) {
            Rect nextClip = entry.rect();
            graphics.enableScissor(nextClip.x(), nextClip.y(), nextClip.right(), nextClip.bottom());
        }
    }

    @Override
    public void popClip() {
        if (clipStack.popEnabled()) {
            graphics.disableScissor();
        }
    }

    public void close() {
        while (!clipStack.isEmpty()) {
            popClip();
        }
    }

    private void fillRoundedRows(Rect rect, int color, int radius, Rect target) {
        for (int y = target.y(); y < target.bottom(); y++) {
            RowRange row = intersect(rowRange(rect, radius, y), new RowRange(target.x(), target.right()));
            if (clipStack.current() != null) {
                row = intersect(row, rowRange(clipStack.current().rect(), clipStack.current().radius(), y));
            }
            if (row != null) {
                fillRow(row, y, color);
            }
        }
    }

    private static Component toComponent(SlateText text) {
        return switch (text) {
            case SlateText.Literal literal -> Component.literal(literal.fallbackText());
            case SlateText.Translatable translatable -> Component.translatable(translatable.key(), translatable.args().toArray());
        };
    }

    private static RowRange rowRange(Rect rect, int radius, int y) {
        if (y < rect.y() || y >= rect.bottom() || rect.width() <= 0) {
            return null;
        }
        int inset = roundedInsetForY(rect, radius, y);
        return new RowRange(rect.x() + inset, rect.right() - inset);
    }

    private static int roundedInsetForY(Rect rect, int radius, int y) {
        int clampedRadius = clampRadius(rect, radius);
        if (clampedRadius <= 0) {
            return 0;
        }
        int localY = y - rect.y();
        double distanceFromCornerCenter;
        if (localY < clampedRadius) {
            distanceFromCornerCenter = clampedRadius - localY - 0.5D;
        } else if (localY >= rect.height() - clampedRadius) {
            distanceFromCornerCenter = localY - (rect.height() - clampedRadius) + 0.5D;
        } else {
            return 0;
        }
        double horizontal = Math.sqrt(Math.max(0D, clampedRadius * clampedRadius - distanceFromCornerCenter * distanceFromCornerCenter));
        return Math.max(0, (int) Math.ceil(clampedRadius - horizontal));
    }

    private static int clampRadius(Rect rect, int radius) {
        if (radius <= 0 || rect.width() <= 0 || rect.height() <= 0) {
            return 0;
        }
        return Math.min(radius, Math.min(rect.width(), rect.height()) / 2);
    }

    private static RowRange intersect(RowRange first, RowRange second) {
        if (first == null || second == null) {
            return null;
        }
        int left = Math.max(first.left(), second.left());
        int right = Math.min(first.right(), second.right());
        return right > left ? new RowRange(left, right) : null;
    }

    private void fillRow(RowRange row, int y, int color) {
        if (row != null && row.right() > row.left()) {
            graphics.fill(row.left(), y, row.right(), y + 1, color);
        }
    }

    private record RowRange(int left, int right) {
    }
}
