package top.huliawsl.slateui.runtime;

import java.util.Map;

public record SlateDragPayload(String type, Object data, Map<String, Object> metadata) {

    public SlateDragPayload(String type, Object data) {
        this(type, data, Map.of());
    }

    public SlateDragPayload {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Drag payload type must not be blank");
        }
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public boolean is(String expectedType) {
        return type.equals(expectedType);
    }
}
