package top.huliawsl.slateui.api;

import top.huliawsl.slateui.api.container.SlateContainerPolicy;
import top.huliawsl.slateui.api.container.SlateMenuBinding;
import top.huliawsl.slateui.command.SlateCommandRegistry;

public final class SlateContainerScreen {

    private final String title;
    private final SlateComponent root;
    private final SlateCommandRegistry commands;
    private final StateProvider stateProvider;
    private final Theme theme;
    private final boolean debugEnabled;
    private final SlateMenuBinding menuBinding;
    private final SlateContainerPolicy containerPolicy;

    public SlateContainerScreen(
        String title,
        SlateComponent root,
        SlateCommandRegistry commands,
        StateProvider stateProvider,
        Theme theme,
        boolean debugEnabled,
        SlateMenuBinding menuBinding,
        SlateContainerPolicy containerPolicy
    ) {
        this.title = title == null ? "" : title;
        this.root = java.util.Objects.requireNonNull(root, "root");
        this.commands = commands == null ? new SlateCommandRegistry() : commands;
        this.stateProvider = stateProvider == null ? StateProvider.EMPTY : stateProvider;
        this.theme = theme == null ? Theme.DEFAULT : theme;
        this.debugEnabled = debugEnabled;
        this.menuBinding = menuBinding == null ? new SlateMenuBinding("", java.util.Map.of()) : menuBinding;
        this.containerPolicy = containerPolicy == null ? SlateContainerPolicy.serverAuthoritative() : containerPolicy;
    }

    public String title() {
        return title;
    }

    public SlateComponent root() {
        return root;
    }

    public SlateCommandRegistry commands() {
        return commands;
    }

    public StateProvider stateProvider() {
        return stateProvider;
    }

    public Theme theme() {
        return theme;
    }

    public boolean debugEnabled() {
        return debugEnabled;
    }

    public SlateMenuBinding menuBinding() {
        return menuBinding;
    }

    public SlateContainerPolicy containerPolicy() {
        return containerPolicy;
    }
}
