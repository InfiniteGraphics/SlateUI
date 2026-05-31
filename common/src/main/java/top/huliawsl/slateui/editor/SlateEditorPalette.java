package top.huliawsl.slateui.editor;

import java.util.List;

public record SlateEditorPalette(
    List<String> themeTokens,
    List<String> commands,
    List<String> bindings
) {

    public SlateEditorPalette {
        themeTokens = themeTokens == null ? List.of() : List.copyOf(themeTokens);
        commands = commands == null ? List.of() : List.copyOf(commands);
        bindings = bindings == null ? List.of() : List.copyOf(bindings);
    }
}
