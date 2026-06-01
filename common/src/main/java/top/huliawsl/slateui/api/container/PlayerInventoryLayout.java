package top.huliawsl.slateui.api.container;

import java.util.ArrayList;
import java.util.List;

public record PlayerInventoryLayout(
    int inventorySlateStart,
    int inventoryNativeStart,
    int hotbarSlateStart,
    int hotbarNativeStart,
    int columns,
    int slotSize,
    int slotGap
) {

    public static final int INVENTORY_ROWS = 3;
    public static final int HOTBAR_COUNT = 9;
    public static final int INVENTORY_COUNT = INVENTORY_ROWS * HOTBAR_COUNT;

    public PlayerInventoryLayout {
        inventorySlateStart = Math.max(0, inventorySlateStart);
        inventoryNativeStart = Math.max(0, inventoryNativeStart);
        hotbarSlateStart = Math.max(0, hotbarSlateStart);
        hotbarNativeStart = Math.max(0, hotbarNativeStart);
        columns = Math.max(1, columns);
        slotSize = Math.max(8, slotSize);
        slotGap = Math.max(0, slotGap);
    }

    public static PlayerInventoryLayout vanilla(int inventorySlateStart, int inventoryNativeStart) {
        return new PlayerInventoryLayout(
            inventorySlateStart,
            inventoryNativeStart,
            inventorySlateStart + INVENTORY_COUNT,
            inventoryNativeStart + INVENTORY_COUNT,
            HOTBAR_COUNT,
            18,
            2
        );
    }

    public List<NativeContainerSlot> slots() {
        List<NativeContainerSlot> slots = new ArrayList<>();
        for (int index = 0; index < INVENTORY_COUNT; index++) {
            slots.add(NativeContainerSlot.playerInventory(inventorySlateStart + index, inventoryNativeStart + index));
        }
        for (int index = 0; index < HOTBAR_COUNT; index++) {
            slots.add(NativeContainerSlot.hotbar(hotbarSlateStart + index, hotbarNativeStart + index));
        }
        return List.copyOf(slots);
    }
}
