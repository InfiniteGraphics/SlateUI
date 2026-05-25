package top.huliawsl.slateui.api.component;

import java.util.List;
import java.util.function.Function;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StateProvider;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.DrawTextCommand;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public class Text extends SlateComponent {

    private final Function<StateProvider, String> textResolver;
    private String resolvedText = "";

    public Text(String text) {
        this(provider -> text, SlateStyle.EMPTY);
    }

    public Text(String text, SlateStyle style) {
        this(provider -> text, style);
    }

    public Text(Function<StateProvider, String> textResolver, SlateStyle style) {
        super(style);
        this.textResolver = textResolver;
    }

    public String text() {
        return resolvedText;
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        resolvedText = safeText(textResolver.apply(StateProvider.EMPTY));
        Size measured = applyStyleSize(addInsets(new Size(context.textWidth(resolvedText), context.lineHeight()), style().padding()));
        setMeasuredSize(measured);
        return measured;
    }

    @Override
    public void layout(SlateLayoutContext context, Rect bounds) {
        setBounds(bounds);
    }

    @Override
    public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
        emitBoxChrome(context, commands);
        Rect contentRect = contentRect(bounds());
        commands.add(new DrawTextCommand(contentRect.x(), contentRect.y(), resolvedText, resolveTextColor(context.theme())));
    }

    private static String safeText(String value) {
        return value == null ? "" : value;
    }
}
