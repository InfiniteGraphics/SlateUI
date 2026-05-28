package top.huliawsl.slateui.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.Theme;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommandDispatcher;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.MinecraftSlateRenderer;
import top.huliawsl.slateui.runtime.MinecraftTextMeasurer;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public final class WorldSpaceSlateSurface {

    private final SlateComponent root;
    private final WorldSpaceAnchor anchor;
    private final WorldSpaceProjection projection;
    private final Theme theme;
    private final boolean debugEnabled;
    private Rect projectedBounds = Rect.ZERO;
    private List<DrawCommand> drawCommands = List.of();

    public WorldSpaceSlateSurface(SlateComponent root, WorldSpaceAnchor anchor, WorldSpaceProjection projection, Theme theme, boolean debugEnabled) {
        this.root = Objects.requireNonNull(root, "root");
        this.anchor = Objects.requireNonNull(anchor, "anchor");
        this.projection = projection == null ? WorldSpaceProjection.screenCenter() : projection;
        this.theme = theme == null ? Theme.DEFAULT : theme;
        this.debugEnabled = debugEnabled;
    }

    public Rect projectedBounds() { return projectedBounds; }
    public List<DrawCommand> drawCommands() { return drawCommands; }

    public void rebuild(Font font, int screenWidth, int screenHeight) {
        projectedBounds = projection.project(anchor, screenWidth, screenHeight);
        SlateLayoutContext layoutContext = new SlateLayoutContext(new MinecraftTextMeasurer(font), theme);
        root.measure(layoutContext, new Size(projectedBounds.width(), projectedBounds.height()));
        root.layout(layoutContext, projectedBounds);
        List<DrawCommand> commands = new ArrayList<>();
        root.collectDrawCommands(new SlateRenderContext(debugEnabled, theme), commands);
        drawCommands = List.copyOf(commands);
    }

    public void render(GuiGraphics guiGraphics, Font font, int screenWidth, int screenHeight) {
        rebuild(font, screenWidth, screenHeight);
        MinecraftSlateRenderer renderer = new MinecraftSlateRenderer(guiGraphics, font);
        try {
            DrawCommandDispatcher.render(drawCommands, renderer);
        } finally {
            renderer.close();
        }
    }
}
