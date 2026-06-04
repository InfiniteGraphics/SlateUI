package top.huliawsl.slateui.style;

import java.util.ArrayList;
import java.util.List;
import top.huliawsl.slateui.api.SlateStyle;

public final class SlateStyleRegistry {

    private final List<SlateStyleRule> rules = new ArrayList<>();

    public SlateStyleRegistry register(SlateStyleRule rule) {
        if (rule != null) {
            rules.add(rule);
        }
        return this;
    }

    public SlateStyle resolve(String styleClass, String variant, SlatePseudoClass pseudoClass, SlateStyle fallback) {
        SlateStyle resolved = fallback == null ? SlateStyle.EMPTY : fallback;
        for (SlateStyleRule rule : rules) {
            if (rule.matches(styleClass, variant, pseudoClass)) {
                resolved = SlateStyle.withDefaults(resolved, rule.style());
            }
        }
        return resolved;
    }

    public List<SlateStyleRule> rules() {
        return List.copyOf(rules);
    }
}
