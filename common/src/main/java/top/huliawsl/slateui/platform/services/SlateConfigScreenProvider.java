package top.huliawsl.slateui.platform.services;

@FunctionalInterface
public interface SlateConfigScreenProvider {
    /**
     * Returns the native screen object for the current loader/Minecraft adapter.
     * The service layer intentionally keeps this as Object so platform services do not pull Minecraft or SlateScreen classes into core contracts.
     */
    Object create(Object parentScreen);
}
