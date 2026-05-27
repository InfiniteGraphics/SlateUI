package top.huliawsl.slateui.render;

import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
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
                    case DrawRectCommand rectCommand -> fill(graphics, rectCommand.rect(), rectCommand.color());
                    case DrawBorderCommand borderCommand -> drawBorder(graphics, borderCommand.rect(), borderCommand.color(), borderCommand.thickness());
                    case DrawTextCommand textCommand -> graphics.drawString(font, textCommand.text(), textCommand.x(), textCommand.y(), textCommand.color(), false);
                    case DrawDebugRectCommand debugRectCommand -> drawBorder(graphics, debugRectCommand.rect(), debugRectCommand.color(), 1);
                    case DrawImageCommand imageCommand -> drawImagePlaceholder(graphics, font, imageCommand);
                    case PushClipCommand pushClipCommand -> pushClip(graphics, clipStack, pushClipCommand.rect());
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
        graphics.fill(rect.x(), rect.y(), rect.right(), rect.bottom(), color);
    }

    private static void drawBorder(GuiGraphics graphics, Rect rect, int color, int thickness) {
        if (thickness <= 0 || rect.width() == 0 || rect.height() == 0) {
            return;
        }
        int clampedThickness = Math.min(thickness, Math.min(rect.width(), rect.height()));
        graphics.fill(rect.x(), rect.y(), rect.right(), rect.y() + clampedThickness, color);
        graphics.fill(rect.x(), rect.bottom() - clampedThickness, rect.right(), rect.bottom(), color);
        graphics.fill(rect.x(), rect.y(), rect.x() + clampedThickness, rect.bottom(), color);
        graphics.fill(rect.right() - clampedThickness, rect.y(), rect.right(), rect.bottom(), color);
    }

    private static void drawImagePlaceholder(GuiGraphics graphics, Font font, DrawImageCommand command) {
        int background = command.missing() ? 0xFF7F1D1D : 0xFF0F172A;
        int border = command.missing() ? 0xFFFCA5A5 : 0xFF60A5FA;
        fill(graphics, command.rect(), background);
        drawBorder(graphics, command.rect(), border, 1);
        String label = command.resourceLocation().toString();
        graphics.drawString(font, label, command.rect().x() + 4, command.rect().y() + 4, 0xFFFFFFFF, false);
    }

    private static void pushClip(GuiGraphics graphics, ClipStack clipStack, Rect rect) {
        ClipStack.Entry entry = clipStack.push(rect);
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
}
