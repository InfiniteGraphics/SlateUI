package top.huliawsl.slateui.api.container;

@FunctionalInterface
public interface SlotValidator {

    SlotValidationResult validate(ContainerSlot slot, SlotClickType clickType);

    static SlotValidator allowAll() {
        return (slot, clickType) -> SlotValidationResult.ok();
    }
}
