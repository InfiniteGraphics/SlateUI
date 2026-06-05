package top.huliawsl.slateui.platform;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record MinecraftVersionRange(String minInclusive, String maxExclusive) {

    public MinecraftVersionRange {
        Objects.requireNonNull(minInclusive, "minInclusive");
        Objects.requireNonNull(maxExclusive, "maxExclusive");
    }

    public static MinecraftVersionRange of(String minInclusive, String maxExclusive) {
        return new MinecraftVersionRange(minInclusive, maxExclusive);
    }

    public boolean contains(String version) {
        return compare(version, minInclusive) >= 0 && compare(version, maxExclusive) < 0;
    }

    static int compare(String left, String right) {
        Version a = Version.parse(left);
        Version b = Version.parse(right);
        int length = Math.max(a.parts().size(), b.parts().size());
        for (int i = 0; i < length; i++) {
            int av = i < a.parts().size() ? a.parts().get(i) : 0;
            int bv = i < b.parts().size() ? b.parts().get(i) : 0;
            if (av != bv) {
                return Integer.compare(av, bv);
            }
        }
        return Integer.compare(a.preReleaseWeight(), b.preReleaseWeight());
    }

    record Version(List<Integer> parts, int preReleaseWeight) {
        static Version parse(String raw) {
            if (raw == null || raw.isBlank()) {
                return new Version(List.of(0), 0);
            }
            String normalized = raw.trim().toLowerCase(java.util.Locale.ROOT);
            int weight = 0;
            if (normalized.contains("-pre")) {
                weight = -20;
            } else if (normalized.contains("-rc")) {
                weight = -10;
            } else if (normalized.contains("snapshot") || normalized.matches(".*\\d{2}w\\d{2}.*")) {
                weight = -30;
            }
            int suffix = normalized.indexOf('-');
            if (suffix >= 0) {
                normalized = normalized.substring(0, suffix);
            }
            List<Integer> values = new ArrayList<>();
            for (String part : normalized.split("\\.")) {
                String digits = leadingDigits(part);
                if (!digits.isBlank()) {
                    values.add(Integer.parseInt(digits));
                }
            }
            if (values.isEmpty()) {
                values.add(0);
            }
            return new Version(List.copyOf(values), weight);
        }

        private static String leadingDigits(String value) {
            StringBuilder builder = new StringBuilder();
            for (int index = 0; index < value.length(); index++) {
                char character = value.charAt(index);
                if (!Character.isDigit(character)) {
                    break;
                }
                builder.append(character);
            }
            return builder.toString();
        }
    }
}
