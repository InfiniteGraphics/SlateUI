package top.huliawsl.slateui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateContainerScreen;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.Theme;
import top.huliawsl.slateui.api.component.*;
import top.huliawsl.slateui.api.container.*;
import top.huliawsl.slateui.command.SlateCommandRegistry;
import top.huliawsl.slateui.hud.*;
import top.huliawsl.slateui.layout.Insets;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.DrawItemIconCommand;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;
import top.huliawsl.slateui.server.*;
import top.huliawsl.slateui.world.*;

class V06RoadmapRuntimeTest {

    @Test
    void v04ComponentsMeasureAndRender() {
        List<SlateComponent> components = List.of(
            new Slider(0, 100, 25, null, SlateStyle.EMPTY),
            new NumberInput("Count", 3, null, SlateStyle.EMPTY),
            new Dropdown(List.of("A", "B"), 1, null, SlateStyle.EMPTY),
            new RadioGroup(List.of("A", "B"), 0, null, SlateStyle.EMPTY),
            new CheckboxGroup(List.of("A", "B"), Set.of("B"), null, SlateStyle.EMPTY),
            new ColorPicker(0xFFEF4444, null, SlateStyle.EMPTY),
            new KeybindInput("key.jump", null, SlateStyle.EMPTY),
            new SearchBox("", null, SlateStyle.EMPTY),
            new TextArea("Notes", "Hello", null, SlateStyle.EMPTY),
            new ResourcePicker("minecraft:stone", null, SlateStyle.EMPTY),
            new Grid(2, List.of(new Text("A"), new Text("B")), SlateStyle.EMPTY),
            new SplitPane(new Text("L"), new Text("R"), false, SlateStyle.EMPTY),
            new Tabs(List.of(new Tabs.Tab("One", new Text("Body"))), 0, null, SlateStyle.EMPTY),
            new Accordion(List.of(new Accordion.Section("One", new Text("Body"), true)), null, SlateStyle.EMPTY),
            new TreeView(List.of(new TreeView.Node("Root", List.of(), true)), SlateStyle.EMPTY),
            new VirtualList(List.of(new Text("Row")), SlateStyle.EMPTY),
            new Spacer(4, 4),
            new Divider(false),
            new Card(List.of(new Text("Card"))),
            new Toolbar(List.of(new Button("Run", null, SlateStyle.EMPTY))),
            new Toast("Saved", "Config saved", 1000, SlateStyle.EMPTY),
            new ProgressBar(0.5, SlateStyle.EMPTY),
            new Spinner(),
            new Badge("Beta"),
            new Alert("Warning", "Message", SlateStyle.EMPTY),
            new ConfirmDialog("Confirm", "Continue?", null, null, SlateStyle.EMPTY),
            new ContextMenu(new Text("Anchor"), List.of(new ContextMenu.MenuItem("Copy", null)), true, SlateStyle.EMPTY),
            new CommandPalette("", List.of(new Button("Run", null, SlateStyle.EMPTY)), true, SlateStyle.EMPTY),
            new ItemIcon("minecraft:stone", 1, SlateStyle.EMPTY),
            new ItemStackView("minecraft:stone", 64, SlateStyle.EMPTY),
            new FluidView("minecraft:water", 500, 1000, SlateStyle.EMPTY),
            new EntityPreview("minecraft:pig", 0, 0, SlateStyle.EMPTY),
            new RecipePreview(List.of("minecraft:stone"), "minecraft:stone_button", SlateStyle.EMPTY),
            new AdvancementIcon("minecraft:diamond", SlateStyle.EMPTY),
            new KeybindLabel("key.jump", SlateStyle.EMPTY),
            new ResourceLocationInput("minecraft:stone", null, SlateStyle.EMPTY),
            new ModIcon("minecraft:textures/gui/widgets.png", SlateStyle.EMPTY),
            new PlayerHead("Alex", SlateStyle.EMPTY)
        );

        SlateLayoutContext layout = new SlateLayoutContext(null);
        for (SlateComponent component : components) {
            Size measured = component.measure(layout, new Size(240, 160));
            component.layout(layout, new Rect(0, 0, measured.width(), measured.height()));
            List<DrawCommand> commands = new java.util.ArrayList<>();
            component.collectDrawCommands(new SlateRenderContext(false, Theme.DEFAULT), commands);
            assertTrue(measured.width() >= 0);
            assertTrue(measured.height() >= 0);
        }
    }

    @Test
    void v05SlotIntentAndContainerPoliciesAreUsable() {
        SlotGrid grid = new SlotGrid(new StaticContainerSlotProvider(List.of(new ContainerSlot(0, "minecraft:stone", 64, true))), 1, null, SlateStyle.EMPTY)
            .slotMode(SlotMode.FILTER)
            .slotValidator(SlotValidator.allowAll());
        Size measured = grid.measure(new SlateLayoutContext(null), new Size(80, 80));
        grid.layout(new SlateLayoutContext(null), new Rect(0, 0, measured.width(), measured.height()));
        List<DrawCommand> commands = new java.util.ArrayList<>();
        grid.collectDrawCommands(new SlateRenderContext(false, Theme.DEFAULT), commands);

        SlateIntentPacket packet = SlateIntentPacket.fromIntent(SlateServerIntent.now("slot.click", "Machine", java.util.Map.of("slotIndex", 0), java.util.Map.of()), 1L);
        SlateIntentSecurityPolicy security = new SlateIntentSecurityPolicy();
        SlateContainerScreen screen = new SlateContainerScreen("Machine", grid, new SlateCommandRegistry(), null, Theme.DEFAULT, false, new SlateMenuBinding("machine", java.util.Map.of()), SlateContainerPolicy.serverAuthoritative());

        assertTrue(commands.stream().anyMatch(DrawItemIconCommand.class::isInstance));
        assertTrue(grid.tooltipTextAt(6, 6).contains("minecraft:stone"));
        assertTrue(grid.accessibilityDiagnostics().contains("mode=FILTER"));
        assertTrue(security.validate(packet).accepted());
        assertFalse(security.validate(packet).accepted());
        assertTrue(screen.containerPolicy().vanillaSlotInterop());
    }

    @Test
    void v06HudAndWorldPoliciesProjectSurfaces() {
        SlateHudLayer hud = new SlateHudLayer(
            "status",
            new FixedComponent(20, 10),
            Theme.DEFAULT,
            false,
            new SlateHudConfig(SlateHudAnchor.BOTTOM_RIGHT, Insets.all(4), 1F, true, 32, 2_000_000L)
        );
        hud.rebuild(null, 100, 60);

        WorldSpaceSlateSurface surface = new WorldSpaceSlateSurface(
            new FixedComponent(20, 10),
            new WorldSpaceAnchor(1, 2, 3, 30, 20),
            WorldSpaceProjection.screenCenter(),
            Theme.DEFAULT,
            false,
            new WorldSpacePolicy(WorldSpaceBillboardMode.CAMERA_FACING, 1D, WorldSpaceOcclusionPolicy.HIDE_WHEN_OCCLUDED, true, WorldSpaceAttachment.entity("demo", new WorldSpaceCoordinate(0, 1, 0)), true, "server-authoritative", 32)
        );
        surface.rebuild(null, 100, 60);

        assertEquals(SlateHudLifecycle.MOUNTED, hud.lifecycle());
        assertEquals(new Rect(76, 46, 20, 10), hud.bounds());
        assertFalse(hud.diagnostics().overBudget());
        assertTrue(surface.hitTest(50, 30));
        assertFalse(surface.diagnostics().overBudget());
        assertEquals(WorldSpaceAttachment.Type.ENTITY, surface.policy().attachment().type());
    }

    private static final class FixedComponent extends SlateComponent {

        private final Size size;

        private FixedComponent(int width, int height) {
            this.size = new Size(width, height);
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
