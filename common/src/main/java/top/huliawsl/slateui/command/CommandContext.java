package top.huliawsl.slateui.command;

import java.util.Map;
import top.huliawsl.slateui.runtime.SlateHost;

public class CommandContext {

    private final SlateHost host;
    private final Map<String, Object> payload;

    public CommandContext() {
        this(SlateHost.NOOP, Map.of());
    }

    public CommandContext(SlateHost host) {
        this(host, Map.of());
    }

    public CommandContext(Object ignored, SlateHost host) {
        this(host, Map.of());
    }

    public CommandContext(SlateHost host, Map<String, Object> payload) {
        this.host = host == null ? SlateHost.NOOP : host;
        this.payload = payload == null ? Map.of() : Map.copyOf(payload);
    }

    public SlateHost host() {
        return host;
    }

    public Map<String, Object> payload() {
        return payload;
    }

    public CommandContext withPayload(Map<String, Object> payload) {
        return new CommandContext(host, payload);
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
