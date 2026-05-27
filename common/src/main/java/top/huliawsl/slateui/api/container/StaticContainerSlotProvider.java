package top.huliawsl.slateui.api.container;

import java.util.List;

public final class StaticContainerSlotProvider implements ContainerSlotProvider {

    private final List<ContainerSlot> slots;

    public StaticContainerSlotProvider(List<ContainerSlot> slots) {
        this.slots = slots == null ? List.of() : List.copyOf(slots);
    }

    @Override
    public List<ContainerSlot> slots() {
        return slots;
    }
}
