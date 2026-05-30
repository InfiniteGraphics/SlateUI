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
import top.huliawsl.slateui.render.DrawCommandDispatcher;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.MinecraftSlateRenderer;
import top.huliawsl.slateui.runtime.MinecraftTextMeasurer;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public final class SlateHudLayer {

    private final String id;
    private final SlateComponent root;
    private final Theme theme;
    private final boolean debugEnabled;
    private final SlateHudConfig config;
    private Rect bounds = Rect.ZERO;
    private List<DrawCommand> drawCommands = List.of();
    private boolean dirty = true;
    private SlateHudLifecycle lifecycle = SlateHudLifecycle.CREATED;
    private SlateHudDiagnostics diagnostics = SlateHudDiagnostics.empty();

    public SlateHudLayer(String id, SlateComponent root, Theme theme, boolean debugEnabled) {
        this(id, root, theme, debugEnabled, SlateHudConfig.defaultConfig());
    }

    public SlateHudLayer(String id, SlateComponent root, Theme theme, boolean debugEnabled, SlateHudConfig config) {
        this.id = Objects.requireNonNull(id, "id");
        this.root = Objects.requireNonNull(root, "root");
        this.theme = theme == null ? Theme.DEFAULT : theme;
        this.debugEnabled = debugEnabled;
        this.config = config == null ? SlateHudConfig.defaultConfig() : config;
    }

    public String id() { return id; }
    public Rect bounds() { return bounds; }
    public List<DrawCommand> drawCommands() { return drawCommands; }
    public SlateHudLifecycle lifecycle() { return lifecycle; }
    public SlateHudDiagnostics diagnostics() { return diagnostics; }
    public void markDirty() { dirty = true; }

    public void rebuild(Font font, int screenWidth, int screenHeight) {
        long start = System.nanoTime();
        if (!config.visible()) {
            drawCommands = List.of();
            lifecycle = SlateHudLifecycle.HIDDEN;
            dirty = false;
            return;
        }
        Rect safeBounds = new Rect(
            config.safeArea().left(),
            config.safeArea().top(),
            Math.max(0, screenWidth - config.safeArea().horizontal()),
            Math.max(0, screenHeight - config.safeArea().vertical())
        );
        SlateLayoutContext layoutContext = new SlateLayoutContext(new MinecraftTextMeasurer(font), theme);
        Size available = new Size(Math.max(0, Math.round(safeBounds.width() / config.scale())), Math.max(0, Math.round(safeBounds.height() / config.scale())));
        Size measured = root.measure(layoutContext, available);
        bounds = anchoredBounds(safeBounds, measured);
        root.layout(layoutContext, bounds);
        List<DrawCommand> commands = new ArrayList<>();
        root.collectDrawCommands(new SlateRenderContext(debugEnabled, theme), commands);
        drawCommands = List.copyOf(commands);
        long rebuildNanos = System.nanoTime() - start;
        diagnostics = new SlateHudDiagnostics(drawCommands.size(), rebuildNanos, drawCommands.size() > config.maxDrawCommands() || rebuildNanos > config.maxRebuildNanos());
        lifecycle = SlateHudLifecycle.MOUNTED;
        dirty = false;
    }

    public void render(GuiGraphics guiGraphics, Font font, int screenWidth, int screenHeight) {
        if (dirty || bounds.width() != screenWidth || bounds.height() != screenHeight) {
            rebuild(font, screenWidth, screenHeight);
        }
        MinecraftSlateRenderer renderer = new MinecraftSlateRenderer(guiGraphics, font);
        try {
            DrawCommandDispatcher.render(drawCommands, renderer);
        } finally {
            renderer.close();
        }
    }

    private Rect anchoredBounds(Rect safeBounds, Size measured) {
        int width = Math.max(0, Math.round(measured.width() * config.scale()));
        int height = Math.max(0, Math.round(measured.height() * config.scale()));
        int x = switch (config.anchor()) {
            case TOP_RIGHT, BOTTOM_RIGHT -> safeBounds.right() - width;
            case CENTER -> safeBounds.x() + (safeBounds.width() - width) / 2;
            default -> safeBounds.x();
        };
        int y = switch (config.anchor()) {
            case BOTTOM_LEFT, BOTTOM_RIGHT -> safeBounds.bottom() - height;
            case CENTER -> safeBounds.y() + (safeBounds.height() - height) / 2;
            default -> safeBounds.y();
        };
        return new Rect(Math.max(safeBounds.x(), x), Math.max(safeBounds.y(), y), width, height);
    }
}
