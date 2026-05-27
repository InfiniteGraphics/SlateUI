package top.huliawsl.slateui.api;

import java.util.Objects;

public final class Theme {

    public static final Theme DEFAULT = new Theme(ThemeTokens.defaults());

    private final ThemeTokens tokens;

    public Theme(ThemeTokens tokens) {
        this.tokens = Objects.requireNonNull(tokens, "tokens");
    }

    public ThemeTokens tokens() {
        return tokens;
    }

    public Theme merge(ThemeTokens overrideTokens) {
        return new Theme(tokens.merge(overrideTokens));
    }

    public int resolveColor(Integer directValue, String token, int fallback) {
        if (directValue != null) {
            return directValue;
        }
        Integer tokenValue = token == null ? null : tokens.color(token);
        return tokenValue != null ? tokenValue : fallback;
    }

    public int resolveSpacing(Integer directValue, String token, int fallback) {
        if (directValue != null) {
            return directValue;
        }
        Integer tokenValue = token == null ? null : tokens.spacing(token);
        return tokenValue != null ? tokenValue : fallback;
    }

    public int resolveRadius(Integer directValue, String token, int fallback) {
        if (directValue != null) {
            return directValue;
        }
        Integer tokenValue = token == null ? null : tokens.radius(token);
        return tokenValue != null ? tokenValue : fallback;
    }
}
