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
    private final WorldSpacePolicy policy;
    private Rect projectedBounds = Rect.ZERO;
    private List<DrawCommand> drawCommands = List.of();
    private WorldSpaceDiagnostics diagnostics = WorldSpaceDiagnostics.empty();

    public WorldSpaceSlateSurface(SlateComponent root, WorldSpaceAnchor anchor, WorldSpaceProjection projection, Theme theme, boolean debugEnabled) {
        this(root, anchor, projection, theme, debugEnabled, WorldSpacePolicy.defaultPolicy());
    }

    public WorldSpaceSlateSurface(SlateComponent root, WorldSpaceAnchor anchor, WorldSpaceProjection projection, Theme theme, boolean debugEnabled, WorldSpacePolicy policy) {
        this.root = Objects.requireNonNull(root, "root");
        this.anchor = Objects.requireNonNull(anchor, "anchor");
        this.projection = projection == null ? WorldSpaceProjection.screenCenter() : projection;
        this.theme = theme == null ? Theme.DEFAULT : theme;
        this.debugEnabled = debugEnabled;
        this.policy = policy == null ? WorldSpacePolicy.defaultPolicy() : policy;
    }

    public Rect projectedBounds() { return projectedBounds; }
    public List<DrawCommand> drawCommands() { return drawCommands; }
    public WorldSpacePolicy policy() { return policy; }
    public WorldSpaceDiagnostics diagnostics() { return diagnostics; }
    public boolean hitTest(double mouseX, double mouseY) { return policy.raycastInteraction() && projectedBounds.contains(mouseX, mouseY); }

    public void rebuild(Font font, int screenWidth, int screenHeight) {
        projectedBounds = projection.project(anchor, screenWidth, screenHeight);
        if (policy.frustumCulling() && (projectedBounds.right() <= 0 || projectedBounds.bottom() <= 0 || projectedBounds.x() >= screenWidth || projectedBounds.y() >= screenHeight)) {
            drawCommands = List.of();
            diagnostics = new WorldSpaceDiagnostics(0, true, false);
            return;
        }
        int scaledWidth = Math.max(1, (int) Math.round(projectedBounds.width() * policy.distanceScale()));
        int scaledHeight = Math.max(1, (int) Math.round(projectedBounds.height() * policy.distanceScale()));
        projectedBounds = new Rect(projectedBounds.x(), projectedBounds.y(), scaledWidth, scaledHeight);
        SlateLayoutContext layoutContext = new SlateLayoutContext(new MinecraftTextMeasurer(font), theme);
        root.measure(layoutContext, new Size(projectedBounds.width(), projectedBounds.height()));
        root.layout(layoutContext, projectedBounds);
        List<DrawCommand> commands = new ArrayList<>();
        root.collectDrawCommands(new SlateRenderContext(debugEnabled, theme), commands);
        drawCommands = List.copyOf(commands);
        diagnostics = new WorldSpaceDiagnostics(drawCommands.size(), false, drawCommands.size() > policy.maxDrawCommands());
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
