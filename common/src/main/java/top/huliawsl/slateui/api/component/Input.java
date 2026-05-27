package top.huliawsl.slateui.api.component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.lwjgl.glfw.GLFW;
import top.huliawsl.slateui.api.InputValueHandler;
import top.huliawsl.slateui.debug.SlateRuntimeException;
import top.huliawsl.slateui.api.SlateBorder;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StateProvider;
import top.huliawsl.slateui.layout.Insets;
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

    private static final SlateStyle DEFAULT_STYLE = SlateStyle.builder()
        .padding(Insets.symmetric(8, 6))
        .backgroundColor(0xFF0F172A)
        .hoverBackgroundColor(0xFF111C31)
        .border(new SlateBorder(0xFF475569, 1))
        .focusBorder(new SlateBorder(0xFF60A5FA, 1))
        .borderRadiusToken("radius.sm")
        .clipContent(true)
        .build();

    private final StateProvider stateProvider;
    private final Function<StateProvider, String> valueResolver;
    private final String placeholder;
    private final String inputCommand;
    private final String changeCommand;
    private final String commitCommand;
    private final InputValueHandler changeHandler;
    private final int maxLength;
    private String draft = "";
    private int cursor;
    private int selectionAnchor;
    private boolean initialized;

    public Input(String placeholder, String initialValue, String changeCommand, SlateStyle style) {
        this(placeholder, provider -> initialValue, changeCommand, null, style);
    }

    public Input(String placeholder, Function<StateProvider, String> valueResolver, String changeCommand, InputValueHandler changeHandler, SlateStyle style) {
        this(StateProvider.EMPTY, placeholder, valueResolver, null, changeCommand, changeCommand, changeHandler, 0, style);
    }

    public Input(StateProvider stateProvider, String placeholder, Function<StateProvider, String> valueResolver, String changeCommand, InputValueHandler changeHandler, SlateStyle style) {
        this(stateProvider, placeholder, valueResolver, null, changeCommand, changeCommand, changeHandler, 0, style);
    }

    public Input(
        StateProvider stateProvider,
        String placeholder,
        Function<StateProvider, String> valueResolver,
        String inputCommand,
        String changeCommand,
        String commitCommand,
        InputValueHandler changeHandler,
        int maxLength,
        SlateStyle style
    ) {
        super(SlateStyle.withDefaults(DEFAULT_STYLE, style));
        this.stateProvider = stateProvider == null ? StateProvider.EMPTY : stateProvider;
        this.placeholder = placeholder == null ? "" : placeholder;
        this.valueResolver = Objects.requireNonNull(valueResolver, "valueResolver");
        this.inputCommand = inputCommand;
        this.changeCommand = changeCommand;
        this.commitCommand = commitCommand;
        this.changeHandler = changeHandler;
        this.maxLength = Math.max(0, maxLength);
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
            cursor = draft.length();
            selectionAnchor = cursor;
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
        commands.add(new PushClipCommand(content, contentClipRadius(context.theme())));
        String text = draft.isEmpty() ? placeholder : draft;
        int color = draft.isEmpty() ? 0xFF94A3B8 : resolveTextColor(context.theme());
        String renderedText = isFocused() && !draft.isEmpty()
            ? draft.substring(0, cursor) + "|" + draft.substring(cursor)
            : text + (isFocused() ? "|" : "");
        commands.add(new DrawTextCommand(content.x() + 2, content.y() + 2, renderedText, color));
        commands.add(new PopClipCommand());
    }

    @Override
    public boolean keyPressed(SlateInteractionContext context, int keyCode, int scanCode, int modifiers) {
        if (!isFocused()) {
            return false;
        }
        boolean shift = (modifiers & GLFW.GLFW_MOD_SHIFT) != 0;
        boolean control = (modifiers & GLFW.GLFW_MOD_CONTROL) != 0;
        if (control && keyCode == GLFW.GLFW_KEY_A) {
            cursor = draft.length();
            selectionAnchor = 0;
            context.screen().requestRebuild("input-select-all");
            return true;
        }
        if (control && keyCode == GLFW.GLFW_KEY_C) {
            copySelection(context);
            return true;
        }
        if (control && keyCode == GLFW.GLFW_KEY_X) {
            copySelection(context);
            if (hasSelection()) {
                replaceSelection(context, "");
            }
            return true;
        }
        if (control && keyCode == GLFW.GLFW_KEY_V) {
            paste(context);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            backspace(context);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_DELETE) {
            delete(context);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_LEFT) {
            moveCursor(Math.max(0, cursor - 1), shift);
            context.screen().requestRebuild("input-left");
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            moveCursor(Math.min(draft.length(), cursor + 1), shift);
            context.screen().requestRebuild("input-right");
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_HOME) {
            moveCursor(0, shift);
            context.screen().requestRebuild("input-home");
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_END) {
            moveCursor(draft.length(), shift);
            context.screen().requestRebuild("input-end");
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            commit(context);
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(SlateInteractionContext context, char codePoint, int modifiers) {
        if (!isFocused() || Character.isISOControl(codePoint)) {
            return false;
        }
        replaceSelection(context, String.valueOf(codePoint));
        return true;
    }

    private void commit(SlateInteractionContext context) {
        if (changeHandler != null) {
            changeHandler.onChange(context, draft);
        }
        runValueCommand(context, commitCommand, "commit");
        context.logDiagnostic("INPUT commit=" + summarizeValue(draft));
    }

    private void runValueCommand(SlateInteractionContext context, String command, String phase) {
        if (command != null && !command.isBlank()) {
            try {
                boolean executed = context.commands().execute(command, context, Map.of("value", draft, "phase", phase));
                context.commandLogger().accept((executed ? "EXEC " : "MISS ") + command + " component=" + debugPath() + " value=" + summarizeValue(draft));
                if (!executed) {
                    context.logDiagnostic("COMMAND missing id=" + command + " component=" + debugPath());
                }
            } catch (Throwable throwable) {
                throw SlateRuntimeException.command(this, command, throwable);
            }
        }
    }

    private void replaceSelection(SlateInteractionContext context, String text) {
        int start = selectionStart();
        int end = selectionEnd();
        String insert = fitToMaxLength(text == null ? "" : text, start, end);
        if (insert.isEmpty() && start == end) {
            return;
        }
        draft = draft.substring(0, start) + insert + draft.substring(end);
        cursor = start + insert.length();
        selectionAnchor = cursor;
        if (changeHandler != null) {
            changeHandler.onChange(context, draft);
        }
        runValueCommand(context, inputCommand, "input");
        runValueCommand(context, changeCommand, "change");
        context.screen().requestRebuild("input-change");
    }

    private String fitToMaxLength(String value, int selectionStart, int selectionEnd) {
        if (maxLength <= 0) {
            return value;
        }
        int available = maxLength - (draft.length() - (selectionEnd - selectionStart));
        if (available <= 0) {
            return "";
        }
        return value.length() <= available ? value : value.substring(0, available);
    }

    private void backspace(SlateInteractionContext context) {
        if (hasSelection()) {
            replaceSelection(context, "");
            return;
        }
        if (cursor == 0) {
            return;
        }
        selectionAnchor = cursor - 1;
        replaceSelection(context, "");
    }

    private void delete(SlateInteractionContext context) {
        if (hasSelection()) {
            replaceSelection(context, "");
            return;
        }
        if (cursor >= draft.length()) {
            return;
        }
        selectionAnchor = cursor + 1;
        replaceSelection(context, "");
    }

    private void paste(SlateInteractionContext context) {
        String clipboard = context.clipboard().get();
        if (clipboard != null && !clipboard.isEmpty()) {
            replaceSelection(context, clipboard);
        }
    }

    private void copySelection(SlateInteractionContext context) {
        if (hasSelection()) {
            context.clipboard().set(draft.substring(selectionStart(), selectionEnd()));
        }
    }

    private void moveCursor(int nextCursor, boolean keepSelection) {
        cursor = Math.max(0, Math.min(nextCursor, draft.length()));
        if (!keepSelection) {
            selectionAnchor = cursor;
        }
    }

    private boolean hasSelection() {
        return cursor != selectionAnchor;
    }

    private int selectionStart() {
        return Math.min(cursor, selectionAnchor);
    }

    private int selectionEnd() {
        return Math.max(cursor, selectionAnchor);
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
