package top.huliawsl.slateui.authoring;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;

public final class SlateAuthoringSchema {

    private static final Map<String, List<String>> COMPONENT_PROPS = Map.ofEntries(
        Map.entry("OverlayRoot", List.of("class")),
        Map.entry("Box", List.of("class")),
        Map.entry("Panel", List.of("class", "title", "contentGap")),
        Map.entry("Stack", List.of("class", "direction")),
        Map.entry("Text", List.of("class", "value", "translationKey", "translationArgs")),
        Map.entry("Button", List.of("class", "label", "command")),
        Map.entry("Input", List.of("class", "placeholder", "value", "onInput", "onChange", "onCommit", "maxLength")),
        Map.entry("Toggle", List.of("class", "label", "checked", "onChange", "command")),
        Map.entry("List", List.of("class", "itemGap")),
        Map.entry("ScrollView", List.of("class")),
        Map.entry("Image", List.of("class", "resource", "u", "v", "textureWidth", "textureHeight", "regionWidth", "regionHeight")),
        Map.entry("Tooltip", List.of("class")),
        Map.entry("Popup", List.of("class", "open")),
        Map.entry("Modal", List.of("class", "open")),
        Map.entry("SlotGrid", List.of("class", "slots", "columns", "slotSize", "slotGap", "command"))
    );

    private static final List<String> EXPERIMENTAL_COMPONENTS = List.of("Tooltip", "Popup", "Modal", "SlotGrid");

    private SlateAuthoringSchema() {
    }

    public static JsonObject export() {
        JsonObject schema = new JsonObject();
        schema.addProperty("schemaVersion", 1);
        JsonObject components = new JsonObject();
        for (Map.Entry<String, List<String>> entry : COMPONENT_PROPS.entrySet()) {
            JsonObject component = new JsonObject();
            JsonArray props = new JsonArray();
            for (String prop : entry.getValue()) {
                props.add(prop);
            }
            component.add("props", props);
            component.addProperty("experimental", EXPERIMENTAL_COMPONENTS.contains(entry.getKey()));
            components.add(entry.getKey(), component);
        }
        schema.add("components", components);
        return schema;
    }
}
