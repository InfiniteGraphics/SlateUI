package top.huliawsl.slateui.api;

import java.util.ArrayList;
import java.util.List;
import top.huliawsl.slateui.layout.Insets;

public final class SlateStyle {

    public static final SlateStyle EMPTY = SlateStyle.builder().build();

    private final String styleClass;
    private final String variant;
    private final Integer width;
    private final Integer height;
    private final Integer minWidth;
    private final Integer minHeight;
    private final Integer maxWidth;
    private final Integer maxHeight;
    private final Insets padding;
    private final Integer gap;
    private final String gapToken;
    private final Integer backgroundColor;
    private final String backgroundToken;
    private final Integer hoverBackgroundColor;
    private final String hoverBackgroundToken;
    private final Integer activeBackgroundColor;
    private final String activeBackgroundToken;
    private final SlateBorder border;
    private final String borderColorToken;
    private final Integer borderRadius;
    private final String borderRadiusToken;
    private final SlateBorder focusBorder;
    private final String focusBorderColorToken;
    private final Integer textColor;
    private final String textColorToken;
    private final HorizontalAlign horizontalAlign;
    private final VerticalAlign verticalAlign;
    private final Boolean disabled;
    private final Boolean clipContent;

    private SlateStyle(Builder builder) {
        this.styleClass = builder.styleClass;
        this.variant = builder.variant;
        this.width = builder.width;
        this.height = builder.height;
        this.minWidth = builder.minWidth;
        this.minHeight = builder.minHeight;
        this.maxWidth = builder.maxWidth;
        this.maxHeight = builder.maxHeight;
        this.padding = builder.padding;
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
        this.borderRadius = builder.borderRadius;
        this.borderRadiusToken = builder.borderRadiusToken;
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

    public static SlateStyle withDefaults(SlateStyle defaults, SlateStyle override) {
        if (defaults == null) {
            return override == null ? EMPTY : override;
        }
        if (override == null || override == EMPTY) {
            return defaults;
        }
        Builder builder = builder();
        builder.styleClass = override.styleClass != null ? override.styleClass : defaults.styleClass;
        builder.variant = override.variant != null ? override.variant : defaults.variant;
        builder.width = override.width != null ? override.width : defaults.width;
        builder.height = override.height != null ? override.height : defaults.height;
        builder.minWidth = override.minWidth != null ? override.minWidth : defaults.minWidth;
        builder.minHeight = override.minHeight != null ? override.minHeight : defaults.minHeight;
        builder.maxWidth = override.maxWidth != null ? override.maxWidth : defaults.maxWidth;
        builder.maxHeight = override.maxHeight != null ? override.maxHeight : defaults.maxHeight;
        builder.padding = override.padding != null ? override.padding : defaults.padding;
        builder.gap = override.gap != null ? override.gap : defaults.gap;
        builder.gapToken = override.gapToken != null ? override.gapToken : defaults.gapToken;
        builder.backgroundColor = override.backgroundColor != null ? override.backgroundColor : defaults.backgroundColor;
        builder.backgroundToken = override.backgroundToken != null ? override.backgroundToken : defaults.backgroundToken;
        builder.hoverBackgroundColor = override.hoverBackgroundColor != null ? override.hoverBackgroundColor : defaults.hoverBackgroundColor;
        builder.hoverBackgroundToken = override.hoverBackgroundToken != null ? override.hoverBackgroundToken : defaults.hoverBackgroundToken;
        builder.activeBackgroundColor = override.activeBackgroundColor != null ? override.activeBackgroundColor : defaults.activeBackgroundColor;
        builder.activeBackgroundToken = override.activeBackgroundToken != null ? override.activeBackgroundToken : defaults.activeBackgroundToken;
        builder.border = override.border != null ? override.border : defaults.border;
        builder.borderColorToken = override.borderColorToken != null ? override.borderColorToken : defaults.borderColorToken;
        builder.borderRadius = override.borderRadius != null ? override.borderRadius : defaults.borderRadius;
        builder.borderRadiusToken = override.borderRadiusToken != null ? override.borderRadiusToken : defaults.borderRadiusToken;
        builder.focusBorder = override.focusBorder != null ? override.focusBorder : defaults.focusBorder;
        builder.focusBorderColorToken = override.focusBorderColorToken != null ? override.focusBorderColorToken : defaults.focusBorderColorToken;
        builder.textColor = override.textColor != null ? override.textColor : defaults.textColor;
        builder.textColorToken = override.textColorToken != null ? override.textColorToken : defaults.textColorToken;
        builder.horizontalAlign = override.horizontalAlign != null ? override.horizontalAlign : defaults.horizontalAlign;
        builder.verticalAlign = override.verticalAlign != null ? override.verticalAlign : defaults.verticalAlign;
        builder.disabled = override.disabled != null ? override.disabled : defaults.disabled;
        builder.clipContent = override.clipContent != null ? override.clipContent : defaults.clipContent;
        return builder.build();
    }

    public String styleClass() {
        return styleClass;
    }

    public String variant() {
        return variant;
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
        return padding == null ? Insets.ZERO : padding;
    }

    public int gap() {
        return gap == null ? 0 : gap;
    }

    public Integer directGap() {
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
        return border == null ? SlateBorder.NONE : border;
    }

    public String borderColorToken() {
        return borderColorToken;
    }

    public Integer borderRadius() {
        return borderRadius;
    }

    public String borderRadiusToken() {
        return borderRadiusToken;
    }

    public SlateBorder focusBorder() {
        return focusBorder == null ? SlateBorder.NONE : focusBorder;
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
        return horizontalAlign == null ? HorizontalAlign.START : horizontalAlign;
    }

    public VerticalAlign verticalAlign() {
        return verticalAlign == null ? VerticalAlign.START : verticalAlign;
    }

    public boolean disabled() {
        return disabled != null && disabled;
    }

    public boolean clipContent() {
        return clipContent != null && clipContent;
    }


    public String describe(Theme theme) {
        Theme resolvedTheme = theme == null ? Theme.DEFAULT : theme;
        int resolvedBackground = resolvedTheme.resolveColor(backgroundColor, backgroundToken, Integer.MIN_VALUE);
        int resolvedText = resolvedTheme.resolveColor(textColor, textColorToken, 0xFFFFFFFF);
        int resolvedRadius = resolvedTheme.resolveRadius(borderRadius, borderRadiusToken, 0);
        int resolvedGap = resolvedTheme.resolveSpacing(gap, gapToken, gap());
        String background = resolvedBackground == Integer.MIN_VALUE ? "<none>" : String.format("#%08X", resolvedBackground);
        return "class=" + (styleClass == null ? "<none>" : styleClass)
            + " variant=" + (variant == null ? "<none>" : variant)
            + " size=" + nullable(width) + "x" + nullable(height)
            + " min=" + nullable(minWidth) + "x" + nullable(minHeight)
            + " max=" + nullable(maxWidth) + "x" + nullable(maxHeight)
            + " padding=" + padding()
            + " gap=" + resolvedGap + originSuffix(gapToken)
            + " background=" + background + originSuffix(backgroundToken)
            + " border=" + border().thickness() + "/" + String.format("#%08X", border().color()) + originSuffix(borderColorToken)
            + " radius=" + resolvedRadius + originSuffix(borderRadiusToken)
            + " focusBorder=" + focusBorder().thickness()
            + " text=" + String.format("#%08X", resolvedText) + originSuffix(textColorToken)
            + " align=" + horizontalAlign() + "/" + verticalAlign()
            + " clip=" + clipContent()
            + " disabled=" + disabled();
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (width != null && width < 0) {
            errors.add("width must be >= 0");
        }
        if (height != null && height < 0) {
            errors.add("height must be >= 0");
        }
        if (minWidth != null && minWidth < 0) {
            errors.add("minWidth must be >= 0");
        }
        if (minHeight != null && minHeight < 0) {
            errors.add("minHeight must be >= 0");
        }
        if (maxWidth != null && maxWidth < 0) {
            errors.add("maxWidth must be >= 0");
        }
        if (maxHeight != null && maxHeight < 0) {
            errors.add("maxHeight must be >= 0");
        }
        if (minWidth != null && maxWidth != null && minWidth > maxWidth) {
            errors.add("minWidth must be <= maxWidth");
        }
        if (minHeight != null && maxHeight != null && minHeight > maxHeight) {
            errors.add("minHeight must be <= maxHeight");
        }
        return List.copyOf(errors);
    }

    public String diff(SlateStyle other, Theme theme) {
        SlateStyle right = other == null ? EMPTY : other;
        String before = describe(theme);
        String after = right.describe(theme);
        return before.equals(after) ? "<none>" : "before=" + before + "\nafter=" + after;
    }

    private static String nullable(Integer value) {
        return value == null ? "auto" : String.valueOf(value);
    }

    private static String originSuffix(String token) {
        return token == null || token.isBlank() ? "" : " token=" + token;
    }

    public static final class Builder {

        private String styleClass;
        private String variant;
        private Integer width;
        private Integer height;
        private Integer minWidth;
        private Integer minHeight;
        private Integer maxWidth;
        private Integer maxHeight;
        private Insets padding;
        private Integer gap;
        private String gapToken;
        private Integer backgroundColor;
        private String backgroundToken;
        private Integer hoverBackgroundColor;
        private String hoverBackgroundToken;
        private Integer activeBackgroundColor;
        private String activeBackgroundToken;
        private SlateBorder border;
        private String borderColorToken;
        private Integer borderRadius;
        private String borderRadiusToken;
        private SlateBorder focusBorder;
        private String focusBorderColorToken;
        private Integer textColor;
        private String textColorToken;
        private HorizontalAlign horizontalAlign;
        private VerticalAlign verticalAlign;
        private Boolean disabled;
        private Boolean clipContent;

        private Builder() {
        }

        public Builder styleClass(String styleClass) {
            this.styleClass = styleClass == null || styleClass.isBlank() ? null : styleClass;
            return this;
        }

        public Builder variant(String variant) {
            this.variant = variant == null || variant.isBlank() ? null : variant;
            return this;
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

        public Builder borderRadius(int borderRadius) {
            this.borderRadius = Math.max(0, borderRadius);
            return this;
        }

        public Builder borderRadiusToken(String borderRadiusToken) {
            this.borderRadiusToken = borderRadiusToken;
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
