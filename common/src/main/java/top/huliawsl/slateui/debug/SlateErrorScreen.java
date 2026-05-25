package top.huliawsl.slateui.debug;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class SlateErrorScreen extends Screen {

    private final String stage;
    private final Throwable throwable;
    private final SlateDiagnostics diagnostics;

    public SlateErrorScreen(String stage, Throwable throwable, SlateDiagnostics diagnostics) {
        super(Component.literal("SlateUI Error"));
        this.stage = stage;
        this.throwable = throwable;
        this.diagnostics = diagnostics;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        List<String> lines = new ArrayList<>();
        lines.add("SlateUI runtime error");
        lines.add("Stage: " + stage);
        lines.add("Message: " + throwable.getClass().getSimpleName() + " - " + safeMessage(throwable));
        lines.add("");
        lines.add("Component tree:");
        appendBlock(lines, diagnostics.componentTreeDump(), 6);
        lines.add("");
        lines.add("Layout dump:");
        appendBlock(lines, diagnostics.layoutDump(), 6);
        lines.add("");
        lines.add("Command log:");
        appendBlock(lines, diagnostics.commandLogDump(), 4);

        int y = 16;
        for (String line : lines) {
            guiGraphics.drawString(font, line, 16, y, 0xFFFFFFFF, false);
            y += 10;
            if (y > height - 10) {
                break;
            }
        }
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private static String safeMessage(Throwable throwable) {
        return throwable.getMessage() == null ? "<no message>" : throwable.getMessage();
    }

    private static void appendBlock(List<String> lines, String block, int maxLines) {
        if (block == null || block.isBlank()) {
            lines.add("  <empty>");
            return;
        }
        String[] split = block.split("\\R");
        for (int i = 0; i < split.length && i < maxLines; i++) {
            lines.add("  " + split[i]);
        }
        if (split.length > maxLines) {
            lines.add("  ...");
        }
    }
}
