package top.huliawsl.slateui.api;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;

public class ComputedStateProvider extends MutableStateProvider {

    private final Map<String, ComputedValue> computedValues = new LinkedHashMap<>();
    private final Map<String, Set<String>> dependencyIndex = new LinkedHashMap<>();

    public ComputedStateProvider registerComputed(String path, List<String> dependencies, Function<StateProvider, Object> resolver) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(dependencies, "dependencies");
        Objects.requireNonNull(resolver, "resolver");
        computedValues.put(path, new ComputedValue(List.copyOf(dependencies), resolver));
        for (String dependency : dependencies) {
            dependencyIndex.computeIfAbsent(dependency, ignored -> new LinkedHashSet<>()).add(path);
        }
        super.notifyDirty(path);
        return this;
    }

    @Override
    public ComputedStateProvider set(String path, Object value) {
        super.set(path, value);
        return this;
    }

    @Override
    public Object get(String path) {
        ComputedValue computed = computedValues.get(path);
        if (computed == null) {
            return super.get(path);
        }
        if (computed.dirty) {
            computed.cached = computed.resolver.apply(this);
            computed.dirty = false;
        }
        return computed.cached;
    }

    @Override
    public boolean contains(String path) {
        return computedValues.containsKey(path) || super.contains(path);
    }

    @Override
    public void notifyDirty(String path) {
        super.notifyDirty(path);
        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new LinkedHashSet<>();
        queue.add(path);
        while (!queue.isEmpty()) {
            String current = queue.remove();
            for (String computedPath : dependencyIndex.getOrDefault(current, Set.of())) {
                if (!visited.add(computedPath)) {
                    continue;
                }
                ComputedValue computed = computedValues.get(computedPath);
                if (computed == null) {
                    continue;
                }
                computed.dirty = true;
                super.notifyDirty(computedPath);
                queue.add(computedPath);
            }
        }
    }

    @Override
    public Map<String, Object> snapshot() {
        Map<String, Object> snapshot = new LinkedHashMap<>(super.snapshot());
        for (String path : computedValues.keySet()) {
            snapshot.put(path, get(path));
        }
        return snapshot;
    }

    private static final class ComputedValue {

        private final List<String> dependencies;
        private final Function<StateProvider, Object> resolver;
        private Object cached;
        private boolean dirty = true;

        private ComputedValue(List<String> dependencies, Function<StateProvider, Object> resolver) {
            this.dependencies = new ArrayList<>(dependencies);
            this.resolver = resolver;
        }
    }
}
