package top.huliawsl.slateui.world;

public record WorldSpaceAnchor(double x, double y, double z, int width, int height) {

    public WorldSpaceAnchor {
        width = Math.max(0, width);
        height = Math.max(0, height);
    }
}
