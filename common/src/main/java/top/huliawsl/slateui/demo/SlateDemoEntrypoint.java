package top.huliawsl.slateui.demo;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import top.huliawsl.slateui.api.ComputedStateProvider;
import top.huliawsl.slateui.api.HorizontalAlign;
import top.huliawsl.slateui.api.InputValueHandler;
import top.huliawsl.slateui.api.ToggleValueHandler;
import top.huliawsl.slateui.api.SlateBorder;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateScreen;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;
import top.huliawsl.slateui.api.StateProvider;
import top.huliawsl.slateui.api.Theme;
import top.huliawsl.slateui.api.ThemeTokens;
import top.huliawsl.slateui.api.VerticalAlign;
import top.huliawsl.slateui.api.component.Box;
import top.huliawsl.slateui.api.component.Conditional;
import top.huliawsl.slateui.api.component.AbsoluteOverlay;
import top.huliawsl.slateui.api.component.GhostIngredient;
import top.huliawsl.slateui.api.component.Image;
import top.huliawsl.slateui.api.component.IngredientView;
import top.huliawsl.slateui.api.component.Input;
import top.huliawsl.slateui.api.component.Modal;
import top.huliawsl.slateui.api.component.OverlayRoot;
import top.huliawsl.slateui.api.component.Popup;
import top.huliawsl.slateui.api.component.RecipePreview;
import top.huliawsl.slateui.api.component.ScrollView;
import top.huliawsl.slateui.api.component.SlotGrid;
import top.huliawsl.slateui.api.component.Stack;
import top.huliawsl.slateui.api.component.StickyHeader;
import top.huliawsl.slateui.api.component.Text;
import top.huliawsl.slateui.api.component.Tooltip;
import top.huliawsl.slateui.api.component.Toggle;
import top.huliawsl.slateui.api.component.VirtualList;
import top.huliawsl.slateui.animation.SlateEasing;
import top.huliawsl.slateui.animation.SlateTween;
import top.huliawsl.slateui.authoring.SlateIrLoader;
import top.huliawsl.slateui.authoring.SlateIrRuntimeFactory;
import top.huliawsl.slateui.authoring.SlateReloadSupport;
import top.huliawsl.slateui.api.container.ContainerSlot;
import top.huliawsl.slateui.api.container.StaticContainerSlotProvider;
import top.huliawsl.slateui.command.SlateCommandRegistry;
import top.huliawsl.slateui.command.MinecraftCommandContext;
import top.huliawsl.slateui.layout.AbsolutePlacement;
import top.huliawsl.slateui.layout.FlexSpec;
import top.huliawsl.slateui.layout.Insets;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.ResponsivePreset;
import top.huliawsl.slateui.layout.ScrollSnap;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public final class SlateDemoEntrypoint {

    private static final Component DEMO_TITLE = Component.literal("SlateUI Gallery");
    private static final String AUTHORING_RESOURCE = "slateui/gallery.json";

    private SlateDemoEntrypoint() {
    }

    public static Button createTitleScreenButton(TitleScreen screen, int x, int y) {
        return Button.builder(Component.literal("SlateUI Gallery"), button -> Minecraft.getInstance().setScreen(createDefaultEntryScreen()))
            .bounds(x, y, 124, 20)
            .build();
    }

    public static SlateScreen createDefaultEntryScreen() {
        return createGalleryScreen(false);
    }

    public static SlateScreen createGalleryScreen(boolean debugEnabled) {
        return createGalleryScreen(debugEnabled, "Ready", null);
    }

    static SlateScreen createGalleryScreen(boolean debugEnabled, String status, String notice) {
        ComputedStateProvider provider = new ComputedStateProvider()
            .set("gallery.page", "settings")
            .set("settings.playerName", "Slate Tester")
            .set("settings.status", status)
            .set("settings.enabled", true)
            .set("settings.mode", debugEnabled ? "Debug" : "Normal")
            .set("form.email", "alex@example.com")
            .set("form.query", "diamond pickaxe")
            .set("theme.name", debugEnabled ? "Debug Theme" : "Default Theme")
            .set("ui.popupOpen", false)
            .set("ui.modalOpen", false)
            .set("ui.runtimeHits", 0)
            .registerComputed("settings.summary", List.of("settings.playerName", "settings.status"), state ->
                state.get("settings.playerName") + " / " + state.get("settings.status"))
            .registerComputed("form.summary", List.of("form.email", "form.query"), state ->
                state.get("form.email") + " searches for " + state.get("form.query"));
        Theme theme = createTheme(debugEnabled);
        SlateCommandRegistry commands = createGalleryCommands(debugEnabled, provider, theme);

        InputValueHandler playerNameHandler = (context, value) -> provider.set("settings.playerName", value);
        InputValueHandler statusHandler = (context, value) -> provider.set("settings.status", value);
        InputValueHandler emailHandler = (context, value) -> provider.set("form.email", value);
        InputValueHandler queryHandler = (context, value) -> provider.set("form.query", value);
        ToggleValueHandler enabledHandler = (context, checked) -> provider.set("settings.enabled", checked);

        SlateStyle rootStyle = SlateStyle.builder()
            .padding(Insets.all(16))
            .backgroundToken("color.surface")
            .horizontalAlign(HorizontalAlign.CENTER)
            .verticalAlign(VerticalAlign.START)
            .build();
        SlateStyle columnStyle = SlateStyle.builder()
            .width(380)
            .gapToken("spacing.md")
            .build();
        SlateStyle panelStyle = SlateStyle.builder()
            .padding(Insets.all(12))
            .backgroundToken("color.panel")
            .border(new SlateBorder(0xFF334155, 1))
            .borderColorToken("color.border")
            .borderRadiusToken("radius.md")
            .focusBorder(new SlateBorder(0xFF60A5FA, 1))
            .focusBorderColorToken("color.primary")
            .gapToken("spacing.sm")
            .clipContent(true)
            .build();
        SlateStyle primaryButtonStyle = SlateStyle.builder()
            .padding(Insets.symmetric(10, 6))
            .backgroundToken("color.primary")
            .hoverBackgroundToken("color.primaryHover")
            .activeBackgroundToken("color.primaryActive")
            .border(new SlateBorder(0xFFBFDBFE, 1))
            .horizontalAlign(HorizontalAlign.CENTER)
            .focusBorder(new SlateBorder(0xFFFFFFFF, 1))
            .borderRadiusToken("radius.sm")
            .build();
        SlateStyle subtleButtonStyle = SlateStyle.builder()
            .padding(Insets.symmetric(8, 5))
            .backgroundColor(0xFF1E293B)
            .hoverBackgroundColor(0xFF334155)
            .border(new SlateBorder(0xFF64748B, 1))
            .horizontalAlign(HorizontalAlign.CENTER)
            .focusBorder(new SlateBorder(0xFF93C5FD, 1))
            .borderRadiusToken("radius.sm")
            .build();
        SlateStyle noticeStyle = SlateStyle.builder()
            .padding(Insets.symmetric(8, 6))
            .backgroundColor(0xFF1E293B)
            .border(new SlateBorder(0xFF64748B, 1))
            .borderRadiusToken("radius.sm")
            .textColor(0xFFCBD5E1)
            .build();
        SlateStyle fieldStyle = SlateStyle.builder()
            .padding(Insets.symmetric(8, 6))
            .backgroundColor(0xFF0F172A)
            .border(new SlateBorder(0xFF475569, 1))
            .focusBorder(new SlateBorder(0xFF60A5FA, 1))
            .borderRadiusToken("radius.sm")
            .width(330)
            .build();

        DemoPanel navigation = new DemoPanel(
            "Gallery",
            List.of(
                new Text(ignored -> "Current page: " + provider.get("gallery.page"), SlateStyle.EMPTY),
                new Stack(StackDirection.ROW, List.of(
                    new top.huliawsl.slateui.api.component.Button("Settings", "demo.page.settings", subtleButtonStyle),
                    new top.huliawsl.slateui.api.component.Button("Form", "demo.page.form", subtleButtonStyle)
                ), SlateStyle.builder().gap(8).clipContent(true).build()),
                new Stack(StackDirection.ROW, List.of(
                    new top.huliawsl.slateui.api.component.Button("List", "demo.page.list", subtleButtonStyle),
                    new top.huliawsl.slateui.api.component.Button("Container", "demo.page.container", subtleButtonStyle)
                ), SlateStyle.builder().gap(8).clipContent(true).build()),
                new Stack(StackDirection.ROW, List.of(
                    new top.huliawsl.slateui.api.component.Button("Runtime", "demo.page.runtime", subtleButtonStyle),
                    new top.huliawsl.slateui.api.component.Button("Layout", "demo.page.layout", subtleButtonStyle),
                    new top.huliawsl.slateui.api.component.Button("Ecosystem", "demo.page.ecosystem", subtleButtonStyle)
                ), SlateStyle.builder().gap(8).clipContent(true).build()),
                new Stack(StackDirection.ROW, List.of(
                    new top.huliawsl.slateui.api.component.Button("Inspect Runtime", "screen.inspect", primaryButtonStyle),
                    new top.huliawsl.slateui.api.component.Button(debugEnabled ? "Normal" : "Debug", debugEnabled ? "demo.normal" : "demo.debug", primaryButtonStyle)
                ), SlateStyle.builder().gap(8).clipContent(true).build())
            ),
            panelStyle,
            SlateStyle.builder().gap(8).build()
        );

        List<SlateComponent> settingsChildren = new ArrayList<>();
        settingsChildren.add(new Text(ignored -> "Player: " + provider.get("settings.playerName"), SlateStyle.EMPTY));
        settingsChildren.add(new Text(ignored -> "Status: " + provider.get("settings.status"), SlateStyle.EMPTY));
        settingsChildren.add(new Text(ignored -> "Summary: " + provider.get("settings.summary"), SlateStyle.EMPTY));
        settingsChildren.add(new Text(ignored -> "Enabled: " + provider.get("settings.enabled"), SlateStyle.EMPTY));
        if (notice != null && !notice.isBlank()) {
            settingsChildren.add(new Text(notice, noticeStyle));
        }
        settingsChildren.add(new Input("Enter player name", ignored -> String.valueOf(provider.get("settings.playerName")), null, playerNameHandler, fieldStyle));
        settingsChildren.add(new Input("Update status", ignored -> String.valueOf(provider.get("settings.status")), null, statusHandler, fieldStyle));
        settingsChildren.add(new Toggle(provider, "Enable feature", ignored -> Boolean.TRUE.equals(provider.get("settings.enabled")), null, enabledHandler, subtleButtonStyle));
        settingsChildren.add(new Stack(StackDirection.COLUMN, List.of(
            new top.huliawsl.slateui.api.component.Button("Open .slate Screen", "demo.authoring", primaryButtonStyle),
            new top.huliawsl.slateui.api.component.Button("Open Error Page", "demo.error", primaryButtonStyle)
        ), SlateStyle.builder().gap(8).build()));
        DemoPanel settingsPage = new DemoPanel(
            "Settings Page",
            settingsChildren,
            panelStyle,
            SlateStyle.builder().gap(8).build()
        );

        DemoPanel formPage = new DemoPanel(
            "Form Page",
            List.of(
                new Text("Two inputs bound into computed state."),
                new Input("Email", ignored -> String.valueOf(provider.get("form.email")), null, emailHandler, fieldStyle),
                new Input("Search query", ignored -> String.valueOf(provider.get("form.query")), null, queryHandler, fieldStyle),
                new Text(ignored -> "Result: " + provider.get("form.summary"), noticeStyle),
                new top.huliawsl.slateui.api.component.Button("Submit Form", "demo.form.submit", primaryButtonStyle)
            ),
            panelStyle,
            SlateStyle.builder().gap(8).build()
        );

        List<SlateComponent> scrollItems = new ArrayList<>();
        for (int index = 1; index <= 24; index++) {
            scrollItems.add(new Stack(StackDirection.COLUMN, List.of(
                new Text("List row #" + index),
                new Text("Nested row content with fixed width and clipped border chrome.", SlateStyle.builder().textColor(0xFFCBD5E1).build())
            ), SlateStyle.builder()
                .padding(Insets.symmetric(8, 6))
                .gap(4)
                .backgroundColor(index % 2 == 0 ? 0xFF111827 : 0xFF0F172A)
                .border(new SlateBorder(0xFF334155, 1))
                .borderRadiusToken("radius.sm")
                .clipContent(true)
                .build()));
        }
        List<SlateComponent> virtualItems = new ArrayList<>();
        for (int index = 1; index <= 200; index++) {
            virtualItems.add(new Stack(StackDirection.COLUMN, List.of(
                new Text("Virtual row #" + index),
                new Text("Only visible rows are measured and drawn.", SlateStyle.builder().textColor(0xFFCBD5E1).build())
            ), SlateStyle.builder()
                .padding(Insets.symmetric(8, 5))
                .gap(2)
                .backgroundColor(index % 2 == 0 ? 0xFF111827 : 0xFF0F172A)
                .border(new SlateBorder(0xFF334155, 1))
                .borderRadiusToken("radius.sm")
                .clipContent(true)
                .build()));
        }
        DemoPanel listPage = new DemoPanel(
            "List Page",
            List.of(
                new Text("VirtualList: 200 rows, wheel scroll should stay responsive.", noticeStyle),
                new VirtualList(
                    virtualItems,
                    30,
                    4,
                    SlateStyle.builder().height(150).width(340).padding(Insets.all(8)).build()
                ),
                new Text("SlateList baseline: regular scroll container.", noticeStyle),
                new top.huliawsl.slateui.api.component.SlateList(
                    scrollItems,
                    SlateStyle.builder().height(120).padding(Insets.all(8)).backgroundColor(0xFF020617).border(new SlateBorder(0xFF334155, 1)).borderRadiusToken("radius.md").clipContent(true).build()
                )
            ),
            panelStyle,
            SlateStyle.builder().gap(8).build()
        );

        SlotGrid slotGrid = new SlotGrid(new StaticContainerSlotProvider(List.of(
            new ContainerSlot(0, "minecraft:stone", 64, true),
            new ContainerSlot(1, "minecraft:oak_log", 32, true),
            new ContainerSlot(2, "minecraft:iron_ingot", 12, true),
            ContainerSlot.empty(3),
            new ContainerSlot(4, "minecraft:diamond", 3, true),
            new ContainerSlot(5, "minecraft:apple", 8, true),
            ContainerSlot.empty(6),
            new ContainerSlot(7, "minecraft:torch", 48, true),
            new ContainerSlot(8, "minecraft:book", 1, false)
        )), 3, 22, 4, "demo.slot.click", SlateStyle.builder().width(110).clipContent(true).build());
        DemoPanel containerPage = new DemoPanel(
            "Container Page",
            List.of(
                new Text("Fixed slot grid for inventory/container layouts."),
                slotGrid,
                new Text("Disabled slots ignore commands; enabled slots log component path and slot index.", noticeStyle),
                new Popup(
                    new Tooltip(
                        new top.huliawsl.slateui.api.component.Button("Toggle Popup", "demo.popup.toggle", primaryButtonStyle),
                        new Box(List.of(new Text("Tooltip uses hover state.")), panelStyle),
                        SlateStyle.EMPTY
                    ),
                    new Box(List.of(new Text("Popup content uses rounded chrome.")), panelStyle),
                    () -> provider.get("ui.popupOpen"),
                    SlateStyle.EMPTY
                ),
                new Modal(
                    new top.huliawsl.slateui.api.component.Button("Open Modal", "demo.modal.open", primaryButtonStyle),
                    new Box(List.of(
                        new Text("Modal overlay"),
                        new top.huliawsl.slateui.api.component.Button("Close Modal", "demo.modal.close", primaryButtonStyle)
                    ), panelStyle),
                    () -> provider.get("ui.modalOpen"),
                    SlateStyle.EMPTY
                )
            ),
            panelStyle,
            SlateStyle.builder().gap(8).build()
        );

        SlateStyle disabledPanelStyle = SlateStyle.builder()
            .padding(Insets.all(8))
            .backgroundColor(0xFF111827)
            .border(new SlateBorder(0xFF475569, 1))
            .borderRadiusToken("radius.sm")
            .disabled(true)
            .clipContent(true)
            .build();
        DemoPanel runtimePage = new DemoPanel(
            "Runtime Event Model",
            List.of(
                new Text("Tab / Shift+Tab traverses focusable controls and skips the disabled subtree.", noticeStyle),
                new Input("Focusable input", ignored -> String.valueOf(provider.get("settings.status")), null, statusHandler, fieldStyle),
                new Box(List.of(
                    new Text("Disabled parent: child button should not focus or click."),
                    new top.huliawsl.slateui.api.component.Button("Disabled Child", "demo.disabled.hit", subtleButtonStyle)
                ), disabledPanelStyle),
                new top.huliawsl.slateui.api.component.Button("Next Focusable Button", "demo.runtime.focus", primaryButtonStyle),
                new Text(ignored -> "Clip hit count: " + provider.get("ui.runtimeHits"), noticeStyle),
                new ClipHitDemo(
                    new top.huliawsl.slateui.api.component.Button("Half clipped button", "demo.clip.hit", primaryButtonStyle),
                    SlateStyle.builder()
                        .width(340)
                        .height(76)
                        .padding(new Insets(0, 24, 0, 0))
                        .backgroundColor(0xFF020617)
                        .border(new SlateBorder(0xFF334155, 1))
                        .borderRadiusToken("radius.md")
                        .clipContent(true)
                        .build()
                ),
                new Text("The top half is outside the clipped content area, so it should not receive clicks.", SlateStyle.builder().textColor(0xFFCBD5E1).build())
            ),
            panelStyle,
            SlateStyle.builder().gap(8).build()
        );

        SlateTween tween = new SlateTween(0F, 100F, 1000L, SlateEasing.EASE_OUT);
        FlexSpec flexSpec = new FlexSpec(1.5F, 0.5F, 120);
        DemoPanel layoutPage = new DemoPanel(
            "Layout + Animation Models",
            List.of(
                new Text("Recent non-resource runtime models are shown with live component composition.", noticeStyle),
                new AbsoluteOverlay(List.of(
                    new AbsoluteOverlay.Child(
                        new Box(List.of(new Text("AbsoluteOverlay base panel")), SlateStyle.builder()
                            .padding(Insets.all(8))
                            .backgroundColor(0xFF0F172A)
                            .border(new SlateBorder(0xFF334155, 1))
                            .borderRadiusToken("radius.md")
                            .build()),
                        new AbsolutePlacement(0, 0, 300, 56)
                    ),
                    new AbsoluteOverlay.Child(
                        new Box(List.of(new Text("badge")), SlateStyle.builder()
                            .padding(Insets.symmetric(6, 3))
                            .backgroundColor(0xFF2563EB)
                            .borderRadiusToken("radius.sm")
                            .build()),
                        new AbsolutePlacement(216, 10, 72, 24)
                    )
                ), SlateStyle.builder().width(320).height(64).build()),
                new StickyHeader(
                    new Text("StickyHeader wrapper"),
                    new Stack(StackDirection.COLUMN, List.of(
                        new Text("Header and body stay grouped as a formal component."),
                        new Text("ResponsivePreset(420): " + ResponsivePreset.fromWidth(420)),
                        new Text("FlexSpec: grow=" + flexSpec.grow() + ", shrink=" + flexSpec.shrink() + ", basis=" + flexSpec.basis()),
                        new Text("ScrollSnap(20).snap(37): " + new ScrollSnap(20).snap(37)),
                        new Text("SlateTween ease-out at 250ms: " + Math.round(tween.valueAt(250)))
                    ), SlateStyle.builder().gap(4).build()),
                    SlateStyle.builder()
                        .padding(Insets.all(8))
                        .gap(6)
                        .backgroundColor(0xFF111827)
                        .border(new SlateBorder(0xFF334155, 1))
                        .borderRadiusToken("radius.md")
                        .build()
                )
            ),
            panelStyle,
            SlateStyle.builder().gap(8).build()
        );

        DemoPanel ecosystemPage = new DemoPanel(
            "Recipe + Ecosystem Components",
            List.of(
                new Text("These components intentionally use item IDs only; no project texture resources are assumed.", noticeStyle),
                new RecipePreview(
                    List.of("minecraft:stick", "minecraft:coal"),
                    "minecraft:torch",
                    SlateStyle.builder()
                        .padding(Insets.all(8))
                        .gap(6)
                        .backgroundColor(0xFF0F172A)
                        .border(new SlateBorder(0xFF334155, 1))
                        .borderRadiusToken("radius.sm")
                        .build()
                ),
                new IngredientView("minecraft:iron_ingot", 12, SlateStyle.builder().gap(6).build()),
                new Stack(StackDirection.ROW, List.of(
                    new GhostIngredient("minecraft:diamond", SlateStyle.EMPTY),
                    new Text("GhostIngredient preview for recipe overlays.")
                ), SlateStyle.builder().gap(8).build()),
                new Text("Platform integration, scripting policy, API stability and release checks are contract-level features; use docs/tests for full validation.", SlateStyle.builder().textColor(0xFFCBD5E1).build())
            ),
            panelStyle,
            SlateStyle.builder().gap(8).build()
        );

        Stack pages = new Stack(StackDirection.COLUMN, List.of(
            new Conditional(() -> "settings".equals(String.valueOf(provider.get("gallery.page"))), settingsPage),
            new Conditional(() -> "form".equals(String.valueOf(provider.get("gallery.page"))), formPage),
            new Conditional(() -> "list".equals(String.valueOf(provider.get("gallery.page"))), listPage),
            new Conditional(() -> "container".equals(String.valueOf(provider.get("gallery.page"))), containerPage),
            new Conditional(() -> "runtime".equals(String.valueOf(provider.get("gallery.page"))), runtimePage),
            new Conditional(() -> "layout".equals(String.valueOf(provider.get("gallery.page"))), layoutPage),
            new Conditional(() -> "ecosystem".equals(String.valueOf(provider.get("gallery.page"))), ecosystemPage)
        ), SlateStyle.builder().gap(8).build());

        SlateComponent content = new ScrollView(
            new Stack(StackDirection.COLUMN, List.of(navigation, pages), columnStyle),
            SlateStyle.builder().height(250).width(420).padding(Insets.all(4)).clipContent(true).build()
        );
        OverlayRoot root = new OverlayRoot(List.of(
            new Box(List.of(content), rootStyle)
        ), rootStyle);
        return new SlateScreen(DEMO_TITLE, root, commands, provider, theme, debugEnabled);
    }

    public static SlateScreen createAuthoringScreen(boolean debugEnabled) {
        if (!SlateIrLoader.resourceExists(AUTHORING_RESOURCE)) {
            return createGalleryScreen(debugEnabled, "Ready", "Authoring screen unavailable. Run compileSlate first.");
        }
        ComputedStateProvider provider = new ComputedStateProvider()
            .set("settings.playerName", "Slate Author")
            .set("settings.status", debugEnabled ? "Debug authoring" : "Authoring ready")
            .set("settings.enabled", true)
            .registerComputed("settings.summary", List.of("settings.playerName", "settings.status"), state ->
                state.get("settings.playerName") + " / " + state.get("settings.status"));
        Theme theme = createTheme(debugEnabled);
        return new SlateIrRuntimeFactory().createScreen(DEMO_TITLE, AUTHORING_RESOURCE, createAuthoringCommands(debugEnabled, provider, theme), provider, theme, debugEnabled);
    }

    private static SlateCommandRegistry createGalleryCommands(boolean debugEnabled, ComputedStateProvider provider, Theme theme) {
        SlateCommandRegistry commands = new SlateCommandRegistry();
        commands.register("demo.error", context -> MinecraftCommandContext.require(context).minecraft().setScreen(createFaultyScreen(debugEnabled)));
        commands.register("demo.debug", context -> MinecraftCommandContext.require(context).minecraft().setScreen(createGalleryScreen(true)));
        commands.register("demo.normal", context -> MinecraftCommandContext.require(context).minecraft().setScreen(createGalleryScreen(false)));
        commands.register("demo.authoring", context -> MinecraftCommandContext.require(context).minecraft().setScreen(createAuthoringScreen(debugEnabled)));
        commands.register("demo.page.settings", context -> provider.set("gallery.page", "settings"));
        commands.register("demo.page.form", context -> provider.set("gallery.page", "form"));
        commands.register("demo.page.list", context -> provider.set("gallery.page", "list"));
        commands.register("demo.page.container", context -> provider.set("gallery.page", "container"));
        commands.register("demo.page.runtime", context -> provider.set("gallery.page", "runtime"));
        commands.register("demo.page.layout", context -> provider.set("gallery.page", "layout"));
        commands.register("demo.page.ecosystem", context -> provider.set("gallery.page", "ecosystem"));
        commands.register("demo.form.submit", context -> provider.set("settings.status", "Form submitted"));
        commands.register("demo.runtime.focus", context -> provider.set("settings.status", "Runtime focus button activated"));
        commands.register("demo.disabled.hit", context -> provider.set("settings.status", "Disabled child should not run"));
        commands.register("demo.clip.hit", context -> provider.set("ui.runtimeHits", ((Number) provider.get("ui.runtimeHits")).intValue() + 1));
        commands.register("demo.slot.click", context -> provider.set("settings.status", "Slot clicked: #" + context.payloadInt("slotIndex", -1) + " " + context.payloadString("itemId", "")));
        commands.register("demo.popup.toggle", context -> provider.set("ui.popupOpen", !(Boolean) provider.get("ui.popupOpen")));
        commands.register("demo.modal.open", context -> provider.set("ui.modalOpen", true));
        commands.register("demo.modal.close", context -> provider.set("ui.modalOpen", false));
        commands.register("slate.reload", context -> SlateReloadSupport.reload(AUTHORING_RESOURCE, DEMO_TITLE, createAuthoringCommands(debugEnabled, provider, theme), provider, theme, debugEnabled));
        return commands;
    }

    private static SlateCommandRegistry createAuthoringCommands(boolean debugEnabled, ComputedStateProvider provider, Theme theme) {
        SlateCommandRegistry commands = new SlateCommandRegistry();
        commands.register("demo.authoring", context -> MinecraftCommandContext.require(context).minecraft().setScreen(createAuthoringScreen(debugEnabled)));
        commands.register("demo.normal", context -> MinecraftCommandContext.require(context).minecraft().setScreen(createGalleryScreen(false)));
        commands.register("demo.debug", context -> MinecraftCommandContext.require(context).minecraft().setScreen(createGalleryScreen(true)));
        commands.register("authoring.name", context -> provider.notifyDirty("settings.playerName"));
        commands.register("slate.reload", context -> SlateReloadSupport.reload(AUTHORING_RESOURCE, DEMO_TITLE, createAuthoringCommands(debugEnabled, provider, theme), provider, theme, debugEnabled));
        return commands;
    }

    private static Theme createTheme(boolean debugEnabled) {
        if (!debugEnabled) {
            return Theme.DEFAULT;
        }
        return new Theme(ThemeTokens.builder()
            .color("color.surface", 0xFF1A102B)
            .color("color.panel", 0xFF24143A)
            .color("color.primary", 0xFFE11D48)
            .color("color.primaryHover", 0xFFFB7185)
            .color("color.primaryActive", 0xFFBE123C)
            .color("color.text", 0xFFFFFFFF)
            .color("color.muted", 0xFFFBCFE8)
            .color("color.border", 0xFF9F1239)
            .spacing("spacing.sm", 8)
            .spacing("spacing.md", 12)
            .build());
    }

    private static SlateScreen createFaultyScreen(boolean debugEnabled) {
        return new SlateScreen(Component.literal("SlateUI Error Demo"), new FaultyComponent(), new SlateCommandRegistry(), StateProvider.EMPTY, Theme.DEFAULT, debugEnabled);
    }

    private static final class ClipHitDemo extends SlateComponent {

        private final SlateComponent child;

        private ClipHitDemo(SlateComponent child, SlateStyle style) {
            super(style);
            this.child = child;
        }

        @Override
        public List<SlateComponent> children() {
            return List.of(child);
        }

        @Override
        public Size measure(SlateLayoutContext context, Size available) {
            measureChild(context, child, new Size(Math.max(0, available.width() - style().padding().horizontal()), 28));
            Size measured = applyStyleSize(new Size(available.width(), 76));
            setMeasuredSize(measured);
            return measured;
        }

        @Override
        public void layout(SlateLayoutContext context, Rect bounds) {
            setBounds(bounds);
            layoutChild(context, child, new Rect(bounds.x() + 12, bounds.y() + 6, Math.max(0, bounds.width() - 24), 28));
        }

        @Override
        public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
            emitBoxChrome(context, commands);
            Rect contentRect = contentRect(bounds());
            pushClip(context, commands, contentRect);
            collectChild(context, commands, child);
            popClip(commands);
        }
    }
}
