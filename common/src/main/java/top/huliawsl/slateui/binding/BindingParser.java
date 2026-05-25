package top.huliawsl.slateui.binding;

public final class BindingParser {

    private BindingParser() {
    }

    public static String normalize(String expression) {
        String trimmed = expression == null ? "" : expression.trim();
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            return trimmed.substring(1, trimmed.length() - 1).trim();
        }
        return trimmed;
    }
}
