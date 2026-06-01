package top.huliawsl.slateui.api.container;

public record NativeContainerSlot(
    int slateIndex,
    int nativeIndex,
    NativeContainerSlotRole role,
    boolean insertAllowed,
    boolean extractAllowed,
    String validatorId
) {

    public NativeContainerSlot {
        slateIndex = Math.max(0, slateIndex);
        nativeIndex = Math.max(0, nativeIndex);
        role = role == null ? NativeContainerSlotRole.MACHINE : role;
        validatorId = validatorId == null ? "" : validatorId;
        if (role == NativeContainerSlotRole.OUTPUT) {
            insertAllowed = false;
            extractAllowed = true;
        }
    }

    public static NativeContainerSlot machine(int slateIndex, int nativeIndex) {
        return new NativeContainerSlot(slateIndex, nativeIndex, NativeContainerSlotRole.MACHINE, true, true, "");
    }

    public static NativeContainerSlot output(int slateIndex, int nativeIndex) {
        return new NativeContainerSlot(slateIndex, nativeIndex, NativeContainerSlotRole.OUTPUT, false, true, "");
    }

    public static NativeContainerSlot upgrade(int slateIndex, int nativeIndex, String validatorId) {
        return new NativeContainerSlot(slateIndex, nativeIndex, NativeContainerSlotRole.UPGRADE, true, true, validatorId);
    }

    public static NativeContainerSlot playerInventory(int slateIndex, int nativeIndex) {
        return new NativeContainerSlot(slateIndex, nativeIndex, NativeContainerSlotRole.PLAYER_INVENTORY, true, true, "");
    }

    public static NativeContainerSlot hotbar(int slateIndex, int nativeIndex) {
        return new NativeContainerSlot(slateIndex, nativeIndex, NativeContainerSlotRole.HOTBAR, true, true, "");
    }
}
