package top.huliawsl.slateui.binding;

import java.util.ArrayList;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import top.huliawsl.slateui.api.SlateBinding;
import top.huliawsl.slateui.api.StateProvider;

public final class BindingEvaluator {

    private static final Pattern COMPARISON = Pattern.compile("(.+?)\\s*(==|!=|>=|<=|>|<)\\s*(.+)");

    private BindingEvaluator() {
    }

    public static SlateBinding<Object> compile(String rawExpression) {
        String expression = BindingParser.normalize(rawExpression);
        return new SlateBinding<>(expression, provider -> evaluate(expression, provider));
    }

    public static Object evaluate(String expression, StateProvider provider) {
        String trimmed = BindingParser.normalize(expression);
        if (trimmed.isEmpty()) {
            return "";
        }
        List<String> concatParts = splitOutsideQuotes(trimmed, '+');
        if (concatParts.size() > 1) {
            StringBuilder builder = new StringBuilder();
            for (String part : concatParts) {
                Object value = evaluate(part, provider);
                builder.append(value == null ? "null" : value);
            }
            return builder.toString();
        }
        Matcher matcher = COMPARISON.matcher(trimmed);
        if (matcher.matches()) {
            Object left = evaluate(matcher.group(1), provider);
            Object right = evaluate(matcher.group(3), provider);
            return compare(left, right, matcher.group(2));
        }
        if ((trimmed.startsWith("\"") && trimmed.endsWith("\"")) || (trimmed.startsWith("'") && trimmed.endsWith("'"))) {
            return trimmed.substring(1, trimmed.length() - 1);
        }
        if ("true".equals(trimmed)) {
            return true;
        }
        if ("false".equals(trimmed)) {
            return false;
        }
        if (trimmed.matches("-?\\d+")) {
            return Integer.parseInt(trimmed);
        }
        Object resolved = resolvePath(provider, trimmed);
        if (resolved != UNRESOLVED) {
            return resolved;
        }
        return trimmed;
    }

    public static boolean isTruthy(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof Number number) {
            return number.doubleValue() != 0D;
        }
        if (value instanceof String string) {
            return !string.isBlank() && !"false".equalsIgnoreCase(string);
        }
        if (value instanceof Iterable<?> iterable) {
            return iterable.iterator().hasNext();
        }
        if (value.getClass().isArray()) {
            return Array.getLength(value) > 0;
        }
        return true;
    }

    private static boolean compare(Object left, Object right, String operator) {
        if (left instanceof Number leftNumber && right instanceof Number rightNumber) {
            double leftValue = leftNumber.doubleValue();
            double rightValue = rightNumber.doubleValue();
            return switch (operator) {
                case "==" -> leftValue == rightValue;
                case "!=" -> leftValue != rightValue;
                case ">" -> leftValue > rightValue;
                case ">=" -> leftValue >= rightValue;
                case "<" -> leftValue < rightValue;
                case "<=" -> leftValue <= rightValue;
                default -> false;
            };
        }
        return switch (operator) {
            case "==" -> Objects.equals(left, right);
            case "!=" -> !Objects.equals(left, right);
            default -> false;
        };
    }

    private static List<String> splitOutsideQuotes(String input, char delimiter) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            if (currentChar == '\'' && !inDoubleQuote) {
                inSingleQuote = !inSingleQuote;
            } else if (currentChar == '"' && !inSingleQuote) {
                inDoubleQuote = !inDoubleQuote;
            }
            if (currentChar == delimiter && !inSingleQuote && !inDoubleQuote) {
                parts.add(current.toString().trim());
                current.setLength(0);
                continue;
            }
            current.append(currentChar);
        }
        if (!current.isEmpty()) {
            parts.add(current.toString().trim());
        }
        return parts;
    }

    private static final Object UNRESOLVED = new Object();

    private static Object resolvePath(StateProvider provider, String path) {
        if (provider.contains(path)) {
            return provider.get(path);
        }
        String[] parts = path.split("\\.");
        for (int index = parts.length - 1; index > 0; index--) {
            String head = String.join(".", Arrays.copyOf(parts, index));
            if (!provider.contains(head)) {
                continue;
            }
            Object current = provider.get(head);
            for (int tailIndex = index; tailIndex < parts.length; tailIndex++) {
                current = readProperty(current, parts[tailIndex]);
                if (current == UNRESOLVED) {
                    return UNRESOLVED;
                }
            }
            return current;
        }
        return UNRESOLVED;
    }

    private static Object readProperty(Object value, String property) {
        if (value == null) {
            return UNRESOLVED;
        }
        if (value instanceof Map<?, ?> map) {
            return map.containsKey(property) ? map.get(property) : UNRESOLVED;
        }
        if (value instanceof List<?> list && property.matches("\\d+")) {
            int index = Integer.parseInt(property);
            return index >= 0 && index < list.size() ? list.get(index) : UNRESOLVED;
        }
        try {
            return value.getClass().getField(property).get(value);
        } catch (ReflectiveOperationException ignored) {
        }
        try {
            String suffix = property.substring(0, 1).toUpperCase() + property.substring(1);
            return value.getClass().getMethod("get" + suffix).invoke(value);
        } catch (ReflectiveOperationException ignored) {
        }
        try {
            return value.getClass().getMethod(property).invoke(value);
        } catch (ReflectiveOperationException ignored) {
            return UNRESOLVED;
        }
    }
}
