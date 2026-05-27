package top.huliawsl.slateui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;
import top.huliawsl.slateui.api.MutableStateProvider;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateScreen;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StateProvider;
import top.huliawsl.slateui.api.Theme;
import top.huliawsl.slateui.api.ThemeTokens;
import top.huliawsl.slateui.api.component.Box;
import top.huliawsl.slateui.api.component.SlotGrid;
import top.huliawsl.slateui.api.container.ContainerSlot;
import top.huliawsl.slateui.api.container.StaticContainerSlotProvider;
import top.huliawsl.slateui.command.CommandContext;
import top.huliawsl.slateui.command.SlateCommandRegistry;
import top.huliawsl.slateui.debug.SlateDiagnostics;
import top.huliawsl.slateui.hud.SlateHudLayer;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.override.SlateOverrideRegistry;
import top.huliawsl.slateui.platform.LoaderId;
import top.huliawsl.slateui.platform.SlateCompatibilityMatrix;
import top.huliawsl.slateui.platform.SupportLevel;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;
import top.huliawsl.slateui.server.QueuedSlateServerIntentBridge;
import top.huliawsl.slateui.world.WorldSpaceAnchor;
import top.huliawsl.slateui.world.WorldSpaceProjection;
import top.huliawsl.slateui.world.WorldSpaceSlateSurface;

class Mvp4RuntimeTest {

    @Test
    void serverIntentCommandsCapturePayloadAndState() {
        QueuedSlateServerIntentBridge bridge = new QueuedSlateServerIntentBridge();
        SlateCommandRegistry commands = new SlateCommandRegistry()
            .registerServerIntent("server.slot.click")
            .withServerIntentBridge(bridge);
        MutableStateProvider provider = new MutableStateProvider().set("player.name", "Alex");
        SlotGrid grid = new SlotGrid(new StaticContainerSlotProvider(List.of(
            new ContainerSlot(0, "minecraft:stone", 64, true)
        )), 1, 18, 2, "server.slot.click", SlateStyle.EMPTY);
        grid.measure(new SlateLayoutContext(null), new Size(80, 80));
        grid.layout(new SlateLayoutContext(null), new Rect(0, 0, 80, 80));
        SlateScreen screen = new SlateScreen(Component.literal("Container"), grid, commands, provider, Theme.DEFAULT, false);
        SlateInteractionContext interaction = new SlateInteractionContext(
            commands,
            new CommandContext(null, screen),
            ignored -> {},
            ignored -> {},
            screen,
            provider,
            Theme.DEFAULT
        );

        assertTrue(grid.mouseClicked(interaction, 6, 6, 0));

        assertEquals(1, bridge.intents().size());
        assertEquals("server.slot.click", bridge.intents().get(0).id());
        assertEquals(0, bridge.intents().get(0).payload().get("slotIndex"));
        assertEquals("minecraft:stone", bridge.intents().get(0).payload().get("itemId"));
        assertEquals("Alex", bridge.intents().get(0).stateSnapshot().get("player.name"));
    }

    @Test
    void slotGridMeasuresRowsAndFindsSlots() {
        SlotGrid grid = new SlotGrid(new StaticContainerSlotProvider(List.of(
            new ContainerSlot(0, "minecraft:stone", 64, true),
            new ContainerSlot(1, "minecraft:dirt", 32, true),
            ContainerSlot.empty(2)
        )), 2, 18, 2, null, SlateStyle.EMPTY);

        Size measured = grid.measure(new SlateLayoutContext(null), new Size(100, 100));
        grid.layout(new SlateLayoutContext(null), new Rect(0, 0, measured.width(), measured.height()));

        assertEquals(new Size(46, 46), measured);
        assertEquals(1, grid.slotAt(25, 6).index());
        assertEquals(2, grid.slotAt(6, 25).index());
    }

    @Test
    void overrideRegistryReplacesComponentRootAndMergesThemeTokens() {
        JsonObject ir = new JsonObject();
        ir.addProperty("schemaVersion", 1);
        JsonObject root = new JsonObject();
        root.addProperty("componentType", "Box");
        ir.add("root", root);
        JsonObject replacement = new JsonObject();
        replacement.addProperty("componentType", "SlotGrid");

        SlateOverrideRegistry registry = new SlateOverrideRegistry()
            .registerComponentOverride("slateui/gallery.json", replacement)
            .registerThemeOverride(ThemeTokens.builder().color("color.primary", 0xFF000001).spacing("spacing.xl", 24).build());

        assertEquals("SlotGrid", registry.applyComponentOverride("slateui/gallery.json", ir).getAsJsonObject("root").get("componentType").getAsString());
        assertEquals(0xFF000001, registry.applyThemeOverride(Theme.DEFAULT).tokens().color("color.primary").intValue());
        assertEquals(24, registry.applyThemeOverride(Theme.DEFAULT).tokens().spacing("spacing.xl").intValue());
    }

    @Test
    void compatibilityMatrixTracksForge1201AsConsidered() {
        SlateCompatibilityMatrix matrix = SlateCompatibilityMatrix.mvp4();

        assertEquals(SupportLevel.SUPPORTED, matrix.resolve(LoaderId.FABRIC, "1.21.1").level());
        assertEquals(SupportLevel.CONSIDERED, matrix.resolve(LoaderId.FORGE, "1.20.1").level());
        assertEquals(SupportLevel.UNSUPPORTED, matrix.resolve(LoaderId.NEOFORGE, "1.20.1").level());
    }

    @Test
    void hudAndWorldSurfacesProduceDrawCommandsWithoutScreenLifecycle() {
        FixedComponent hudRoot = new FixedComponent("hud", 40, 12);
        SlateHudLayer hud = new SlateHudLayer("energy", hudRoot, Theme.DEFAULT, true);
        hud.rebuild(null, 120, 80);

        FixedComponent worldRoot = new FixedComponent("world", 30, 10);
        WorldSpaceSlateSurface surface = new WorldSpaceSlateSurface(
            worldRoot,
            new WorldSpaceAnchor(1, 2, 3, 60, 30),
            WorldSpaceProjection.screenCenter(),
            Theme.DEFAULT,
            true
        );
        surface.rebuild(null, 120, 80);

        assertFalse(hud.drawCommands().isEmpty());
        assertEquals(new Rect(30, 25, 60, 30), surface.projectedBounds());
        assertFalse(surface.drawCommands().isEmpty());
    }

    @Test
    void diagnosticsExposeRuntimeSummaryAndHitPath() {
        Box root = new Box(List.of(new FixedComponent("leaf", 20, 10)), SlateStyle.EMPTY);
        root.measure(new SlateLayoutContext(null), new Size(100, 100));
        root.layout(new SlateLayoutContext(null), new Rect(0, 0, 100, 100));
        List<DrawCommand> commands = new ArrayList<>();
        root.collectDrawCommands(new SlateRenderContext(false, Theme.DEFAULT), commands);
        SlateDiagnostics diagnostics = new SlateDiagnostics();

        diagnostics.capture(root, commands, "<none>", "<none>", "<empty>");
        diagnostics.capturePointer(1, 1);

        assertTrue(diagnostics.runtimeSummaryDump().contains("components=2"));
        assertTrue(diagnostics.hitTestDump().contains("Box > leaf"));
    }

    private static final class FixedComponent extends SlateComponent {

        private final String name;
        private final Size size;

        private FixedComponent(String name, int width, int height) {
            super(SlateStyle.builder().backgroundColor(0xFF334155).build());
            this.name = name;
            this.size = new Size(width, height);
        }

        @Override
        public String debugName() {
            return name;
        }

        @Override
        public Size measure(SlateLayoutContext context, Size available) {
            setMeasuredSize(size);
            return size;
        }

        @Override
        public void layout(SlateLayoutContext context, Rect bounds) {
            setBounds(new Rect(bounds.x(), bounds.y(), size.width(), size.height()));
        }

        @Override
        public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
            emitBoxChrome(context, commands);
        }
    }
}
