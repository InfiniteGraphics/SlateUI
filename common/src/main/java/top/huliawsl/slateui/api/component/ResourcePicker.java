package top.huliawsl.slateui.api.component;

import java.util.List;
import java.util.Map;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;

public final class ResourcePicker extends Stack {

    public ResourcePicker(String resourceLocation, String browseCommand, SlateStyle style) {
        super(StackDirection.ROW, List.of(
            new ResourceLocationInput(resourceLocation, null, SlateStyle.builder().width(160).build()),
            new Button("Browse", browseCommand, Map.of("resourceLocation", resourceLocation == null ? "" : resourceLocation), SlateStyle.EMPTY)
        ), style);
    }
}
