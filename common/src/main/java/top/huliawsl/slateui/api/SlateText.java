package top.huliawsl.slateui.api;

import java.util.List;

public sealed interface SlateText permits SlateText.Literal, SlateText.Translatable {

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
