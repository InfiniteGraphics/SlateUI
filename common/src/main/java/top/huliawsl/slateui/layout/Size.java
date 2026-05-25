package top.huliawsl.slateui.layout;

public record Size(int width, int height) {

    public static final Size ZERO = new Size(0, 0);

    public Size {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size must be >= 0");
        }
    }

    public Size withWidth(int width) {
        return new Size(width, height);
    }

    public Size withHeight(int height) {
        return new Size(width, height);
    }

    public Size expand(int horizontal, int vertical) {
        return new Size(width + horizontal, height + vertical);
    }

    public Size clamp(Size max) {
        return new Size(Math.min(width, max.width), Math.min(height, max.height));
    }

    public Size clamp(Size min, Size max) {
        return new Size(
            Math.max(min.width, Math.min(width, max.width)),
            Math.max(min.height, Math.min(height, max.height))
        );
    }
}
