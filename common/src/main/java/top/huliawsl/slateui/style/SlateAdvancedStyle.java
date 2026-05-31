package top.huliawsl.slateui.style;

import java.util.Map;

public record SlateAdvancedStyle(
    SlateShadow shadow,
    SlateGradient gradient,
    float opacity,
    boolean blurRequested,
    String textureBackground,
    String nineSliceTexture,
    boolean animatedTransitions,
    Map<String, String> variables,
    Map<SlatePseudoClass, String> pseudoClassStyles,
    SlateMediaRule mediaRule,
    SlateThemeVariant themeVariant
) {

    public SlateAdvancedStyle {
        opacity = Math.max(0F, Math.min(1F, opacity));
        variables = variables == null ? Map.of() : Map.copyOf(variables);
        pseudoClassStyles = pseudoClassStyles == null ? Map.of() : Map.copyOf(pseudoClassStyles);
        themeVariant = themeVariant == null ? SlateThemeVariant.DARK : themeVariant;
    }
}
