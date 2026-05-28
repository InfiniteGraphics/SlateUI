package examples.java;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import top.huliawsl.slateui.api.MutableStateProvider;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateScreen;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.Theme;
import top.huliawsl.slateui.api.component.Button;
import top.huliawsl.slateui.api.component.Input;
import top.huliawsl.slateui.api.component.Panel;
import top.huliawsl.slateui.api.component.Text;
import top.huliawsl.slateui.api.component.Toggle;
import top.huliawsl.slateui.command.SlateCommandRegistry;

public final class SettingsScreenExample {

    private SettingsScreenExample() {
    }

    public static void open() {
        MutableStateProvider state = new MutableStateProvider()
            .set("settings.name", "Alex")
            .set("settings.enabled", true);

        SlateCommandRegistry commands = new SlateCommandRegistry()
            .register("settings.save", context -> saveSettings(context.payload()));

        SlateComponent root = new Panel("Settings", List.of(
            new Text(provider -> "Name", SlateStyle.EMPTY),
            new Input(
                state,
                "Player name",
                provider -> String.valueOf(provider.get("settings.name")),
                null,
                (context, value) -> state.set("settings.name", value),
                SlateStyle.builder().width(180).build()
            ),
            new Toggle(
                state,
                "Enabled",
                provider -> Boolean.TRUE.equals(provider.get("settings.enabled")),
                null,
                (context, checked) -> state.set("settings.enabled", checked),
                SlateStyle.EMPTY
            ),
            new Button("Save", "settings.save", SlateStyle.EMPTY)
        ), SlateStyle.builder().width(260).build());

        Minecraft.getInstance().setScreen(new SlateScreen(
            Component.literal("Settings"),
            root,
            commands,
            state,
            Theme.DEFAULT,
            false
        ));
    }

    private static void saveSettings(Object payload) {
        // Persist config here.
    }
}
