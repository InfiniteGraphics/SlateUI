package top.huliawsl.slateui.api;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MutableStateProvider implements StateProvider {

    protected final Map<String, Object> values = new LinkedHashMap<>();
    private final List<StateListener> listeners = new ArrayList<>();
    private int batchDepth;
    private final List<String> batchedPaths = new ArrayList<>();

    public MutableStateProvider set(String path, Object value) {
        values.put(Objects.requireNonNull(path, "path"), value);
        notifyDirty(path);
        return this;
    }

    public MutableStateProvider updateBatch(java.util.function.Consumer<MutableStateProvider> updates) {
        Objects.requireNonNull(updates, "updates");
        batchDepth++;
        try {
            updates.accept(this);
        } finally {
            batchDepth--;
            if (batchDepth == 0 && !batchedPaths.isEmpty()) {
                List<String> paths = List.copyOf(batchedPaths);
                batchedPaths.clear();
                for (String path : paths) {
                    notifyNow(path);
                }
            }
        }
        return this;
    }

    @Override
    public Object get(String path) {
        return values.get(path);
    }

    @Override
    public boolean contains(String path) {
        return values.containsKey(path);
    }

    @Override
    public void addListener(StateListener listener) {
        listeners.add(Objects.requireNonNull(listener, "listener"));
    }

    @Override
    public void removeListener(StateListener listener) {
        listeners.remove(listener);
    }

    public void notifyDirty(String path) {
        if (batchDepth > 0) {
            if (!batchedPaths.contains(path)) {
                batchedPaths.add(path);
            }
            return;
        }
        notifyNow(path);
    }

    private void notifyNow(String path) {
        for (StateListener listener : List.copyOf(listeners)) {
            listener.onStateDirty(path);
        }
    }

    @Override
    public Map<String, Object> snapshot() {
        return Map.copyOf(values);
    }
}
