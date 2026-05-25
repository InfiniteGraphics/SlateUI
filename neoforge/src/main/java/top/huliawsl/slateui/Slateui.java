package top.huliawsl.slateui;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class Slateui {

    public Slateui(IEventBus eventBus) {
        SlateUI.init();
    }
}
