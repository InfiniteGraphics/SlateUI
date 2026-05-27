package top.huliawsl.slateui.debug;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

final class SlateDebugText {

    private static final int MARGIN = 16;
    private static final int LINE_HEIGHT = 10;

    private SlateDebugText() {
    }

    static int scrollStep(double scrollY) {
        return (int) Math.round(scrollY * -24.0D);
    }

    static int clampScroll(int scrollOffset, int lineCount, int height) {
        int contentHeight = lineCount * LINE_HEIGHT;
        int maxScroll = Math.max(0, contentHeight - Math.max(0, height - MARGIN * 2));
        return Math.max(0, Math.min(scrollOffset, maxScroll));
    }

    static List<String> wrap(List<String> lines, Font font, int screenWidth) {
        int maxWidth = Math.max(40, screenWidth - MARGIN * 2);
        List<String> wrapped = new ArrayList<>();
        for (String line : lines) {
            appendWrappedLine(wrapped, font, maxWidth, line == null ? "" : line);
        }
        return wrapped;
    }

    static void draw(GuiGraphics guiGraphics, Font font, List<String> lines, int scrollOffset, int screenHeight) {
        int y = MARGIN - scrollOffset;
        for (String line : lines) {
            if (y > -LINE_HEIGHT && y < screenHeight) {
                guiGraphics.drawString(font, line, MARGIN, y, 0xFFFFFFFF, false);
            }
            y += LINE_HEIGHT;
        }
    }

    private static void appendWrappedLine(List<String> target, Font font, int maxWidth, String line) {
        if (line.isEmpty() || font == null || font.width(line) <= maxWidth) {
            target.add(line);
            return;
        }
        String remaining = line;
        while (!remaining.isEmpty()) {
            int count = fittingPrefixLength(font, remaining, maxWidth);
            target.add(remaining.substring(0, count));
            remaining = remaining.substring(count);
        }
    }

    private static int fittingPrefixLength(Font font, String text, int maxWidth) {
        int low = 1;
        int high = text.length();
        int best = 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            if (font.width(text.substring(0, mid)) <= maxWidth) {
                best = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return best;
    }
}
