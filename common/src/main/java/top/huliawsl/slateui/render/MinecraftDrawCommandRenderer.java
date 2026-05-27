package top.huliawsl.slateui.render;

import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import top.huliawsl.slateui.layout.Insets;
import top.huliawsl.slateui.layout.Rect;

public final class MinecraftDrawCommandRenderer {

    private MinecraftDrawCommandRenderer() {
    }

    public static void render(GuiGraphics graphics, Font font, List<DrawCommand> commands) {
        ClipStack clipStack = new ClipStack();
        try {
            for (DrawCommand command : commands) {
                if (clipStack.shouldSkip(command)) {
                    continue;
                }
                switch (command) {
                    case DrawRectCommand rectCommand -> fill(graphics, rectCommand.rect(), rectCommand.color(), rectCommand.radius(), clipStack.current());
                    case DrawBorderCommand borderCommand -> drawBorder(graphics, borderCommand.rect(), borderCommand.color(), borderCommand.thickness(), borderCommand.radius(), clipStack.current());
                    case DrawTextCommand textCommand -> graphics.drawString(font, textCommand.text(), textCommand.x(), textCommand.y(), textCommand.color(), false);
                    case DrawDebugRectCommand debugRectCommand -> drawBorder(graphics, debugRectCommand.rect(), debugRectCommand.color(), 1, 0, clipStack.current());
                    case DrawImageCommand imageCommand -> drawImagePlaceholder(graphics, font, imageCommand, clipStack.current());
                    case PushClipCommand pushClipCommand -> pushClip(graphics, clipStack, pushClipCommand.rect(), pushClipCommand.radius());
                    case PopClipCommand ignored -> popClip(graphics, clipStack);
                }
            }
        } finally {
            while (!clipStack.isEmpty()) {
                popClip(graphics, clipStack);
            }
        }
    }

    private static void fill(GuiGraphics graphics, Rect rect, int color) {
        fill(graphics, rect, color, 0, null);
    }

    private static void fill(GuiGraphics graphics, Rect rect, int color, int radius, ClipStack.Entry clip) {
        Rect target = clip == null ? rect : rect.intersect(clip.rect());
        if (target.width() <= 0 || target.height() <= 0) {
            return;
        }
        int clampedRadius = clampRadius(rect, radius);
        if (clampedRadius == 0 && (clip == null || clip.radius() == 0)) {
            graphics.fill(target.x(), target.y(), target.right(), target.bottom(), color);
            return;
        }
        fillRoundedRows(graphics, rect, color, clampedRadius, clip, target);
    }

    private static void drawBorder(GuiGraphics graphics, Rect rect, int color, int thickness) {
        drawBorder(graphics, rect, color, thickness, 0, null);
    }

    private static void drawBorder(GuiGraphics graphics, Rect rect, int color, int thickness, int radius, ClipStack.Entry clip) {
        if (thickness <= 0 || rect.width() == 0 || rect.height() == 0) {
            return;
        }
        int clampedThickness = Math.min(thickness, Math.min(rect.width(), rect.height()));
        Rect target = clip == null ? rect : rect.intersect(clip.rect());
        if (target.width() <= 0 || target.height() <= 0) {
            return;
        }
        Rect inner = rect.inset(Insets.all(clampedThickness));
        int outerRadius = clampRadius(rect, radius);
        int innerRadius = clampRadius(inner, Math.max(0, outerRadius - clampedThickness));
        for (int y = target.y(); y < target.bottom(); y++) {
            RowRange outer = rowRange(rect, outerRadius, y);
            RowRange row = intersect(outer, new RowRange(target.x(), target.right()));
            if (clip != null) {
                row = intersect(row, rowRange(clip.rect(), clip.radius(), y));
            }
            if (row == null) {
                continue;
            }
            RowRange innerRow = inner.width() <= 0 || inner.height() <= 0 ? null : rowRange(inner, innerRadius, y);
            innerRow = intersect(row, innerRow);
            if (innerRow == null) {
                fillRow(graphics, row, y, color);
                continue;
            }
            fillRow(graphics, new RowRange(row.left(), innerRow.left()), y, color);
            fillRow(graphics, new RowRange(innerRow.right(), row.right()), y, color);
        }
    }

    private static void drawImagePlaceholder(GuiGraphics graphics, Font font, DrawImageCommand command, ClipStack.Entry clip) {
        int background = command.missing() ? 0xFF7F1D1D : 0xFF0F172A;
        int border = command.missing() ? 0xFFFCA5A5 : 0xFF60A5FA;
        fill(graphics, command.rect(), background, 0, clip);
        drawBorder(graphics, command.rect(), border, 1, 0, clip);
        String label = command.resourceLocation().toString();
        graphics.drawString(font, label, command.rect().x() + 4, command.rect().y() + 4, 0xFFFFFFFF, false);
    }

    private static void pushClip(GuiGraphics graphics, ClipStack clipStack, Rect rect, int radius) {
        ClipStack.Entry entry = clipStack.push(rect, radius);
        if (entry.enabled()) {
            Rect nextClip = entry.rect();
            graphics.enableScissor(nextClip.x(), nextClip.y(), nextClip.right(), nextClip.bottom());
        }
    }

    private static void popClip(GuiGraphics graphics, ClipStack clipStack) {
        if (clipStack.popEnabled()) {
            graphics.disableScissor();
        }
    }

    private static void fillRoundedRows(GuiGraphics graphics, Rect rect, int color, int radius, ClipStack.Entry clip, Rect target) {
        for (int y = target.y(); y < target.bottom(); y++) {
            RowRange shape = rowRange(rect, radius, y);
            RowRange row = intersect(shape, new RowRange(target.x(), target.right()));
            if (clip != null) {
                row = intersect(row, rowRange(clip.rect(), clip.radius(), y));
            }
            if (row != null) {
                fillRow(graphics, row, y, color);
            }
        }
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

    private static void fillRow(GuiGraphics graphics, RowRange row, int y, int color) {
        if (row != null && row.right() > row.left()) {
            graphics.fill(row.left(), y, row.right(), y + 1, color);
        }
    }

    private record RowRange(int left, int right) {
    }
}
