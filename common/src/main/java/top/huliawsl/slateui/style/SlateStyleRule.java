package top.huliawsl.slateui.style;

import top.huliawsl.slateui.api.SlateStyle;

public record SlateStyleRule(String styleClass, String variant, SlatePseudoClass pseudoClass, SlateStyle style) {
    public SlateStyleRule {
        styleClass = styleClass == null ? "" : styleClass;
        variant = variant == null ? "" : variant;
        style = style == null ? SlateStyle.EMPTY : style;
    }

    public boolean matches(String candidateClass, String candidateVariant, SlatePseudoClass candidatePseudoClass) {
        boolean classMatches = styleClass.isBlank() || styleClass.equals(candidateClass);
        boolean variantMatches = variant.isBlank() || variant.equals(candidateVariant);
        boolean pseudoMatches = pseudoClass == null || pseudoClass == candidatePseudoClass;
        return classMatches && variantMatches && pseudoMatches;
    }
}
