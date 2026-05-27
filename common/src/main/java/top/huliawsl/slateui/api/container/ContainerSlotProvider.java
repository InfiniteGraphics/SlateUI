package top.huliawsl.slateui.api.container;

import java.util.List;

@FunctionalInterface
public interface ContainerSlotProvider {

    List<ContainerSlot> slots();

    static ContainerSlotProvider empty() {
        return List::of;
    }
}
