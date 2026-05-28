package top.huliawsl.slateui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import org.junit.jupiter.api.Test;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateText;
import top.huliawsl.slateui.authoring.SlateIrRuntimeFactory;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.DrawTextCommand;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

class TextAuthoringRuntimeTest {

    @Test
    void authoredTextPreservesTranslatableModel() {
        JsonObject root = new JsonObject();
        root.addProperty("componentType", "Text");
        root.add("children", new JsonArray());
        root.add("bindings", new JsonObject());
        JsonObject props = new JsonObject();
        props.addProperty("translationKey", "slateui.test.title");
        props.addProperty("translationArgs", "one,two");
        root.add("props", props);

        SlateComponent component = new SlateIrRuntimeFactory().buildComponentTree(root, top.huliawsl.slateui.api.StateProvider.EMPTY, top.huliawsl.slateui.api.Theme.DEFAULT);
        component.measure(new SlateLayoutContext(null), new Size(200, 40));
        component.layout(new SlateLayoutContext(null), new Rect(0, 0, 200, 20));
        List<DrawCommand> commands = new java.util.ArrayList<>();
        component.collectDrawCommands(new SlateRenderContext(false, top.huliawsl.slateui.api.Theme.DEFAULT), commands);

        DrawTextCommand text = commands.stream()
            .filter(DrawTextCommand.class::isInstance)
            .map(DrawTextCommand.class::cast)
            .findFirst()
            .orElseThrow();
        SlateText.Translatable translatable = assertInstanceOf(SlateText.Translatable.class, text.slateText());
        assertEquals("slateui.test.title", translatable.key());
        assertEquals(List.of("one", "two"), translatable.args());
    }
}
