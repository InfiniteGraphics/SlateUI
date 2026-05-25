package top.huliawsl.slateui.mixin;

import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.huliawsl.slateui.SlateUI;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {

    @Inject(method = "init()V", at = @At("TAIL"))
    private void slateui$onInit(CallbackInfo callbackInfo) {
        SlateUI.registerTitleScreenHook();
    }
}
