package top.huliawsl.slateui.render;

import java.util.Objects;
import top.huliawsl.slateui.layout.Rect;

public record DrawRawCommand(Rect bounds, RawDrawCallback callback, String description) implements DrawCommand {

    public DrawRawCommand {
        bounds = bounds == null ? Rect.ZERO : bounds;
        callback = Objects.requireNonNull(callback, "callback");
        description = description == null || description.isBlank() ? "raw" : description;
    }

    @Override
    public String describe() {
        return "raw " + description + " @" + bounds;
    }
}
