package top.huliawsl.slateui.api.container;

import java.util.Map;

public record NativeContainerClick(
    int slateSlotIndex,
    int nativeSlotIndex,
    NativeContainerSlotRole role,
    NativeContainerInteraction interaction,
    int button,
    int hotbarIndex,
    boolean serverAuthoritative,
    String validatorId
) {

    public NativeContainerClick {
        role = role == null ? NativeContainerSlotRole.MACHINE : role;
        interaction = interaction == null ? NativeContainerInteraction.PICKUP_OR_PLACE : interaction;
        hotbarIndex = hotbarIndex < 0 || hotbarIndex > 8 ? -1 : hotbarIndex;
        validatorId = validatorId == null ? "" : validatorId;
    }

    public Map<String, Object> payload() {
        return Map.of(
            "slateSlotIndex", slateSlotIndex,
            "nativeSlotIndex", nativeSlotIndex,
            "role", role.name(),
            "interaction", interaction.name(),
            "button", button,
            "hotbarIndex", hotbarIndex,
            "serverAuthoritative", serverAuthoritative,
            "validatorId", validatorId
        );
    }
}
