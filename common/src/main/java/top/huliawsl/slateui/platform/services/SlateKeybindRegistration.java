package top.huliawsl.slateui.platform.services;

import java.util.Objects;

public record SlateKeybindRegistration(String id, String translationKey, int defaultKeyCode, String category, Runnable handler) {
    public SlateKeybindRegistration {
        id = Objects.requireNonNull(id, "id");
        translationKey = translationKey == null || translationKey.isBlank() ? id : translationKey;
        category = category == null || category.isBlank() ? "key.categories.slateui" : category;
        handler = handler == null ? () -> { } : handler;
    }
}
