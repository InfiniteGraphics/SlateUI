package top.huliawsl.slateui.editor;

import java.util.ArrayList;
import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.Theme;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public final class SlateEditorPreview {

    private final SlateComponent root;
    private final Theme theme;

    public SlateEditorPreview(SlateComponent root, Theme theme) {
        this.root = root;
        this.theme = theme == null ? Theme.DEFAULT : theme;
    }

    public List<DrawCommand> renderPreview(Size size) {
        Size available = size == null ? Size.ZERO : size;
        SlateLayoutContext layoutContext = new SlateLayoutContext(null, theme);
        root.measure(layoutContext, available);
        root.layout(layoutContext, new Rect(0, 0, available.width(), available.height()));
        List<DrawCommand> commands = new ArrayList<>();
        root.collectDrawCommands(new SlateRenderContext(false, theme), commands);
        return List.copyOf(commands);
    }

    public String componentTreePanel() {
        return root.dumpComponentTree();
    }

    public String propertyPanel() {
        return root.dumpStyleTree(theme);
    }
}
