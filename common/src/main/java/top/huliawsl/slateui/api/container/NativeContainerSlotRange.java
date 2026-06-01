package top.huliawsl.slateui.api.container;

import java.util.ArrayList;
import java.util.List;

public record NativeContainerSlotRange(
    int slateStart,
    int nativeStart,
    int count,
    NativeContainerSlotRole role,
    boolean insertAllowed,
    boolean extractAllowed,
    String validatorId
) {

    public NativeContainerSlotRange {
        slateStart = Math.max(0, slateStart);
        nativeStart = Math.max(0, nativeStart);
        count = Math.max(0, count);
        role = role == null ? NativeContainerSlotRole.MACHINE : role;
        validatorId = validatorId == null ? "" : validatorId;
        if (role == NativeContainerSlotRole.OUTPUT) {
            insertAllowed = false;
            extractAllowed = true;
        }
    }

    public List<NativeContainerSlot> slots() {
        List<NativeContainerSlot> slots = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            slots.add(new NativeContainerSlot(
                slateStart + index,
                nativeStart + index,
                role,
                insertAllowed,
                extractAllowed,
                validatorId
            ));
        }
        return List.copyOf(slots);
    }
}
