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
import top.huliawsl.slateui.render.DrawRectCommand;
import top.huliawsl.slateui.render.DrawTextCommand;
import top.huliawsl.slateui.render.PopClipCommand;
import top.huliawsl.slateui.render.PushClipCommand;
import top.huliawsl.slateui.runtime.InvalidationType;
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
    private Function<String, String> validator;
    private boolean password;
    private String draft = "";
    private String validationError = "";
    private int cursor;
    private int selectionAnchor;
    private int textOffsetX;
    private int cursorPixelX;
    private int selectionStartPixelX;
    private int selectionEndPixelX;
    private int lineHeight = 9;
    private boolean initialized;
    private boolean draggingSelection;
    private long lastClickMillis;

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

    public Input validator(Function<String, String> validator) {
        this.validator = validator;
        validateDraft();
        return this;
    }

    public Input password(boolean password) {
        this.password = password;
        return this;
    }

    public String validationError() {
        return validationError;
    }

    @Override
    protected void onFocusChanged(boolean focused) {
        if (!focused) {
            syncDraftFromState();
        }
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        if (!initialized) {
            syncDraftFromState();
            cursor = draft.length();
            selectionAnchor = cursor;
            initialized = true;
        } else if (!isFocused()) {
            syncDraftFromState();
        }
        lineHeight = context.lineHeight();
        int width = style().width() != null ? style().width() : Math.min(available.width(), 180);
        int height = style().height() != null ? style().height() : lineHeight + style().padding().vertical() + 8;
        Size measured = applyStyleSize(new Size(width, height));
        updateTextViewport(context, Math.max(0, measured.width() - style().padding().horizontal() - 4));
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
        int textX = content.x() + 2 - textOffsetX;
        int textY = content.y() + 2;
        if (isFocused() && hasSelection()) {
            int selectionX = content.x() + 2 + selectionStartPixelX - textOffsetX;
            int selectionWidth = Math.max(1, selectionEndPixelX - selectionStartPixelX);
            commands.add(new DrawRectCommand(new Rect(selectionX, textY, selectionWidth, lineHeight), 0x663B82F6, 0));
        }
        String text = draft.isEmpty() && !isFocused() ? placeholder : displayText();
        int color = draft.isEmpty() ? 0xFF94A3B8 : resolveTextColor(context.theme());
        commands.add(new DrawTextCommand(textX, textY, text, color));
        if (isFocused()) {
            int cursorX = content.x() + 2 + cursorPixelX - textOffsetX;
            commands.add(new DrawRectCommand(new Rect(cursorX, textY, 1, lineHeight), resolveTextColor(context.theme()), 0));
        }
        if (!validationError.isBlank()) {
            commands.add(new DrawTextCommand(content.x() + 2, Math.min(bounds().bottom() - lineHeight, textY + lineHeight + 2), validationError, 0xFFFF6B6B));
        }
        commands.add(new PopClipCommand());
    }

    @Override
    public boolean mouseClicked(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        if (style().disabled() || !bounds().contains(mouseX, mouseY)) {
            return false;
        }
        context.requestFocus(this);
        int next = cursorAt(mouseX);
        long now = System.currentTimeMillis();
        if (now - lastClickMillis <= 350) {
            selectWord(next);
        } else {
            cursor = next;
            selectionAnchor = cursor;
        }
        lastClickMillis = now;
        draggingSelection = true;
        context.requestInvalidation(InvalidationType.INTERACTION, "input-cursor");
        return true;
    }

    @Override
    public boolean mouseReleased(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        if (draggingSelection) {
            draggingSelection = false;
            context.requestInvalidation(InvalidationType.INTERACTION, "input-selection");
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseMoved(SlateInteractionContext context, double mouseX, double mouseY) {
        if (draggingSelection) {
            cursor = cursorAt(mouseX);
            context.requestInvalidation(InvalidationType.INTERACTION, "input-drag-selection");
            return true;
        }
        return super.mouseMoved(context, mouseX, mouseY);
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
            context.requestInvalidation(InvalidationType.INTERACTION, "input-select-all");
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
            context.requestInvalidation(InvalidationType.INTERACTION, "input-left");
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            moveCursor(Math.min(draft.length(), cursor + 1), shift);
            context.requestInvalidation(InvalidationType.INTERACTION, "input-right");
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_HOME) {
            moveCursor(0, shift);
            context.requestInvalidation(InvalidationType.INTERACTION, "input-home");
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_END) {
            moveCursor(draft.length(), shift);
            context.requestInvalidation(InvalidationType.INTERACTION, "input-end");
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
        validateDraft();
        if (changeHandler != null) {
            changeHandler.onChange(context, draft);
        }
        runValueCommand(context, inputCommand, "input");
        runValueCommand(context, changeCommand, "change");
        context.requestInvalidation(InvalidationType.LAYOUT, "input-change");
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

    private void syncDraftFromState() {
        String value = valueResolver.apply(stateProvider);
        draft = value == null ? "" : value;
        validateDraft();
        cursor = Math.min(cursor, draft.length());
        selectionAnchor = Math.min(selectionAnchor, draft.length());
    }

    private void updateTextViewport(SlateLayoutContext context, int viewportWidth) {
        String text = displayText();
        cursorPixelX = context.textWidth(text.substring(0, Math.min(cursor, text.length())));
        selectionStartPixelX = context.textWidth(text.substring(0, selectionStart()));
        selectionEndPixelX = context.textWidth(text.substring(0, selectionEnd()));
        if (cursorPixelX - textOffsetX > viewportWidth) {
            textOffsetX = Math.max(0, cursorPixelX - viewportWidth);
        }
        if (cursorPixelX < textOffsetX) {
            textOffsetX = cursorPixelX;
        }
        int fullWidth = context.textWidth(text);
        textOffsetX = Math.min(textOffsetX, Math.max(0, fullWidth - viewportWidth));
    }

    private int cursorAt(double mouseX) {
        Rect content = contentRect(bounds());
        int localX = Math.max(0, (int) Math.round(mouseX) - content.x() - 2 + textOffsetX);
        int averageWidth = 6;
        int best = Math.round(localX / (float) averageWidth);
        return Math.max(0, Math.min(best, draft.length()));
    }

    private void selectWord(int index) {
        if (draft.isEmpty()) {
            cursor = 0;
            selectionAnchor = 0;
            return;
        }
        int start = Math.max(0, Math.min(index, draft.length()));
        int end = start;
        while (start > 0 && !Character.isWhitespace(draft.charAt(start - 1))) {
            start--;
        }
        while (end < draft.length() && !Character.isWhitespace(draft.charAt(end))) {
            end++;
        }
        selectionAnchor = start;
        cursor = end;
    }

    private String displayText() {
        if (!password) {
            return draft;
        }
        return "*".repeat(draft.length());
    }

    private void validateDraft() {
        validationError = validator == null ? "" : Objects.toString(validator.apply(draft), "");
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
