package top.huliawsl.slateui.api;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ThemeTokens {

    private final Map<String, Integer> colors;
    private final Map<String, Integer> spacing;
    private final Map<String, Integer> radii;

    private ThemeTokens(Builder builder) {
        this.colors = Map.copyOf(builder.colors);
        this.spacing = Map.copyOf(builder.spacing);
        this.radii = Map.copyOf(builder.radii);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static ThemeTokens defaults() {
        return builder()
            .color("color.surface", 0xFF111827)
            .color("color.panel", 0xFF1F2937)
            .color("color.primary", 0xFF2563EB)
            .color("color.primaryHover", 0xFF3B82F6)
            .color("color.primaryActive", 0xFF1D4ED8)
            .color("color.text", 0xFFFFFFFF)
            .color("color.muted", 0xFFCBD5E1)
            .color("color.border", 0xFF334155)
            .spacing("spacing.xs", 4)
            .spacing("spacing.sm", 8)
            .spacing("spacing.md", 12)
            .spacing("spacing.lg", 16)
            .radius("radius.sm", 2)
            .radius("radius.md", 4)
            .radius("radius.lg", 8)
            .build();
    }

    public ThemeTokens merge(ThemeTokens override) {
        if (override == null) {
            return this;
        }
        Builder builder = builder();
        colors.forEach(builder::color);
        spacing.forEach(builder::spacing);
        radii.forEach(builder::radius);
        override.colors.forEach(builder::color);
        override.spacing.forEach(builder::spacing);
        override.radii.forEach(builder::radius);
        return builder.build();
    }

    public Map<String, Integer> colors() {
        return colors;
    }

    public Map<String, Integer> spacing() {
        return spacing;
    }

    public Map<String, Integer> radii() {
        return radii;
    }

    public Integer color(String key) {
        return colors.get(key);
    }

    public Integer spacing(String key) {
        return spacing.get(key);
    }

    public Integer radius(String key) {
        return radii.get(key);
    }

    public static final class Builder {

        private final Map<String, Integer> colors = new LinkedHashMap<>();
        private final Map<String, Integer> spacing = new LinkedHashMap<>();
        private final Map<String, Integer> radii = new LinkedHashMap<>();

        private Builder() {
        }

        public Builder color(String key, int value) {
            colors.put(key, value);
            return this;
        }

        public Builder spacing(String key, int value) {
            spacing.put(key, value);
            return this;
        }

        public Builder radius(String key, int value) {
            radii.put(key, value);
            return this;
        }

        public ThemeTokens build() {
            return new ThemeTokens(this);
        }
    }
}
