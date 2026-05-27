package top.huliawsl.slateui.api.component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.SlateText;
import top.huliawsl.slateui.api.StateProvider;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.DrawTextCommand;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public class Text extends SlateComponent {

    private final StateProvider stateProvider;
    private final Function<StateProvider, String> textResolver;
    private String resolvedText = "";
    private List<String> lines = List.of("");
    private int lineHeight;

    public Text(String text) {
        this(provider -> text, SlateStyle.EMPTY);
    }

    public Text(SlateText text) {
        this(provider -> text == null ? "" : text.fallbackText(), SlateStyle.EMPTY);
    }

    public Text(String text, SlateStyle style) {
        this(provider -> text, style);
    }

    public Text(SlateText text, SlateStyle style) {
        this(provider -> text == null ? "" : text.fallbackText(), style);
    }

    public Text(Function<StateProvider, String> textResolver, SlateStyle style) {
        this(StateProvider.EMPTY, textResolver, style);
    }

    public Text(StateProvider stateProvider, Function<StateProvider, String> textResolver, SlateStyle style) {
        super(style);
        this.stateProvider = stateProvider == null ? StateProvider.EMPTY : stateProvider;
        this.textResolver = textResolver;
    }

    public String text() {
        return resolvedText;
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        resolvedText = safeText(textResolver.apply(stateProvider));
        int wrapWidth = style().width() != null
            ? Math.max(0, style().width() - style().padding().horizontal())
            : Math.max(0, available.width());
        lines = wrapWidth > 0 ? wrapText(context, resolvedText, wrapWidth) : List.of(resolvedText);
        if (lines.isEmpty()) {
            lines = List.of("");
        }
        lineHeight = context.lineHeight();
        int maxWidth = 0;
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, context.textWidth(line));
        }
        Size measured = applyStyleSize(addInsets(new Size(maxWidth, Math.max(lineHeight, lines.size() * lineHeight)), style().padding()));
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
        for (int index = 0; index < lines.size(); index++) {
            commands.add(new DrawTextCommand(
                contentRect.x(),
                contentRect.y() + index * lineHeight,
                lines.get(index),
                resolveTextColor(context.theme())
            ));
        }
    }

    private static String safeText(String value) {
        return value == null ? "" : value;
    }

    private static List<String> wrapText(SlateLayoutContext context, String text, int maxWidth) {
        List<String> wrapped = new ArrayList<>();
        for (String paragraph : text.split("\\R", -1)) {
            if (paragraph.isEmpty()) {
                wrapped.add("");
                continue;
            }
            StringBuilder line = new StringBuilder();
            for (int index = 0; index < paragraph.length(); index++) {
                char character = paragraph.charAt(index);
                if (line.length() == 0 && character == ' ') {
                    continue;
                }
                String candidate = line.toString() + character;
                if (line.length() > 0 && context.textWidth(candidate) > maxWidth) {
                    wrapped.add(line.toString());
                    line.setLength(0);
                    if (character == ' ') {
                        continue;
                    }
                }
                line.append(character);
            }
            wrapped.add(line.toString());
        }
        return wrapped;
    }
}
