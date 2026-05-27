package top.huliawsl.slateui.hud;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.Theme;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.MinecraftDrawCommandRenderer;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public final class SlateHudLayer {

    private final String id;
    private final SlateComponent root;
    private final Theme theme;
    private final boolean debugEnabled;
    private Rect bounds = Rect.ZERO;
    private List<DrawCommand> drawCommands = List.of();
    private boolean dirty = true;

    public SlateHudLayer(String id, SlateComponent root, Theme theme, boolean debugEnabled) {
        this.id = Objects.requireNonNull(id, "id");
        this.root = Objects.requireNonNull(root, "root");
        this.theme = theme == null ? Theme.DEFAULT : theme;
        this.debugEnabled = debugEnabled;
    }

    public String id() { return id; }
    public Rect bounds() { return bounds; }
    public List<DrawCommand> drawCommands() { return drawCommands; }
    public void markDirty() { dirty = true; }

    public void rebuild(Font font, int screenWidth, int screenHeight) {
        bounds = new Rect(0, 0, Math.max(0, screenWidth), Math.max(0, screenHeight));
        SlateLayoutContext layoutContext = new SlateLayoutContext(font);
        root.measure(layoutContext, new Size(bounds.width(), bounds.height()));
        root.layout(layoutContext, bounds);
        List<DrawCommand> commands = new ArrayList<>();
        root.collectDrawCommands(new SlateRenderContext(debugEnabled, theme), commands);
        drawCommands = List.copyOf(commands);
        dirty = false;
    }

    public void render(GuiGraphics guiGraphics, Font font, int screenWidth, int screenHeight) {
        if (dirty || bounds.width() != screenWidth || bounds.height() != screenHeight) {
            rebuild(font, screenWidth, screenHeight);
        }
        MinecraftDrawCommandRenderer.render(guiGraphics, font, drawCommands);
    }
}
