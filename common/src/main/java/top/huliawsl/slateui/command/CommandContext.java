package top.huliawsl.slateui.command;

import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public record CommandContext(Minecraft minecraft, Screen screen, Map<String, Object> payload) {

    public CommandContext(Minecraft minecraft, Screen screen) {
        this(minecraft, screen, Map.of());
    }

    public CommandContext {
        payload = payload == null ? Map.of() : Map.copyOf(payload);
    }

    public CommandContext withPayload(Map<String, Object> payload) {
        return new CommandContext(minecraft, screen, payload);
    }

    public Object payload(String key) {
        return payload.get(key);
    }

    public String payloadString(String key, String fallback) {
        Object value = payload.get(key);
        return value == null ? fallback : String.valueOf(value);
    }

    public int payloadInt(String key, int fallback) {
        Object value = payload.get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    public boolean payloadBoolean(String key, boolean fallback) {
        Object value = payload.get(key);
        if (value instanceof Boolean bool) {
            return bool;
        }
        return value == null ? fallback : Boolean.parseBoolean(String.valueOf(value));
    }
}
