package top.huliawsl.slateui.api.component;

import java.util.function.Function;

public record TableColumn<T>(String header, int width, Function<T, String> valueResolver) {
    public TableColumn {
        header = header == null ? "" : header;
        width = Math.max(24, width);
        valueResolver = valueResolver == null ? ignored -> "" : valueResolver;
    }
}
