package top.huliawsl.slateui.api.container;

public record ContainerSlot(int index, String itemId, int count, boolean enabled) {

    public ContainerSlot {
        itemId = itemId == null ? "" : itemId;
        count = Math.max(0, count);
    }

    public boolean empty() {
        return itemId.isBlank() || count == 0;
    }

    public static ContainerSlot empty(int index) {
        return new ContainerSlot(index, "", 0, true);
    }
}
