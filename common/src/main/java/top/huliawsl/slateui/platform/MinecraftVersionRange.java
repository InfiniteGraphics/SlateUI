package top.huliawsl.slateui.platform;

import java.util.Objects;

public record MinecraftVersionRange(String minInclusive, String maxExclusive) {

    public MinecraftVersionRange {
        Objects.requireNonNull(minInclusive, "minInclusive");
        Objects.requireNonNull(maxExclusive, "maxExclusive");
    }

    public boolean contains(String version) {
        return compare(version, minInclusive) >= 0 && compare(version, maxExclusive) < 0;
    }

    static int compare(String left, String right) {
        int[] a = parse(left);
        int[] b = parse(right);
        int length = Math.max(a.length, b.length);
        for (int i = 0; i < length; i++) {
            int av = i < a.length ? a[i] : 0;
            int bv = i < b.length ? b[i] : 0;
            if (av != bv) {
                return Integer.compare(av, bv);
            }
        }
        return 0;
    }

    private static int[] parse(String version) {
        String normalized = version == null ? "0" : version.replaceAll("[^0-9.]", "");
        if (normalized.isBlank()) {
            return new int[] {0};
        }
        String[] parts = normalized.split("\\.");
        int[] values = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            values[i] = parts[i].isBlank() ? 0 : Integer.parseInt(parts[i]);
        }
        return values;
    }
}
