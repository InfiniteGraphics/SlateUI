package top.huliawsl.slateui.command;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import top.huliawsl.slateui.security.SlateCommandCapability;

public final class SlateCommand {

    private final String id;
    private final String title;
    private final String description;
    private final SlateShortcut shortcut;
    private final SlateCommandCapability capability;
    private final Predicate<CommandContext> enabled;
    private final Predicate<CommandContext> visible;
    private final Consumer<CommandContext> handler;

    private SlateCommand(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id");
        this.title = builder.title == null || builder.title.isBlank() ? builder.id : builder.title;
        this.description = builder.description == null ? "" : builder.description;
        this.shortcut = builder.shortcut;
        this.capability = builder.capability == null ? SlateCommandCapability.LOCAL_SAFE : builder.capability;
        this.enabled = builder.enabled == null ? ignored -> true : builder.enabled;
        this.visible = builder.visible == null ? ignored -> true : builder.visible;
        this.handler = Objects.requireNonNull(builder.handler, "handler");
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public String id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public SlateShortcut shortcut() {
        return shortcut;
    }

    public SlateCommandCapability capability() {
        return capability;
    }

    public boolean enabled(CommandContext context) {
        return enabled.test(context);
    }

    public boolean visible(CommandContext context) {
        return visible.test(context);
    }

    public void execute(CommandContext context) {
        handler.accept(context);
    }

    public static final class Builder {
        private final String id;
        private String title;
        private String description;
        private SlateShortcut shortcut;
        private SlateCommandCapability capability;
        private Predicate<CommandContext> enabled;
        private Predicate<CommandContext> visible;
        private Consumer<CommandContext> handler;

        private Builder(String id) {
            this.id = id;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder shortcut(SlateShortcut shortcut) {
            this.shortcut = shortcut;
            return this;
        }

        public Builder capability(SlateCommandCapability capability) {
            this.capability = capability;
            return this;
        }

        public Builder enabledWhen(Predicate<CommandContext> enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder visibleWhen(Predicate<CommandContext> visible) {
            this.visible = visible;
            return this;
        }

        public Builder handler(Consumer<CommandContext> handler) {
            this.handler = handler;
            return this;
        }

        public SlateCommand build() {
            return new SlateCommand(this);
        }
    }
}
