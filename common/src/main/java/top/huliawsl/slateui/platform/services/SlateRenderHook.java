package top.huliawsl.slateui.platform.services;

@FunctionalInterface
public interface SlateRenderHook {
    void render(float partialTick);
}
