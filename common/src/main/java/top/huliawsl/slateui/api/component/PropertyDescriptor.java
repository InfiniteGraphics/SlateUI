package top.huliawsl.slateui.api.component;

public record PropertyDescriptor(
    String group,
    String name,
    String value,
    String tooltip,
    boolean readOnly,
    boolean error
) {
    public PropertyDescriptor(String group, String name, String value) {
        this(group, name, value, "", false, false);
    }

    public PropertyDescriptor {
        group = group == null ? "" : group;
        name = name == null ? "" : name;
        value = value == null ? "" : value;
        tooltip = tooltip == null ? "" : tooltip;
    }
}
