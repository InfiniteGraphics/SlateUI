package top.huliawsl.slateui;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class SlateUiNeoForge {

    public SlateUiNeoForge(IEventBus eventBus) {
        SlateUI.init();
    }
}

