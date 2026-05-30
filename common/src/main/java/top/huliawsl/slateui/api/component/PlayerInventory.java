package top.huliawsl.slateui.api.component;

import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.container.ContainerSlotProvider;

public final class PlayerInventory extends SlotGrid {

    public PlayerInventory(ContainerSlotProvider provider, String clickCommand, SlateStyle style) {
        super(provider, 9, clickCommand, style);
    }
}
