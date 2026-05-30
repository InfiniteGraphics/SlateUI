package top.huliawsl.slateui.api.component;

import top.huliawsl.slateui.api.SlateStyle;

public final class PlayerHead extends ItemIcon {

    public PlayerHead(String playerName, SlateStyle style) {
        super("minecraft:player_head", 1, style);
        componentKey(playerName);
    }
}
