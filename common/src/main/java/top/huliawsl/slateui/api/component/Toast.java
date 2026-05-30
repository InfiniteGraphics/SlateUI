package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.SlateStyle;

public final class Toast extends Panel {

    private final long ttlMillis;

    public Toast(String title, String message, long ttlMillis, SlateStyle style) {
        super(title, List.of(new Text(message)), style);
        this.ttlMillis = Math.max(0L, ttlMillis);
    }

    public long ttlMillis() {
        return ttlMillis;
    }
}
