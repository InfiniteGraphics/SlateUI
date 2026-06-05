package top.huliawsl.slateui.platform.services;

import java.util.Map;

public interface SlateNetworkingBridge {

    SlateNetworkingBridge NOOP = new SlateNetworkingBridge() {
    };

    default void sendToServer(String channel, Map<String, Object> payload) {
    }

    default void sendToClient(String playerId, String channel, Map<String, Object> payload) {
    }

    default boolean available() {
        return false;
    }
}
