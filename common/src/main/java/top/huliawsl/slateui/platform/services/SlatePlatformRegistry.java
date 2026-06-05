package top.huliawsl.slateui.platform.services;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class SlatePlatformRegistry {

    private static final SlatePlatformRegistry GLOBAL = new SlatePlatformRegistry();

    private final Map<String, SlateConfigScreenProvider> configScreens = new LinkedHashMap<>();
    private final Map<String, SlateKeybindRegistration> keybinds = new LinkedHashMap<>();
    private final Map<String, SlateReloadListener> reloadListeners = new LinkedHashMap<>();
    private final Map<String, SlateTickListener> tickListeners = new LinkedHashMap<>();
    private final Map<String, SlateRenderHook> renderHooks = new LinkedHashMap<>();

    public static SlatePlatformRegistry global() {
        return GLOBAL;
    }

    public SlatePlatformRegistry registerConfigScreen(String modId, SlateConfigScreenProvider provider) {
        if (modId != null && !modId.isBlank() && provider != null) {
            configScreens.put(modId, provider);
        }
        return this;
    }

    public Optional<SlateConfigScreenProvider> configScreen(String modId) {
        return Optional.ofNullable(configScreens.get(modId));
    }

    public Map<String, SlateConfigScreenProvider> configScreens() {
        return Map.copyOf(configScreens);
    }

    public SlatePlatformRegistry registerKeybind(SlateKeybindRegistration registration) {
        if (registration != null) {
            keybinds.put(registration.id(), registration);
        }
        return this;
    }

    public Map<String, SlateKeybindRegistration> keybinds() {
        return Map.copyOf(keybinds);
    }

    public SlatePlatformRegistry registerReloadListener(String id, SlateReloadListener listener) {
        if (id != null && !id.isBlank() && listener != null) {
            reloadListeners.put(id, listener);
        }
        return this;
    }

    public void fireReload() {
        reloadListeners.values().forEach(SlateReloadListener::reload);
    }

    public Map<String, SlateReloadListener> reloadListeners() {
        return Map.copyOf(reloadListeners);
    }

    public SlatePlatformRegistry registerTickListener(String id, SlateTickListener listener) {
        if (id != null && !id.isBlank() && listener != null) {
            tickListeners.put(id, listener);
        }
        return this;
    }

    public void fireClientTick() {
        tickListeners.values().forEach(SlateTickListener::tick);
    }

    public Map<String, SlateTickListener> tickListeners() {
        return Map.copyOf(tickListeners);
    }

    public SlatePlatformRegistry registerRenderHook(String id, SlateRenderHook hook) {
        if (id != null && !id.isBlank() && hook != null) {
            renderHooks.put(id, hook);
        }
        return this;
    }

    public void fireClientRender(float partialTick) {
        renderHooks.values().forEach(hook -> hook.render(partialTick));
    }

    public Map<String, SlateRenderHook> renderHooks() {
        return Map.copyOf(renderHooks);
    }

    public void clear() {
        configScreens.clear();
        keybinds.clear();
        reloadListeners.clear();
        tickListeners.clear();
        renderHooks.clear();
    }
}
