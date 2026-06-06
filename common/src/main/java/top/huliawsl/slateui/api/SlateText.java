package top.huliawsl.slateui.api;

import java.util.Arrays;
import java.util.List;

public sealed interface SlateText permits SlateText.Literal, SlateText.Translatable {

    static SlateText literal(String value) {
        return new Literal(value);
    }

    static SlateText translatable(String key, Object... args) {
        return new Translatable(key, args == null ? List.of() : Arrays.asList(args));
    }

    String fallbackText();

    record Literal(String value) implements SlateText {
        @Override
        public String fallbackText() {
            return value == null ? "" : value;
        }
    }

    record Translatable(String key, List<Object> args) implements SlateText {
        public Translatable {
            args = args == null ? List.of() : List.copyOf(args);
        }

        @Override
        public String fallbackText() {
            return key == null ? "" : key;
        }
    }
}
