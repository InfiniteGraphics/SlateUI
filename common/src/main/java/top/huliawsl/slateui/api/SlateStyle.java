package top.huliawsl.slateui.api;

import top.huliawsl.slateui.layout.Insets;

public final class SlateStyle {

    public static final SlateStyle EMPTY = SlateStyle.builder().build();

    private final Integer width;
    private final Integer height;
    private final Insets padding;
    private final Insets margin;
    private final int gap;
    private final Integer backgroundColor;
    private final SlateBorder border;
    private final int textColor;
    private final HorizontalAlign horizontalAlign;
    private final VerticalAlign verticalAlign;

    private SlateStyle(Builder builder) {
        this.width = builder.width;
        this.height = builder.height;
        this.padding = builder.padding;
        this.margin = builder.margin;
        this.gap = builder.gap;
        this.backgroundColor = builder.backgroundColor;
        this.border = builder.border;
        this.textColor = builder.textColor;
        this.horizontalAlign = builder.horizontalAlign;
        this.verticalAlign = builder.verticalAlign;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Integer width() {
        return width;
    }

    public Integer height() {
        return height;
    }

    public Insets padding() {
        return padding;
    }

    public Insets margin() {
        return margin;
    }

    public int gap() {
        return gap;
    }

    public Integer backgroundColor() {
        return backgroundColor;
    }

    public SlateBorder border() {
        return border;
    }

    public int textColor() {
        return textColor;
    }

    public HorizontalAlign horizontalAlign() {
        return horizontalAlign;
    }

    public VerticalAlign verticalAlign() {
        return verticalAlign;
    }

    public static final class Builder {

        private Integer width;
        private Integer height;
        private Insets padding = Insets.ZERO;
        private Insets margin = Insets.ZERO;
        private int gap;
        private Integer backgroundColor;
        private SlateBorder border = SlateBorder.NONE;
        private int textColor = 0xFFFFFFFF;
        private HorizontalAlign horizontalAlign = HorizontalAlign.START;
        private VerticalAlign verticalAlign = VerticalAlign.START;

        private Builder() {
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder padding(Insets padding) {
            this.padding = padding;
            return this;
        }

        public Builder margin(Insets margin) {
            this.margin = margin;
            return this;
        }

        public Builder gap(int gap) {
            this.gap = Math.max(0, gap);
            return this;
        }

        public Builder backgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder border(SlateBorder border) {
            this.border = border;
            return this;
        }

        public Builder textColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        public Builder horizontalAlign(HorizontalAlign horizontalAlign) {
            this.horizontalAlign = horizontalAlign;
            return this;
        }

        public Builder verticalAlign(VerticalAlign verticalAlign) {
            this.verticalAlign = verticalAlign;
            return this;
        }

        public SlateStyle build() {
            return new SlateStyle(this);
        }
    }
}
