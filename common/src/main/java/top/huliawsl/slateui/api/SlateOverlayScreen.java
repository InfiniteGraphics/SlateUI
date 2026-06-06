package top.huliawsl.slateui.api;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import top.huliawsl.slateui.api.overlay.OverlayPolicy;
import top.huliawsl.slateui.command.SlateCommandRegistry;

public class SlateOverlayScreen extends SlateScreen {

    private final OverlayPolicy overlayPolicy;

    public SlateOverlayScreen(SlateText title, SlateComponent root, SlateCommandRegistry commands, OverlayPolicy overlayPolicy, boolean debugEnabled) {
        this(title, root, commands, StateProvider.EMPTY, Theme.DEFAULT, overlayPolicy, debugEnabled);
    }

    public SlateOverlayScreen(
        SlateText title,
        SlateComponent root,
        SlateCommandRegistry commands,
        StateProvider stateProvider,
        Theme theme,
        OverlayPolicy overlayPolicy,
        boolean debugEnabled
    ) {
        super(title, root, commands, stateProvider, theme, debugEnabled);
        this.overlayPolicy = overlayPolicy == null ? OverlayPolicy.DEFAULT : overlayPolicy;
    }

    public SlateOverlayScreen(Component title, SlateComponent root, SlateCommandRegistry commands, OverlayPolicy overlayPolicy, boolean debugEnabled) {
        this(title, root, commands, StateProvider.EMPTY, Theme.DEFAULT, overlayPolicy, debugEnabled);
    }

    public SlateOverlayScreen(
        Component title,
        SlateComponent root,
        SlateCommandRegistry commands,
        StateProvider stateProvider,
        Theme theme,
        OverlayPolicy overlayPolicy,
        boolean debugEnabled
    ) {
        super(title, root, commands, stateProvider, theme, debugEnabled);
        this.overlayPolicy = overlayPolicy == null ? OverlayPolicy.DEFAULT : overlayPolicy;
    }

    public OverlayPolicy overlayPolicy() {
        return overlayPolicy;
    }

    @Override
    protected void renderSlateBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!overlayPolicy.transparentBackground()) {
            super.renderSlateBackground(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    protected boolean shouldDispatchPointerEvent(double mouseX, double mouseY, String eventName) {
        return overlayPolicy.capturesPointer(mouseX, mouseY);
    }

    @Override
    protected boolean shouldDispatchKeyboardEvent(String eventName) {
        return overlayPolicy.keyboardCaptured() || focusedComponent() != null;
    }

    @Override
    public boolean isPauseScreen() {
        return overlayPolicy.pauseScreen();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return overlayPolicy.closeOnEsc();
    }
}
