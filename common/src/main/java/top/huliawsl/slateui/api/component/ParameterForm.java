package top.huliawsl.slateui.api.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import top.huliawsl.slateui.api.HorizontalAlign;
import top.huliawsl.slateui.api.SlateBorder;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;
import top.huliawsl.slateui.layout.Insets;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.runtime.InvalidationType;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public final class ParameterForm extends SlateComponent {

    private static final SlateStyle DEFAULT_STYLE = SlateStyle.builder()
        .padding(Insets.all(8))
        .gap(8)
        .clipContent(true)
        .build();

    private static final SlateStyle LABEL_STYLE = SlateStyle.builder()
        .textColor(0xFFE2E8F0)
        .width(120)
        .build();

    private static final SlateStyle DESCRIPTION_STYLE = SlateStyle.builder()
        .textColor(0xFF94A3B8)
        .build();

    private final List<ParameterDescriptor> parameters;
    private final Map<String, Object> values;
    private final ParameterValueHandler valueHandler;
    private final Stack stack;

    public ParameterForm(List<ParameterDescriptor> parameters, Map<String, Object> values, ParameterValueHandler valueHandler, SlateStyle style) {
        super(SlateStyle.withDefaults(DEFAULT_STYLE, style));
        this.parameters = List.copyOf(parameters == null ? List.of() : parameters);
        this.values = values == null ? Map.of() : Map.copyOf(values);
        this.valueHandler = valueHandler;
        this.stack = new Stack(StackDirection.COLUMN, buildRows(), SlateStyle.builder().gap(style().gap()).build());
    }

    public List<ParameterDescriptor> parameters() {
        return parameters;
    }

    @Override
    public List<SlateComponent> children() {
        return List.of(stack);
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        Size measured = measureChild(context, stack, contentAvailable(available));
        measured = applyStyleSize(addInsets(measured, style().padding()));
        setMeasuredSize(measured);
        return measured;
    }

    @Override
    public void layout(SlateLayoutContext context, Rect bounds) {
        setBounds(bounds);
        layoutChild(context, stack, contentRect(bounds));
    }

    @Override
    public void collectDrawCommands(SlateRenderContext context, List<DrawCommand> commands) {
        emitBoxChrome(context, commands);
        collectChild(context, commands, stack);
    }

    private List<SlateComponent> buildRows() {
        List<SlateComponent> rows = new ArrayList<>();
        for (ParameterDescriptor parameter : parameters) {
            rows.add(rowFor(parameter));
        }
        return rows;
    }

    private SlateComponent rowFor(ParameterDescriptor parameter) {
        List<SlateComponent> cells = new ArrayList<>();
        cells.add(new Text(parameter.label(), LABEL_STYLE));
        List<SlateComponent> controlColumn = new ArrayList<>();
        controlColumn.add(controlFor(parameter));
        if (!parameter.description().isBlank()) {
            controlColumn.add(new Text(parameter.description(), DESCRIPTION_STYLE));
        }
        cells.add(new Stack(StackDirection.COLUMN, controlColumn, SlateStyle.builder().gap(3).horizontalAlign(HorizontalAlign.STRETCH).build()));
        return new Stack(StackDirection.ROW, cells, SlateStyle.builder().gap(8).horizontalAlign(HorizontalAlign.STRETCH).build());
    }

    private SlateComponent controlFor(ParameterDescriptor parameter) {
        Object currentValue = values.getOrDefault(parameter.key(), parameter.defaultValue());
        return switch (parameter.type()) {
            case BOOLEAN -> new Toggle(parameter.placeholder().isBlank() ? "Enabled" : parameter.placeholder(), asBoolean(currentValue), null, SlateStyle.EMPTY) {
                @Override
                public boolean mouseReleased(SlateInteractionContext context, double mouseX, double mouseY, int button) {
                    boolean before = checked();
                    boolean handled = super.mouseReleased(context, mouseX, mouseY, button);
                    if (handled && before != checked() && valueHandler != null) {
                        valueHandler.onChange(context, parameter.key(), checked());
                    }
                    return handled;
                }
            };
            case INTEGER -> new Input(parameter.placeholder(), ignored -> asString(currentValue), null, (context, value) -> emit(context, parameter, parseInteger(value)), SlateStyle.EMPTY)
                .validator(value -> numericError(value, true));
            case FLOAT -> new Input(parameter.placeholder(), ignored -> asString(currentValue), null, (context, value) -> emit(context, parameter, parseFloat(value)), SlateStyle.EMPTY)
                .validator(value -> numericError(value, false));
            case ENUM -> new ChoiceControl(parameter, asString(currentValue), valueHandler, SlateStyle.EMPTY);
            case RESOURCE_ID -> new Input(parameter.placeholder().isBlank() ? "namespace:path" : parameter.placeholder(), ignored -> asString(currentValue), null, (context, value) -> emit(context, parameter, value), SlateStyle.EMPTY)
                .validator(ParameterForm::resourceIdError);
            case STRING -> new Input(parameter.placeholder(), ignored -> asString(currentValue), null, (context, value) -> emit(context, parameter, value), SlateStyle.EMPTY);
        };
    }

    private void emit(SlateInteractionContext context, ParameterDescriptor parameter, Object value) {
        if (valueHandler != null) {
            valueHandler.onChange(context, parameter.key(), value);
        }
    }

    private static boolean asBoolean(Object value) {
        if (value instanceof Boolean bool) {
            return bool;
        }
        return value != null && Boolean.parseBoolean(String.valueOf(value));
    }

    private static String asString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private static Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static Float parseFloat(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Float.parseFloat(value.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static String numericError(String value, boolean integer) {
        if (value == null || value.isBlank()) {
            return "";
        }
        try {
            if (integer) {
                Integer.parseInt(value.trim());
            } else {
                Float.parseFloat(value.trim());
            }
            return "";
        } catch (NumberFormatException ignored) {
            return integer ? "Expected integer" : "Expected number";
        }
    }

    private static String resourceIdError(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.matches("[a-z0-9_.-]+:[a-z0-9_./-]+") ? "" : "Expected namespace:path";
    }

    private static final class ChoiceControl extends SlateComponent {

        private static final SlateStyle DEFAULT_CHOICE_STYLE = SlateStyle.builder()
            .padding(Insets.symmetric(8, 6))
            .backgroundColor(0xFF0F172A)
            .hoverBackgroundColor(0xFF111C31)
            .activeBackgroundColor(0xFF1E293B)
            .border(new SlateBorder(0xFF475569, 1))
            .borderRadiusToken("radius.sm")
            .clipContent(true)
            .build();

        private final ParameterDescriptor parameter;
        private final ParameterValueHandler valueHandler;
        private String selectedValue;
        private int lineHeight = 9;

        private ChoiceControl(ParameterDescriptor parameter, String selectedValue, ParameterValueHandler valueHandler, SlateStyle style) {
            super(SlateStyle.withDefaults(DEFAULT_CHOICE_STYLE, style));
            this.parameter = parameter;
            this.valueHandler = valueHandler;
            this.selectedValue = selectedValue == null || selectedValue.isBlank()
                ? (parameter.options().isEmpty() ? "" : parameter.options().get(0))
                : selectedValue;
        }

        @Override
        public boolean focusable() {
            return true;
        }

        @Override
        public Size measure(SlateLayoutContext context, Size available) {
            lineHeight = context.lineHeight();
            int width = style().width() != null ? style().width() : Math.min(available.width(), 180);
            int height = style().height() != null ? style().height() : lineHeight + style().padding().vertical() + 8;
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
            String label = selectedValue.isBlank() ? "Select" : selectedValue;
            commands.add(new top.huliawsl.slateui.render.DrawTextCommand(content.x() + 2, content.y() + Math.max(0, (content.height() - lineHeight) / 2), label, resolveTextColor(context.theme())));
        }

        @Override
        public boolean mouseClicked(SlateInteractionContext context, double mouseX, double mouseY, int button) {
            if (style().disabled() || !bounds().contains(mouseX, mouseY)) {
                return false;
            }
            setPressed(true);
            context.requestFocus(this);
            context.requestInvalidation(InvalidationType.INTERACTION, "choice-press");
            return true;
        }

        @Override
        public boolean mouseReleased(SlateInteractionContext context, double mouseX, double mouseY, int button) {
            boolean wasPressed = isPressed();
            setPressed(false);
            if (!wasPressed || !bounds().contains(mouseX, mouseY)) {
                return wasPressed;
            }
            List<String> options = parameter.options();
            if (!options.isEmpty()) {
                int index = Math.max(0, options.indexOf(selectedValue));
                selectedValue = options.get((index + 1) % options.size());
                if (valueHandler != null) {
                    valueHandler.onChange(context, parameter.key(), selectedValue);
                }
            }
            context.requestInvalidation(InvalidationType.INTERACTION, "choice-cycle");
            return true;
        }
    }
}
