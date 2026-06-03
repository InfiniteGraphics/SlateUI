package top.huliawsl.slateui.api.component;

import java.util.List;

public record ParameterDescriptor(
    String key,
    String label,
    ParameterType type,
    Object defaultValue,
    List<String> options,
    String placeholder,
    String description
) {

    public ParameterDescriptor {
        key = key == null ? "" : key;
        label = label == null || label.isBlank() ? key : label;
        type = type == null ? ParameterType.STRING : type;
        options = List.copyOf(options == null ? List.of() : options);
        placeholder = placeholder == null ? "" : placeholder;
        description = description == null ? "" : description;
    }

    public static ParameterDescriptor string(String key, String label, Object defaultValue) {
        return new ParameterDescriptor(key, label, ParameterType.STRING, defaultValue, List.of(), "", "");
    }

    public static ParameterDescriptor integer(String key, String label, Object defaultValue) {
        return new ParameterDescriptor(key, label, ParameterType.INTEGER, defaultValue, List.of(), "", "");
    }

    public static ParameterDescriptor floating(String key, String label, Object defaultValue) {
        return new ParameterDescriptor(key, label, ParameterType.FLOAT, defaultValue, List.of(), "", "");
    }

    public static ParameterDescriptor bool(String key, String label, Object defaultValue) {
        return new ParameterDescriptor(key, label, ParameterType.BOOLEAN, defaultValue, List.of(), "", "");
    }

    public static ParameterDescriptor enumeration(String key, String label, Object defaultValue, List<String> options) {
        return new ParameterDescriptor(key, label, ParameterType.ENUM, defaultValue, options, "", "");
    }
}
