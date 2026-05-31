package top.huliawsl.slateui.editor;

import java.util.Map;

public record SlateEditorAction(Type type, String componentPath, Map<String, Object> payload) {

    public enum Type {
        RESIZE,
        REORDER,
        STYLE_EDIT,
        SELECT_THEME_TOKEN,
        SELECT_COMMAND,
        SELECT_BINDING,
        SOURCE_NAVIGATION
    }

    public SlateEditorAction {
        componentPath = componentPath == null ? "" : componentPath;
        payload = payload == null ? Map.of() : Map.copyOf(payload);
    }
}
