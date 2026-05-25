package top.huliawsl.slateui.command;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public record CommandContext(Minecraft minecraft, Screen screen) {
}
