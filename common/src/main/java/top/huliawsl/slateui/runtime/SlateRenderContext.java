package top.huliawsl.slateui.runtime;

import java.util.Objects;
import top.huliawsl.slateui.api.Theme;

public record SlateRenderContext(boolean debugEnabled, Theme theme) {

    public SlateRenderContext {
        Objects.requireNonNull(theme, "theme");
    }
}
