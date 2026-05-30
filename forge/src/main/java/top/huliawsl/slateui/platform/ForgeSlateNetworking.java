package top.huliawsl.slateui.platform;

import top.huliawsl.slateui.server.SlateIntentPacket;

public final class ForgeSlateNetworking {

    private ForgeSlateNetworking() {
    }

    public static String channelId() {
        return "slateui:intent";
    }

    public static SlateIntentPacket passthrough(SlateIntentPacket packet) {
        return packet;
    }
}
