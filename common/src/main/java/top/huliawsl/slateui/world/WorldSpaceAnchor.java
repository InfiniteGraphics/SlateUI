package top.huliawsl.slateui.world;

public record WorldSpaceAnchor(double x, double y, double z, int width, int height) {

    public WorldSpaceAnchor {
        width = Math.max(0, width);
        height = Math.max(0, height);
    }

    public WorldSpaceCoordinate coordinate() {
        return new WorldSpaceCoordinate(x, y, z);
    }

    public double distanceTo(WorldSpaceCoordinate camera) {
        WorldSpaceCoordinate origin = camera == null ? new WorldSpaceCoordinate(0, 0, 0) : camera;
        double dx = x - origin.x();
        double dy = y - origin.y();
        double dz = z - origin.z();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
