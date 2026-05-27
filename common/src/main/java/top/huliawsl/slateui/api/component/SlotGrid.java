package top.huliawsl.slateui.api.component;

import java.util.List;
import java.util.Map;
import top.huliawsl.slateui.api.SlateBorder;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.container.ContainerSlot;
import top.huliawsl.slateui.api.container.ContainerSlotProvider;
import top.huliawsl.slateui.layout.Insets;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawBorderCommand;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.DrawRectCommand;
import top.huliawsl.slateui.render.DrawTextCommand;
import top.huliawsl.slateui.runtime.SlateInteractionContext;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

public final class SlotGrid extends SlateComponent {

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
    private List<ContainerSlot> lastSlots = List.of();

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
            commands.add(new DrawRectCommand(slotRect, fill));
            commands.add(new DrawBorderCommand(slotRect, slot.empty() ? 0xFF475569 : 0xFF94A3B8, 1));
            if (!slot.empty()) {
                String label = shortItemName(slot.itemId());
                commands.add(new DrawTextCommand(slotRect.x() + 2, slotRect.y() + 2, label, resolveTextColor(context.theme())));
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
        if (slot == null || !slot.enabled()) {
            return bounds().contains(mouseX, mouseY);
        }
        if (clickCommand != null && !clickCommand.isBlank()) {
            boolean executed = context.commands().execute(clickCommand, context, Map.of(
                "slotIndex", slot.index(),
                "itemId", slot.itemId(),
                "count", slot.count(),
                "button", button
            ));
            context.commandLogger().accept((executed ? "EXEC " : "MISS ") + clickCommand + " slot=" + slot.index());
        }
        context.requestFocus(this);
        return true;
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

    private static String shortItemName(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return "";
        }
        int colon = itemId.lastIndexOf(':');
        String name = colon >= 0 ? itemId.substring(colon + 1) : itemId;
        return name.length() <= 3 ? name : name.substring(0, 3);
    }
}
