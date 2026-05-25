package top.huliawsl.slateui.api;

import top.huliawsl.slateui.layout.Insets;

public final class SlateStyle {

    public static final SlateStyle EMPTY = SlateStyle.builder().build();

    private final Integer width;
    private final Integer height;
    private final Integer minWidth;
    private final Integer minHeight;
    private final Integer maxWidth;
    private final Integer maxHeight;
    private final Insets padding;
    private final Insets margin;
    private final int gap;
    private final String gapToken;
    private final Integer backgroundColor;
    private final String backgroundToken;
    private final Integer hoverBackgroundColor;
    private final String hoverBackgroundToken;
    private final Integer activeBackgroundColor;
    private final String activeBackgroundToken;
    private final SlateBorder border;
    private final String borderColorToken;
    private final SlateBorder focusBorder;
    private final String focusBorderColorToken;
    private final Integer textColor;
    private final String textColorToken;
    private final HorizontalAlign horizontalAlign;
    private final VerticalAlign verticalAlign;
    private final boolean disabled;
    private final boolean clipContent;

    private SlateStyle(Builder builder) {
        this.width = builder.width;
        this.height = builder.height;
        this.minWidth = builder.minWidth;
        this.minHeight = builder.minHeight;
        this.maxWidth = builder.maxWidth;
        this.maxHeight = builder.maxHeight;
        this.padding = builder.padding;
        this.margin = builder.margin;
        this.gap = builder.gap;
        this.gapToken = builder.gapToken;
        this.backgroundColor = builder.backgroundColor;
        this.backgroundToken = builder.backgroundToken;
        this.hoverBackgroundColor = builder.hoverBackgroundColor;
        this.hoverBackgroundToken = builder.hoverBackgroundToken;
        this.activeBackgroundColor = builder.activeBackgroundColor;
        this.activeBackgroundToken = builder.activeBackgroundToken;
        this.border = builder.border;
        this.borderColorToken = builder.borderColorToken;
        this.focusBorder = builder.focusBorder;
        this.focusBorderColorToken = builder.focusBorderColorToken;
        this.textColor = builder.textColor;
        this.textColorToken = builder.textColorToken;
        this.horizontalAlign = builder.horizontalAlign;
        this.verticalAlign = builder.verticalAlign;
        this.disabled = builder.disabled;
        this.clipContent = builder.clipContent;
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

    public Integer minWidth() {
        return minWidth;
    }

    public Integer minHeight() {
        return minHeight;
    }

    public Integer maxWidth() {
        return maxWidth;
    }

    public Integer maxHeight() {
        return maxHeight;
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

    public String gapToken() {
        return gapToken;
    }

    public Integer backgroundColor() {
        return backgroundColor;
    }

    public String backgroundToken() {
        return backgroundToken;
    }

    public Integer hoverBackgroundColor() {
        return hoverBackgroundColor;
    }

    public String hoverBackgroundToken() {
        return hoverBackgroundToken;
    }

    public Integer activeBackgroundColor() {
        return activeBackgroundColor;
    }

    public String activeBackgroundToken() {
        return activeBackgroundToken;
    }

    public SlateBorder border() {
        return border;
    }

    public String borderColorToken() {
        return borderColorToken;
    }

    public SlateBorder focusBorder() {
        return focusBorder;
    }

    public String focusBorderColorToken() {
        return focusBorderColorToken;
    }

    public Integer textColor() {
        return textColor;
    }

    public String textColorToken() {
        return textColorToken;
    }

    public HorizontalAlign horizontalAlign() {
        return horizontalAlign;
    }

    public VerticalAlign verticalAlign() {
        return verticalAlign;
    }

    public boolean disabled() {
        return disabled;
    }

    public boolean clipContent() {
        return clipContent;
    }

    public static final class Builder {

        private Integer width;
        private Integer height;
        private Integer minWidth;
        private Integer minHeight;
        private Integer maxWidth;
        private Integer maxHeight;
        private Insets padding = Insets.ZERO;
        private Insets margin = Insets.ZERO;
        private int gap;
        private String gapToken;
        private Integer backgroundColor;
        private String backgroundToken;
        private Integer hoverBackgroundColor;
        private String hoverBackgroundToken;
        private Integer activeBackgroundColor;
        private String activeBackgroundToken;
        private SlateBorder border = SlateBorder.NONE;
        private String borderColorToken;
        private SlateBorder focusBorder = SlateBorder.NONE;
        private String focusBorderColorToken;
        private Integer textColor = 0xFFFFFFFF;
        private String textColorToken;
        private HorizontalAlign horizontalAlign = HorizontalAlign.START;
        private VerticalAlign verticalAlign = VerticalAlign.START;
        private boolean disabled;
        private boolean clipContent;

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

        public Builder minWidth(int minWidth) {
            this.minWidth = minWidth;
            return this;
        }

        public Builder minHeight(int minHeight) {
            this.minHeight = minHeight;
            return this;
        }

        public Builder maxWidth(int maxWidth) {
            this.maxWidth = maxWidth;
            return this;
        }

        public Builder maxHeight(int maxHeight) {
            this.maxHeight = maxHeight;
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

        public Builder gapToken(String gapToken) {
            this.gapToken = gapToken;
            return this;
        }

        public Builder backgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder backgroundToken(String backgroundToken) {
            this.backgroundToken = backgroundToken;
            return this;
        }

        public Builder hoverBackgroundColor(int hoverBackgroundColor) {
            this.hoverBackgroundColor = hoverBackgroundColor;
            return this;
        }

        public Builder hoverBackgroundToken(String hoverBackgroundToken) {
            this.hoverBackgroundToken = hoverBackgroundToken;
            return this;
        }

        public Builder activeBackgroundColor(int activeBackgroundColor) {
            this.activeBackgroundColor = activeBackgroundColor;
            return this;
        }

        public Builder activeBackgroundToken(String activeBackgroundToken) {
            this.activeBackgroundToken = activeBackgroundToken;
            return this;
        }

        public Builder border(SlateBorder border) {
            this.border = border;
            return this;
        }

        public Builder borderColorToken(String borderColorToken) {
            this.borderColorToken = borderColorToken;
            return this;
        }

        public Builder focusBorder(SlateBorder focusBorder) {
            this.focusBorder = focusBorder;
            return this;
        }

        public Builder focusBorderColorToken(String focusBorderColorToken) {
            this.focusBorderColorToken = focusBorderColorToken;
            return this;
        }

        public Builder textColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        public Builder textColorToken(String textColorToken) {
            this.textColorToken = textColorToken;
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

        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder clipContent(boolean clipContent) {
            this.clipContent = clipContent;
            return this;
        }

        public SlateStyle build() {
            return new SlateStyle(this);
        }
    }
}
