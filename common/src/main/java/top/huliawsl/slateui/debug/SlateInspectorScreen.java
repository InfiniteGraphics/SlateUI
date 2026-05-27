package top.huliawsl.slateui.debug;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import top.huliawsl.slateui.api.SlateScreen;

public final class SlateInspectorScreen extends Screen {

    private static final int BACKGROUND_COLOR = 0xFF020617;

    private final SlateScreen source;
    private final SlateDiagnostics diagnostics;

    public SlateInspectorScreen(SlateScreen source, SlateDiagnostics diagnostics) {
        super(Component.literal("SlateUI Inspector"));
        this.source = source;
        this.diagnostics = diagnostics;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(source);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, width, height, BACKGROUND_COLOR);
        List<String> lines = new ArrayList<>();
        lines.add("SlateUI Inspector");
        lines.add("Press ESC to return");
        lines.add("");
        lines.add("Component tree:");
        appendBlock(lines, diagnostics.componentTreeDump(), 10);
        lines.add("");
        lines.add("Layout:");
        appendBlock(lines, diagnostics.layoutDump(), 8);
        lines.add("");
        lines.add("Draw commands:");
        appendBlock(lines, diagnostics.drawCommandDump(), 8);
        lines.add("");
        lines.add("Focus:");
        appendBlock(lines, diagnostics.focusDump(), 2);
        lines.add("");
        lines.add("Bindings:");
        appendBlock(lines, diagnostics.bindingDump(), 4);
        lines.add("");
        lines.add("State:");
        appendBlock(lines, diagnostics.stateDump(), 6);
        lines.add("");
        lines.add("Command log:");
        appendBlock(lines, diagnostics.commandLogDump(), 4);
        lines.add("");
        lines.add("Diagnostics:");
        appendBlock(lines, diagnostics.diagnosticsLogDump(), 6);

        int y = 16;
        for (String line : lines) {
            guiGraphics.drawString(font, line, 16, y, 0xFFFFFFFF, false);
            y += 10;
            if (y > height - 10) {
                break;
            }
        }
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
