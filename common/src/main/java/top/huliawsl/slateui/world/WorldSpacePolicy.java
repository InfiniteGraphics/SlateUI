package top.huliawsl.slateui.world;

public record WorldSpacePolicy(
    WorldSpaceBillboardMode billboardMode,
    double distanceScale,
    WorldSpaceOcclusionPolicy occlusionPolicy,
    boolean frustumCulling,
    WorldSpaceAttachment attachment,
    boolean raycastInteraction,
    String multiplayerSyncPolicy,
    int maxDrawCommands
) {

    public WorldSpacePolicy {
        billboardMode = billboardMode == null ? WorldSpaceBillboardMode.CAMERA_FACING : billboardMode;
        distanceScale = Math.max(0.01D, distanceScale);
        occlusionPolicy = occlusionPolicy == null ? WorldSpaceOcclusionPolicy.HIDE_WHEN_OCCLUDED : occlusionPolicy;
        attachment = attachment == null ? WorldSpaceAttachment.none() : attachment;
        multiplayerSyncPolicy = multiplayerSyncPolicy == null ? "client-projected-server-authoritative-state" : multiplayerSyncPolicy;
        maxDrawCommands = Math.max(1, maxDrawCommands);
    }

    public static WorldSpacePolicy defaultPolicy() {
        return new WorldSpacePolicy(WorldSpaceBillboardMode.CAMERA_FACING, 1D, WorldSpaceOcclusionPolicy.HIDE_WHEN_OCCLUDED, true, WorldSpaceAttachment.none(), true, "client-projected-server-authoritative-state", 512);
    }
}
