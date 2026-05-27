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
import top.huliawsl.slateui.api.component.Image;
import top.huliawsl.slateui.api.component.Input;
import top.huliawsl.slateui.api.component.Modal;
import top.huliawsl.slateui.api.component.OverlayRoot;
import top.huliawsl.slateui.api.component.Popup;
import top.huliawsl.slateui.api.component.ScrollView;
import top.huliawsl.slateui.api.component.Stack;
import top.huliawsl.slateui.api.component.Text;
import top.huliawsl.slateui.api.component.Tooltip;
import top.huliawsl.slateui.authoring.SlateIrLoader;
import top.huliawsl.slateui.authoring.SlateIrRuntimeFactory;
import top.huliawsl.slateui.authoring.SlateReloadSupport;
import top.huliawsl.slateui.command.SlateCommandRegistry;
import top.huliawsl.slateui.layout.Insets;

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
        return SlateIrLoader.resourceExists(AUTHORING_RESOURCE) ? createAuthoringScreen(false) : createGalleryScreen(false);
    }

    public static SlateScreen createGalleryScreen(boolean debugEnabled) {
        return createGalleryScreen(debugEnabled, "Ready");
    }

    private static SlateScreen createGalleryScreen(boolean debugEnabled, String status) {
        ComputedStateProvider provider = new ComputedStateProvider()
            .set("settings.playerName", "Slate Tester")
            .set("settings.status", status)
            .set("settings.mode", debugEnabled ? "Debug" : "Normal")
            .set("theme.name", debugEnabled ? "Debug Theme" : "Default Theme")
            .set("ui.popupOpen", false)
            .set("ui.modalOpen", false)
            .registerComputed("settings.summary", List.of("settings.playerName", "settings.status"), state ->
                state.get("settings.playerName") + " / " + state.get("settings.status"));
        Theme theme = createTheme(debugEnabled);
        SlateCommandRegistry commands = createGalleryCommands(debugEnabled, provider, theme);

        InputValueHandler playerNameHandler = (context, value) -> provider.set("settings.playerName", value);
        InputValueHandler statusHandler = (context, value) -> provider.set("settings.status", value);

        SlateStyle rootStyle = SlateStyle.builder()
            .padding(Insets.all(16))
            .backgroundToken("color.surface")
            .horizontalAlign(HorizontalAlign.CENTER)
            .verticalAlign(VerticalAlign.START)
            .build();
        SlateStyle columnStyle = SlateStyle.builder()
            .width(340)
            .gapToken("spacing.md")
            .build();
        SlateStyle panelStyle = SlateStyle.builder()
            .padding(Insets.all(12))
            .backgroundToken("color.panel")
            .border(new SlateBorder(0xFF334155, 1))
            .borderColorToken("color.border")
            .focusBorder(new SlateBorder(0xFF60A5FA, 1))
            .focusBorderColorToken("color.primary")
            .gapToken("spacing.sm")
            .build();
        SlateStyle primaryButtonStyle = SlateStyle.builder()
            .padding(Insets.symmetric(10, 6))
            .backgroundToken("color.primary")
            .hoverBackgroundToken("color.primaryHover")
            .activeBackgroundToken("color.primaryActive")
            .border(new SlateBorder(0xFFBFDBFE, 1))
            .horizontalAlign(HorizontalAlign.CENTER)
            .focusBorder(new SlateBorder(0xFFFFFFFF, 1))
            .build();
        SlateStyle fieldStyle = SlateStyle.builder()
            .padding(Insets.symmetric(8, 6))
            .backgroundColor(0xFF0F172A)
            .border(new SlateBorder(0xFF475569, 1))
            .focusBorder(new SlateBorder(0xFF60A5FA, 1))
            .width(300)
            .build();

        List<SlateComponent> sections = new ArrayList<>();
        sections.add(new DemoPanel(
            "Settings Page",
            List.of(
                new Text(ignored -> "Player: " + provider.get("settings.playerName"), SlateStyle.EMPTY),
                new Text(ignored -> "Status: " + provider.get("settings.status"), SlateStyle.EMPTY),
                new Text(ignored -> "Summary: " + provider.get("settings.summary"), SlateStyle.EMPTY),
                new Input("Enter player name", ignored -> String.valueOf(provider.get("settings.playerName")), null, playerNameHandler, fieldStyle),
                new Input("Update status", ignored -> String.valueOf(provider.get("settings.status")), null, statusHandler, fieldStyle),
                new Stack(StackDirection.ROW, List.of(
                    new top.huliawsl.slateui.api.component.Button("Open Error Page", "demo.error", primaryButtonStyle),
                    new top.huliawsl.slateui.api.component.Button(debugEnabled ? "Normal Mode" : "Debug Mode", debugEnabled ? "demo.normal" : "demo.debug", primaryButtonStyle),
                    new top.huliawsl.slateui.api.component.Button("Open .slate Screen", "demo.authoring", primaryButtonStyle),
                    new top.huliawsl.slateui.api.component.Button("Inspect Runtime", "screen.inspect", primaryButtonStyle)
                ), SlateStyle.builder().gap(8).build())
            ),
            panelStyle,
            SlateStyle.builder().gap(8).build()
        ));

        List<SlateComponent> scrollItems = new ArrayList<>();
        for (int index = 1; index <= 18; index++) {
            scrollItems.add(new Text("Scroll item #" + index));
        }
        sections.add(new DemoPanel(
            "Scroll Page",
            List.of(
                new ScrollView(
                    new Stack(StackDirection.COLUMN, scrollItems, SlateStyle.builder().gap(4).build()),
                    SlateStyle.builder().height(120).padding(Insets.all(8)).backgroundColor(0xFF0F172A).border(new SlateBorder(0xFF334155, 1)).build()
                )
            ),
            panelStyle,
            SlateStyle.builder().gap(8).build()
        ));

        sections.add(new DemoPanel(
            "Theme + Image Page",
            List.of(
                new Text(ignored -> "Theme: " + provider.get("theme.name"), SlateStyle.EMPTY),
                new Image("minecraft:textures/gui/options_background.png", SlateStyle.builder().width(300).height(64).border(new SlateBorder(0xFF334155, 1)).build())
            ),
            panelStyle,
            SlateStyle.builder().gap(8).build()
        ));

        sections.add(new DemoPanel(
            "Debug Page",
            List.of(
                new Text(debugEnabled ? "Debug overlay is enabled for this screen." : "Open Debug Mode to see runtime debug boxes."),
                new Text("Missing resources are rendered as safe placeholders instead of crashing.")
            ),
            panelStyle,
            SlateStyle.builder().gap(8).build()
        ));

        sections.add(new DemoPanel(
            "Actions",
            List.of(
                new top.huliawsl.slateui.api.component.Button("Close Screen", "screen.close", primaryButtonStyle)
            ),
            panelStyle,
            SlateStyle.builder().gap(8).build()
        ));

        SlateComponent content = new ScrollView(
            new Stack(StackDirection.COLUMN, sections, columnStyle),
            SlateStyle.builder().height(220).width(360).padding(Insets.all(4)).build()
        );
        SlateComponent popupOverlay = new Box(List.of(
            new Popup(
                new Tooltip(
                    new top.huliawsl.slateui.api.component.Button("Toggle Popup", "demo.popup.toggle", primaryButtonStyle),
                    new Box(List.of(new Text("Tooltip follows hover state.")), panelStyle),
                    SlateStyle.EMPTY
                ),
                new Box(List.of(new Text("Popup content")), panelStyle),
                () -> provider.get("ui.popupOpen"),
                SlateStyle.EMPTY
            )
        ), SlateStyle.builder().width(360).build());
        SlateComponent modalOverlay = new Modal(
            new top.huliawsl.slateui.api.component.Button("Open Modal", "demo.modal.open", primaryButtonStyle),
            new Box(List.of(
                new Text("Modal overlay"),
                new top.huliawsl.slateui.api.component.Button("Close Modal", "demo.modal.close", primaryButtonStyle)
            ), panelStyle),
            () -> provider.get("ui.modalOpen"),
            SlateStyle.EMPTY
        );
        OverlayRoot root = new OverlayRoot(List.of(
            new Box(List.of(content), rootStyle),
            popupOverlay,
            modalOverlay
        ), rootStyle);
        return new SlateScreen(DEMO_TITLE, root, commands, provider, theme, debugEnabled);
    }

    public static SlateScreen createAuthoringScreen(boolean debugEnabled) {
        if (!SlateIrLoader.resourceExists(AUTHORING_RESOURCE)) {
            return createGalleryScreen(debugEnabled, "Authoring screen unavailable. Run compileSlate first.");
        }
        ComputedStateProvider provider = new ComputedStateProvider()
            .set("settings.playerName", "Slate Author")
            .set("settings.status", debugEnabled ? "Debug authoring" : "Authoring ready")
            .registerComputed("settings.summary", List.of("settings.playerName", "settings.status"), state ->
                state.get("settings.playerName") + " / " + state.get("settings.status"));
        Theme theme = createTheme(debugEnabled);
        return new SlateIrRuntimeFactory().createScreen(DEMO_TITLE, AUTHORING_RESOURCE, createAuthoringCommands(debugEnabled, provider, theme), provider, theme, debugEnabled);
    }

    private static SlateCommandRegistry createGalleryCommands(boolean debugEnabled, ComputedStateProvider provider, Theme theme) {
        SlateCommandRegistry commands = new SlateCommandRegistry();
        commands.register("demo.error", context -> context.minecraft().setScreen(createFaultyScreen(debugEnabled)));
        commands.register("demo.debug", context -> context.minecraft().setScreen(createGalleryScreen(true)));
        commands.register("demo.normal", context -> context.minecraft().setScreen(createGalleryScreen(false)));
        commands.register("demo.authoring", context -> context.minecraft().setScreen(createAuthoringScreen(debugEnabled)));
        commands.register("demo.popup.toggle", context -> provider.set("ui.popupOpen", !(Boolean) provider.get("ui.popupOpen")));
        commands.register("demo.modal.open", context -> provider.set("ui.modalOpen", true));
        commands.register("demo.modal.close", context -> provider.set("ui.modalOpen", false));
        commands.register("slate.reload", context -> SlateReloadSupport.reload(AUTHORING_RESOURCE, DEMO_TITLE, createAuthoringCommands(debugEnabled, provider, theme), provider, theme, debugEnabled));
        return commands;
    }

    private static SlateCommandRegistry createAuthoringCommands(boolean debugEnabled, ComputedStateProvider provider, Theme theme) {
        SlateCommandRegistry commands = new SlateCommandRegistry();
        commands.register("demo.authoring", context -> context.minecraft().setScreen(createAuthoringScreen(debugEnabled)));
        commands.register("demo.normal", context -> context.minecraft().setScreen(createGalleryScreen(false)));
        commands.register("demo.debug", context -> context.minecraft().setScreen(createGalleryScreen(true)));
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
}
