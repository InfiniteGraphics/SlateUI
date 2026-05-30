package top.huliawsl.slateui.api.container;

import java.util.Map;

public record SlateMenuBinding(String menuId, Map<String, Object> values) {

    public SlateMenuBinding {
        menuId = menuId == null ? "" : menuId;
        values = values == null ? Map.of() : Map.copyOf(values);
    }
}
