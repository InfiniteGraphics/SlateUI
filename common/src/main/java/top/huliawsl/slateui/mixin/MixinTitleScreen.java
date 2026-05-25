package top.huliawsl.slateui.mixin;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.huliawsl.slateui.SlateUI;
import top.huliawsl.slateui.demo.SlateDemoEntrypoint;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen {

    protected MixinTitleScreen(Component title) {
        super(title);
    }

    @Inject(method = "init()V", at = @At("TAIL"))
    private void slateui$onInit(CallbackInfo callbackInfo) {
        if (!SlateUI.shouldEnableDevScreens()) {
            return;
        }
        TitleScreen screen = (TitleScreen) (Object) this;
        addRenderableWidget(SlateDemoEntrypoint.createTitleScreenButton(screen, width / 2 - 62, height / 4 + 120));
    }
}
