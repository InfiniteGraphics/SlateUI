package top.huliawsl.slateui.api.component;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.client.gui.screens.Screen;
import top.huliawsl.slateui.api.InputValueHandler;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StateProvider;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.DrawTextCommand;
import top.huliawsl.slateui.render.PopClipCommand;
import top.huliawsl.slateui.render.PushClipCommand;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public class Input extends SlateComponent {

    private final StateProvider stateProvider;
    private final Function<StateProvider, String> valueResolver;
    private final String placeholder;
    private final String changeCommand;
    private final InputValueHandler changeHandler;
    private String draft = "";
    private boolean initialized;

    public Input(String placeholder, String initialValue, String changeCommand, SlateStyle style) {
        this(placeholder, provider -> initialValue, changeCommand, null, style);
    }

    public Input(String placeholder, Function<StateProvider, String> valueResolver, String changeCommand, InputValueHandler changeHandler, SlateStyle style) {
        this(StateProvider.EMPTY, placeholder, valueResolver, changeCommand, changeHandler, style);
    }

    public Input(StateProvider stateProvider, String placeholder, Function<StateProvider, String> valueResolver, String changeCommand, InputValueHandler changeHandler, SlateStyle style) {
        super(style);
        this.stateProvider = stateProvider == null ? StateProvider.EMPTY : stateProvider;
        this.placeholder = placeholder == null ? "" : placeholder;
        this.valueResolver = Objects.requireNonNull(valueResolver, "valueResolver");
        this.changeCommand = changeCommand;
        this.changeHandler = changeHandler;
    }

    @Override
    public boolean focusable() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused) {
        if (!focused) {
            draft = draft.isBlank() ? draft : draft;
        }
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        if (!initialized) {
            draft = valueResolver.apply(stateProvider);
            if (draft == null) {
                draft = "";
            }
            initialized = true;
        }
        int width = style().width() != null ? style().width() : Math.min(available.width(), 180);
        int height = style().height() != null ? style().height() : context.lineHeight() + style().padding().vertical() + 8;
        Size measured = applyStyleSize(new Size(width, height));
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
        Rect content = contentRect(bounds());
        commands.add(new PushClipCommand(content));
        String text = draft.isEmpty() ? placeholder : draft;
        int color = draft.isEmpty() ? 0xFF94A3B8 : resolveTextColor(context.theme());
        commands.add(new DrawTextCommand(content.x() + 2, content.y() + 2, text + (isFocused() ? "|" : ""), color));
        commands.add(new PopClipCommand());
    }

    @Override
    public boolean keyPressed(SlateInteractionContext context, int keyCode, int scanCode, int modifiers) {
        if (!isFocused()) {
            return false;
        }
        if (keyCode == 259 && !draft.isEmpty()) {
            draft = draft.substring(0, draft.length() - 1);
            context.screen().requestRebuild("input-backspace");
            return true;
        }
        if (keyCode == 257 || keyCode == 335) {
            commit(context);
            return true;
        }
        if (Screen.isPaste(keyCode)) {
            String clipboard = context.commandContext().minecraft().keyboardHandler.getClipboard();
            if (clipboard != null && !clipboard.isEmpty()) {
                draft += clipboard;
                context.screen().requestRebuild("input-paste");
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(SlateInteractionContext context, char codePoint, int modifiers) {
        if (!isFocused() || Character.isISOControl(codePoint)) {
            return false;
        }
        draft += codePoint;
        context.screen().requestRebuild("input-char");
        return true;
    }

    private void commit(SlateInteractionContext context) {
        if (changeHandler != null) {
            changeHandler.onChange(context, draft);
        }
        if (changeCommand != null && !changeCommand.isBlank()) {
            boolean executed = context.commands().execute(changeCommand, context.commandContext());
            context.commandLogger().accept((executed ? "EXEC " : "MISS ") + changeCommand + " value=" + summarizeValue(draft));
        }
        context.logDiagnostic("INPUT commit=" + summarizeValue(draft));
    }

    private static String summarizeValue(String value) {
        if (value == null) {
            return "";
        }
        String normalized = value.replace("\r", "\\r").replace("\n", "\\n");
        if (normalized.length() <= 48) {
            return normalized;
        }
        return normalized.substring(0, 48) + "...";
    }
}
