package top.huliawsl.slateui.demo;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import top.huliawsl.slateui.api.HorizontalAlign;
import top.huliawsl.slateui.api.SlateBorder;
import top.huliawsl.slateui.api.SlateScreen;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;
import top.huliawsl.slateui.api.VerticalAlign;
import top.huliawsl.slateui.api.component.Box;
import top.huliawsl.slateui.api.component.Stack;
import top.huliawsl.slateui.api.component.Text;
import top.huliawsl.slateui.command.SlateCommandRegistry;
import top.huliawsl.slateui.layout.Insets;

public final class SlateDemoEntrypoint {

    private static final Component DEMO_TITLE = Component.literal("SlateUI MVP0");

    private SlateDemoEntrypoint() {
    }

    public static Button createTitleScreenButton(TitleScreen screen, int x, int y) {
        return Button.builder(Component.literal("SlateUI MVP0"), button -> Minecraft.getInstance().setScreen(createDemoScreen()))
            .bounds(x, y, 124, 20)
            .build();
    }

    public static SlateScreen createDemoScreen() {
        SlateCommandRegistry commands = new SlateCommandRegistry();
        SlateStyle rootStyle = SlateStyle.builder()
            .padding(Insets.all(20))
            .backgroundColor(0xFF111827)
            .horizontalAlign(HorizontalAlign.CENTER)
            .verticalAlign(VerticalAlign.CENTER)
            .build();
        SlateStyle panelStyle = SlateStyle.builder()
            .width(240)
            .padding(Insets.all(12))
            .backgroundColor(0xFF1F2937)
            .border(new SlateBorder(0xFF60A5FA, 1))
            .build();
        SlateStyle contentStyle = SlateStyle.builder()
            .gap(8)
            .build();
        SlateStyle buttonStyle = SlateStyle.builder()
            .padding(Insets.symmetric(10, 6))
            .backgroundColor(0xFF2563EB)
            .border(new SlateBorder(0xFFBFDBFE, 1))
            .horizontalAlign(HorizontalAlign.CENTER)
            .build();

        DemoPanel panel = new DemoPanel(
            "MVP0 Runtime",
            List.of(
                new InfoRow("Loader", "Shared Common Runtime"),
                new InfoRow("Model", "Props + Children + Commands"),
                new Box(
                    List.of(new Text("Default slot content is projected here.")),
                    SlateStyle.builder()
                        .padding(Insets.all(8))
                        .backgroundColor(0xFF0F172A)
                        .border(new SlateBorder(0xFF334155, 1))
                        .build()
                ),
                new top.huliawsl.slateui.api.component.Button("Close Screen", "screen.close", buttonStyle)
            ),
            panelStyle,
            contentStyle
        );

        Box root = new Box(List.of(panel), rootStyle);
        return new SlateScreen(DEMO_TITLE, root, commands, false);
    }
}
