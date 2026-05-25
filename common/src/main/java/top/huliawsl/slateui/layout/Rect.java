package top.huliawsl.slateui.layout;

public record Rect(int x, int y, int width, int height) {

    public static final Rect ZERO = new Rect(0, 0, 0, 0);

    public Rect {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Rect size must be >= 0");
        }
    }

    public int right() {
        return x + width;
    }

    public int bottom() {
        return y + height;
    }

    public boolean contains(double mouseX, double mouseY) {
        return mouseX >= x && mouseX < right() && mouseY >= y && mouseY < bottom();
    }

    public Rect inset(Insets insets) {
        int nextWidth = Math.max(0, width - insets.horizontal());
        int nextHeight = Math.max(0, height - insets.vertical());
        return new Rect(x + insets.left(), y + insets.top(), nextWidth, nextHeight);
    }

    public Rect translate(int offsetX, int offsetY) {
        return new Rect(x + offsetX, y + offsetY, width, height);
    }
}
