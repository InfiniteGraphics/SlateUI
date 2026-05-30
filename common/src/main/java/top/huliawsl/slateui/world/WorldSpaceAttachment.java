package top.huliawsl.slateui.world;

public record WorldSpaceAttachment(Type type, String targetId, WorldSpaceCoordinate offset) {

    public enum Type {
        NONE,
        ENTITY,
        BLOCK
    }

    public static WorldSpaceAttachment none() {
        return new WorldSpaceAttachment(Type.NONE, "", new WorldSpaceCoordinate(0, 0, 0));
    }

    public static WorldSpaceAttachment entity(String entityId, WorldSpaceCoordinate offset) {
        return new WorldSpaceAttachment(Type.ENTITY, entityId, offset);
    }

    public static WorldSpaceAttachment block(String blockPos, WorldSpaceCoordinate offset) {
        return new WorldSpaceAttachment(Type.BLOCK, blockPos, offset);
    }
}
