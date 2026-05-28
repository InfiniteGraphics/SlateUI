package top.huliawsl.slateui.override;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import top.huliawsl.slateui.api.Theme;
import top.huliawsl.slateui.api.ThemeTokens;

public final class SlateOverrideRegistry {

    public static final SlateOverrideRegistry EMPTY = new SlateOverrideRegistry();

    private final Map<String, JsonObject> componentOverrides = new LinkedHashMap<>();
    private ThemeTokens themeOverride = ThemeTokens.builder().build();

    public SlateOverrideRegistry registerComponentOverride(String resourcePath, JsonObject rootOverride) {
        JsonObject override = Objects.requireNonNull(rootOverride, "rootOverride");
        if (!override.has("componentType") || !override.get("componentType").isJsonPrimitive()) {
            throw new IllegalArgumentException("component override root must include componentType");
        }
        componentOverrides.put(normalize(resourcePath), override.deepCopy());
        return this;
    }

    public SlateOverrideRegistry registerThemeOverride(ThemeTokens tokens) {
        themeOverride = themeOverride.merge(tokens);
        return this;
    }

    public SlateOverrideRegistry registerThemeOverride(JsonObject themeObject) {
        if (themeObject == null) {
            return this;
        }
        ThemeTokens.Builder builder = ThemeTokens.builder();
        applyIntSection(themeObject.getAsJsonObject("colors"), builder::color);
        applyIntSection(themeObject.getAsJsonObject("spacing"), builder::spacing);
        applyIntSection(themeObject.getAsJsonObject("radii"), builder::radius);
        applyIntSection(themeObject.getAsJsonObject("radius"), builder::radius);
        return registerThemeOverride(builder.build());
    }

    public JsonObject applyComponentOverride(String resourcePath, JsonObject ir) {
        JsonObject copy = Objects.requireNonNull(ir, "ir").deepCopy();
        JsonObject override = componentOverrides.get(normalize(resourcePath));
        if (override != null) {
            copy.add("root", override.deepCopy());
        }
        return copy;
    }

    public Theme applyThemeOverride(Theme baseTheme) {
        Theme base = baseTheme == null ? Theme.DEFAULT : baseTheme;
        return base.merge(themeOverride);
    }

    public boolean hasComponentOverride(String resourcePath) {
        return componentOverrides.containsKey(normalize(resourcePath));
    }

    public boolean hasThemeOverride() {
        return !themeOverride.colors().isEmpty() || !themeOverride.spacing().isEmpty() || !themeOverride.radii().isEmpty();
    }

    private static String normalize(String resourcePath) {
        return resourcePath == null ? "" : resourcePath.replace('\\', '/');
    }

    private static void applyIntSection(JsonObject section, TokenConsumer consumer) {
        if (section == null) {
            return;
        }
        for (Map.Entry<String, JsonElement> entry : section.entrySet()) {
            consumer.put(entry.getKey(), parseInt(entry.getValue()));
        }
    }

    private static int parseInt(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return 0;
        }
        if (element.getAsJsonPrimitive().isNumber()) {
            return element.getAsInt();
        }
        String value = element.getAsString().trim();
        if (value.startsWith("#")) {
            long rgb = Long.parseLong(value.substring(1), 16);
            return value.length() <= 7 ? (int) (0xFF000000L | rgb) : (int) rgb;
        }
        if (value.startsWith("0x") || value.startsWith("0X")) {
            return (int) Long.parseLong(value.substring(2), 16);
        }
        return Integer.parseInt(value);
    }

    @FunctionalInterface
    private interface TokenConsumer {
        void put(String key, int value);
    }
}
