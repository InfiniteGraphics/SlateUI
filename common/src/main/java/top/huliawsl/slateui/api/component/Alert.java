package top.huliawsl.slateui.api.component;

import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;

public final class Alert extends Panel {

    public Alert(String title, String message, SlateStyle style) {
        super(title, List.of(new Text(message)), style);
    }
}
