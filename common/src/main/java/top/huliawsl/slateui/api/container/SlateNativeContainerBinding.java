package top.huliawsl.slateui.api.container;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import top.huliawsl.slateui.server.SlateIntentSyncPolicy;

public final class SlateNativeContainerBinding {

    private final String menuId;
    private final Map<Integer, NativeContainerSlot> slotsBySlateIndex;
    private final PlayerInventoryLayout playerInventoryLayout;
    private final SlateIntentSyncPolicy syncPolicy;
    private final boolean creativeMode;

    private SlateNativeContainerBinding(Builder builder) {
        this.menuId = builder.menuId == null ? "" : builder.menuId;
        this.slotsBySlateIndex = Map.copyOf(builder.slotsBySlateIndex);
        this.playerInventoryLayout = builder.playerInventoryLayout;
        this.syncPolicy = builder.syncPolicy == null ? SlateIntentSyncPolicy.SERVER_AUTHORITATIVE : builder.syncPolicy;
        this.creativeMode = builder.creativeMode;
    }

    public static Builder builder(String menuId) {
        return new Builder(menuId);
    }

    public String menuId() {
        return menuId;
    }

    public List<NativeContainerSlot> slots() {
        return slotsBySlateIndex.values().stream()
            .sorted(Comparator.comparingInt(NativeContainerSlot::slateIndex))
            .toList();
    }

    public Optional<NativeContainerSlot> slot(int slateIndex) {
        return Optional.ofNullable(slotsBySlateIndex.get(slateIndex));
    }

    public PlayerInventoryLayout playerInventoryLayout() {
        return playerInventoryLayout;
    }

    public SlateIntentSyncPolicy syncPolicy() {
        return syncPolicy;
    }

    public boolean creativeMode() {
        return creativeMode;
    }

    public boolean serverAuthoritative() {
        return syncPolicy == SlateIntentSyncPolicy.SERVER_AUTHORITATIVE;
    }

    public Optional<NativeContainerClick> click(int slateIndex, SlotClickType clickType, int button, int hotbarIndex) {
        NativeContainerSlot slot = slotsBySlateIndex.get(slateIndex);
        if (slot == null) {
            return Optional.empty();
        }
        NativeContainerInteraction interaction = switch (clickType == null ? SlotClickType.LEFT_CLICK : clickType) {
            case RIGHT_CLICK -> NativeContainerInteraction.SPLIT_OR_PLACE_ONE;
            case SHIFT_CLICK -> NativeContainerInteraction.QUICK_MOVE;
            case NUMBER_KEY -> NativeContainerInteraction.HOTBAR_SWAP;
            case DRAG_SPLIT -> NativeContainerInteraction.DRAG_SPLIT;
            case DOUBLE_CLICK -> NativeContainerInteraction.PICKUP_ALL;
            case LEFT_CLICK -> creativeMode && button == 2
                ? NativeContainerInteraction.CREATIVE_CLONE
                : NativeContainerInteraction.PICKUP_OR_PLACE;
        };
        return Optional.of(new NativeContainerClick(
            slot.slateIndex(),
            slot.nativeIndex(),
            slot.role(),
            interaction,
            button,
            interaction == NativeContainerInteraction.HOTBAR_SWAP ? hotbarIndex : -1,
            serverAuthoritative(),
            slot.validatorId()
        ));
    }

    public Map<String, Object> describe() {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("menuId", menuId);
        values.put("slotCount", slotsBySlateIndex.size());
        values.put("syncPolicy", syncPolicy.name());
        values.put("creativeMode", creativeMode);
        values.put("serverAuthoritative", serverAuthoritative());
        values.put("hasPlayerInventory", playerInventoryLayout != null);
        return Map.copyOf(values);
    }

    public static final class Builder {

        private final String menuId;
        private final Map<Integer, NativeContainerSlot> slotsBySlateIndex = new LinkedHashMap<>();
        private PlayerInventoryLayout playerInventoryLayout;
        private SlateIntentSyncPolicy syncPolicy = SlateIntentSyncPolicy.SERVER_AUTHORITATIVE;
        private boolean creativeMode;

        private Builder(String menuId) {
            this.menuId = menuId;
        }

        public Builder slot(NativeContainerSlot slot) {
            if (slot != null) {
                slotsBySlateIndex.put(slot.slateIndex(), slot);
            }
            return this;
        }

        public Builder slots(List<NativeContainerSlot> slots) {
            for (NativeContainerSlot slot : slots == null ? List.<NativeContainerSlot>of() : slots) {
                slot(slot);
            }
            return this;
        }

        public Builder range(NativeContainerSlotRange range) {
            if (range != null) {
                slots(range.slots());
            }
            return this;
        }

        public Builder playerInventory(PlayerInventoryLayout layout) {
            this.playerInventoryLayout = layout;
            if (layout != null) {
                slots(layout.slots());
            }
            return this;
        }

        public Builder serverAuthoritative() {
            this.syncPolicy = SlateIntentSyncPolicy.SERVER_AUTHORITATIVE;
            return this;
        }

        public Builder syncPolicy(SlateIntentSyncPolicy syncPolicy) {
            this.syncPolicy = syncPolicy == null ? SlateIntentSyncPolicy.SERVER_AUTHORITATIVE : syncPolicy;
            return this;
        }

        public Builder creativeMode(boolean creativeMode) {
            this.creativeMode = creativeMode;
            return this;
        }

        public SlateNativeContainerBinding build() {
            return new SlateNativeContainerBinding(this);
        }
    }
}
