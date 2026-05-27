package top.huliawsl.slateui.api;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class ScopedStateProvider implements StateProvider {

    private final StateProvider parent;
    private final Map<String, Object> locals = new LinkedHashMap<>();

    public ScopedStateProvider(StateProvider parent) {
        this.parent = Objects.requireNonNull(parent, "parent");
    }

    public ScopedStateProvider setLocal(String name, Object value) {
        locals.put(Objects.requireNonNull(name, "name"), value);
        return this;
    }

    @Override
    public Object get(String path) {
        if (locals.containsKey(path)) {
            return locals.get(path);
        }
        return parent.get(path);
    }

    @Override
    public boolean contains(String path) {
        return locals.containsKey(path) || parent.contains(path);
    }

    @Override
    public void addListener(StateListener listener) {
        parent.addListener(listener);
    }

    @Override
    public void removeListener(StateListener listener) {
        parent.removeListener(listener);
    }

    @Override
    public Map<String, Object> snapshot() {
        Map<String, Object> snapshot = new LinkedHashMap<>(parent.snapshot());
        snapshot.putAll(locals);
        return snapshot;
    }
}
