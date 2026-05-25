package top.huliawsl.slateui.render;

import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import top.huliawsl.slateui.layout.Rect;

public final class MinecraftDrawCommandRenderer {

    private MinecraftDrawCommandRenderer() {
    }

    public static void render(GuiGraphics graphics, Font font, List<DrawCommand> commands) {
        for (DrawCommand command : commands) {
            switch (command) {
                case DrawRectCommand rectCommand -> fill(graphics, rectCommand.rect(), rectCommand.color());
                case DrawBorderCommand borderCommand -> drawBorder(graphics, borderCommand.rect(), borderCommand.color(), borderCommand.thickness());
                case DrawTextCommand textCommand -> graphics.drawString(font, textCommand.text(), textCommand.x(), textCommand.y(), textCommand.color(), false);
                case DrawDebugRectCommand debugRectCommand -> drawBorder(graphics, debugRectCommand.rect(), debugRectCommand.color(), 1);
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
}
