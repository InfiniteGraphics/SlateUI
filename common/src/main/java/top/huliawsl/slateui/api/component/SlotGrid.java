package top.huliawsl.slateui.api.component;

import java.util.List;
import java.util.Map;
import top.huliawsl.slateui.api.SlateBorder;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.container.ContainerSlot;
import top.huliawsl.slateui.api.container.ContainerSlotProvider;
import top.huliawsl.slateui.api.container.SlotClickType;
import top.huliawsl.slateui.api.container.SlotMode;
import top.huliawsl.slateui.api.container.SlotValidationResult;
import top.huliawsl.slateui.api.container.SlotValidator;
import top.huliawsl.slateui.debug.SlateRuntimeException;
import top.huliawsl.slateui.layout.Insets;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawBorderCommand;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.DrawItemIconCommand;
import top.huliawsl.slateui.render.DrawRectCommand;
import top.huliawsl.slateui.render.DrawTextCommand;
import top.huliawsl.slateui.runtime.InvalidationType;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

/**
 * Experimental component. It is available for testing container-style screens, but it is not part of the stable core component contract.
 */
public class SlotGrid extends SlateComponent {

    private static final int DEFAULT_SLOT_SIZE = 18;
    private static final int DEFAULT_GAP = 2;
    private static final SlateStyle DEFAULT_STYLE = SlateStyle.builder()
        .padding(Insets.all(4))
        .backgroundColor(0xFF0F172A)
        .border(new SlateBorder(0xFF334155, 1))
        .borderRadiusToken("radius.md")
        .build();

    private final ContainerSlotProvider provider;
    private final int columns;
    private final int slotSize;
    private final int slotGap;
    private final String clickCommand;
    private SlotMode slotMode = SlotMode.NORMAL;
    private SlotValidator slotValidator = SlotValidator.allowAll();
    private List<ContainerSlot> lastSlots = List.of();
    private int hoveredSlotIndex = -1;
    private long lastClickMillis;

    public SlotGrid(ContainerSlotProvider provider, int columns, String clickCommand, SlateStyle style) {
        this(provider, columns, DEFAULT_SLOT_SIZE, DEFAULT_GAP, clickCommand, style);
    }

    public SlotGrid(ContainerSlotProvider provider, int columns, int slotSize, int slotGap, String clickCommand, SlateStyle style) {
        super(SlateStyle.withDefaults(DEFAULT_STYLE, style));
        this.provider = provider == null ? ContainerSlotProvider.empty() : provider;
        this.columns = Math.max(1, columns);
        this.slotSize = Math.max(8, slotSize);
        this.slotGap = Math.max(0, slotGap);
        this.clickCommand = clickCommand;
    }

    public SlotGrid slotMode(SlotMode slotMode) {
        this.slotMode = slotMode == null ? SlotMode.NORMAL : slotMode;
        return this;
    }

    public SlotGrid slotValidator(SlotValidator slotValidator) {
        this.slotValidator = slotValidator == null ? SlotValidator.allowAll() : slotValidator;
        return this;
    }

    public String accessibilityDiagnostics() {
        return "slots=" + lastSlots.size() + " columns=" + columns + " mode=" + slotMode + " hovered=" + hoveredSlotIndex;
    }

    public String tooltipTextAt(double mouseX, double mouseY) {
        ContainerSlot slot = slotAt(mouseX, mouseY);
        if (slot == null || slot.empty()) {
            return "";
        }
        return slot.itemId() + " x" + slot.count();
    }

    @Override
    public Size measure(SlateLayoutContext context, Size available) {
        lastSlots = List.copyOf(provider.slots());
        int rows = lastSlots.isEmpty() ? 0 : (int) Math.ceil(lastSlots.size() / (double) columns);
        int width = columns * slotSize + Math.max(0, columns - 1) * slotGap;
        int height = rows * slotSize + Math.max(0, rows - 1) * slotGap;
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
        for (int i = 0; i < lastSlots.size(); i++) {
            ContainerSlot slot = lastSlots.get(i);
            Rect slotRect = slotRect(content, i);
            int fill = slot.enabled() ? 0xFF1E293B : 0xFF111827;
            if (slotMode == SlotMode.GHOST) {
                fill = 0x551E293B;
            } else if (slotMode == SlotMode.FILTER) {
                fill = 0xFF1E3A3A;
            } else if (slotMode == SlotMode.LOCKED) {
                fill = 0xFF262626;
            }
            commands.add(new DrawRectCommand(slotRect, fill));
            int borderColor = i == hoveredSlotIndex ? 0xFFFFFFFF : slot.empty() ? 0xFF475569 : 0xFF94A3B8;
            commands.add(new DrawBorderCommand(slotRect, borderColor, 1));
            if (!slot.empty()) {
                commands.add(new DrawItemIconCommand(slotRect, slot.itemId(), slot.count()));
                if (slot.count() > 1) {
                    commands.add(new DrawTextCommand(slotRect.right() - 8, slotRect.bottom() - 9, String.valueOf(slot.count()), 0xFFFFFFFF));
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(SlateInteractionContext context, double mouseX, double mouseY, int button) {
        if (style().disabled() || !bounds().contains(mouseX, mouseY)) {
            return false;
        }
        ContainerSlot slot = slotAt(mouseX, mouseY);
        SlotClickType clickType = clickType(button);
        long now = System.currentTimeMillis();
        if (now - lastClickMillis <= 350) {
            clickType = SlotClickType.DOUBLE_CLICK;
        }
        lastClickMillis = now;
        if (slot == null || !slot.enabled() || slotMode == SlotMode.LOCKED) {
            return bounds().contains(mouseX, mouseY);
        }
        SlotValidationResult validation = slotValidator.validate(slot, clickType);
        if (!validation.valid()) {
            context.logDiagnostic("SLOT validation failed component=" + debugPath() + " slot=" + slot.index() + " message=" + validation.message());
            return true;
        }
        if (clickCommand != null && !clickCommand.isBlank()) {
            try {
                boolean executed = context.commands().execute(clickCommand, context, Map.of(
                    "slotIndex", slot.index(),
                    "itemId", slot.itemId(),
                    "count", slot.count(),
                    "button", button,
                    "clickType", clickType.name(),
                    "mode", slotMode.name()
                ));
                context.commandLogger().accept((executed ? "EXEC " : "MISS ") + clickCommand + " component=" + debugPath() + " slot=" + slot.index());
                if (!executed) {
                    context.logDiagnostic("COMMAND missing id=" + clickCommand + " component=" + debugPath() + " slot=" + slot.index());
                }
            } catch (Throwable throwable) {
                throw SlateRuntimeException.command(this, clickCommand, throwable);
            }
        }
        context.requestFocus(this);
        return true;
    }

    @Override
    public boolean mouseMoved(SlateInteractionContext context, double mouseX, double mouseY) {
        ContainerSlot slot = slotAt(mouseX, mouseY);
        int nextHovered = slot == null ? -1 : slot.index();
        if (hoveredSlotIndex != nextHovered) {
            hoveredSlotIndex = nextHovered;
            context.requestInvalidation(InvalidationType.INTERACTION, "slot-hover");
        }
        return super.mouseMoved(context, mouseX, mouseY);
    }

    @Override
    public boolean focusable() {
        return true;
    }

    public ContainerSlot slotAt(double mouseX, double mouseY) {
        Rect content = contentRect(bounds());
        for (int i = 0; i < lastSlots.size(); i++) {
            if (slotRect(content, i).contains(mouseX, mouseY)) {
                return lastSlots.get(i);
            }
        }
        return null;
    }

    private Rect slotRect(Rect content, int index) {
        int row = index / columns;
        int column = index % columns;
        int x = content.x() + column * (slotSize + slotGap);
        int y = content.y() + row * (slotSize + slotGap);
        return new Rect(x, y, slotSize, slotSize);
    }

    private static SlotClickType clickType(int button) {
        if (button == 1) {
            return SlotClickType.RIGHT_CLICK;
        }
        if (button >= 2 && button <= 9) {
            return SlotClickType.NUMBER_KEY;
        }
        return SlotClickType.LEFT_CLICK;
    }
}
