package top.huliawsl.slateui.api.component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import top.huliawsl.slateui.api.HorizontalAlign;
import top.huliawsl.slateui.api.SlateBorder;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StateProvider;
import top.huliawsl.slateui.api.ToggleValueHandler;
import top.huliawsl.slateui.debug.SlateRuntimeException;
import top.huliawsl.slateui.layout.Insets;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawBorderCommand;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.DrawRectCommand;
import top.huliawsl.slateui.render.DrawTextCommand;
import top.huliawsl.slateui.runtime.InvalidationType;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public class Toggle extends SlateComponent {

    private static final int CHECK_SIZE = 12;
    private static final int LABEL_GAP = 6;
    private static final SlateStyle DEFAULT_STYLE = SlateStyle.builder()
        .padding(Insets.symmetric(8, 5))
        .backgroundColor(0xFF0F172A)
        .hoverBackgroundColor(0xFF111C31)
        .activeBackgroundColor(0xFF1E293B)
        .border(new SlateBorder(0xFF475569, 1))
        .focusBorder(new SlateBorder(0xFF60A5FA, 1))
        .borderRadiusToken("radius.sm")
        .horizontalAlign(HorizontalAlign.START)
        .clipContent(true)
        .build();

    private final StateProvider stateProvider;
    private final String label;
    private final Function<StateProvider, Boolean> checkedResolver;
    private final String changeCommand;
    private final ToggleValueHandler changeHandler;
    private boolean checked;
    private int lineHeight = 9;

    public Toggle(String label, boolean checked, String changeCommand, SlateStyle style) {
        this(StateProvider.EMPTY, label, ignored -> checked, changeCommand, null, style);
    }

    public Toggle(StateProvider stateProvider, String label, Function<StateProvider, Boolean> checkedResolver, String changeCommand, ToggleValueHandler changeHandler, SlateStyle style) {
        super(SlateStyle.withDefaults(DEFAULT_STYLE, style));
        this.stateProvider = stateProvider == null ? StateProvider.EMPTY : stateProvider;
        this.label = label == null ? "" : label;
        this.checkedResolver = Objects.requireNonNull(checkedResolver, "checkedResolver");
        this.changeCommand = changeCommand;
        this.changeHandler = changeHandler;
    }

    public boolean checked() {
        return checked;
    }

    @Override
    public boolean focusable() {
        return true;
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        checked = Boolean.TRUE.equals(checkedResolver.apply(stateProvider));
        lineHeight = context.lineHeight();
        int labelWidth = label.isBlank() ? 0 : context.textWidth(label) + LABEL_GAP;
        int width = CHECK_SIZE + labelWidth;
        int height = Math.max(CHECK_SIZE, lineHeight);
        Size measured = applyStyleSize(addInsets(new Size(width, height), style().padding()));
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
        int checkY = content.y() + Math.max(0, (content.height() - CHECK_SIZE) / 2);
        Rect checkRect = new Rect(content.x(), checkY, CHECK_SIZE, CHECK_SIZE);
        int checkFill = checked ? context.theme().resolveColor(null, "color.primary", 0xFF3B82F6) : 0xFF020617;
        commands.add(new DrawRectCommand(checkRect, checkFill, Math.min(3, resolveBorderRadius(context.theme()))));
        commands.add(new DrawBorderCommand(checkRect, checked ? 0xFFBFDBFE : 0xFF64748B, 1, Math.min(3, resolveBorderRadius(context.theme()))));
        if (checked) {
            commands.add(new DrawTextCommand(checkRect.x() + 2, checkRect.y() + 1, "x", 0xFFFFFFFF));
        }
        if (!label.isBlank()) {
            commands.add(new DrawTextCommand(checkRect.right() + LABEL_GAP, content.y() + Math.max(0, (content.height() - lineHeight) / 2), label, resolveTextColor(context.theme())));
        }
    }

    @Override
    public boolean mouseReleased(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        boolean handled = super.mouseReleased(context, mouseX, mouseY, button);
        if (!handled || !bounds().contains(mouseX, mouseY)) {
            return handled;
        }
        boolean next = !checked;
        checked = next;
        if (changeHandler != null) {
            changeHandler.onChange(context, next);
        }
        if (changeCommand != null && !changeCommand.isBlank()) {
            try {
                boolean executed = context.commands().execute(changeCommand, context, Map.of("checked", next));
                context.commandLogger().accept((executed ? "EXEC " : "MISS ") + changeCommand + " component=" + debugPath() + " checked=" + next);
                if (!executed) {
                    context.logDiagnostic("COMMAND missing id=" + changeCommand + " component=" + debugPath() + " checked=" + next);
                }
            } catch (Throwable throwable) {
                throw SlateRuntimeException.command(this, changeCommand, throwable);
            }
        }
        context.logDiagnostic("TOGGLE checked=" + next + " component=" + debugPath());
        context.requestInvalidation(InvalidationType.LAYOUT, "toggle:" + debugName());
        return true;
    }
}
