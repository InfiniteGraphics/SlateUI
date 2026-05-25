package top.huliawsl.slateui.authoring;

import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import top.huliawsl.slateui.api.SlateComponent;

public final class SlateComponentRegistry {

    @FunctionalInterface
    public interface Factory {
        SlateComponent create(JsonObject node, List<SlateComponent> children, SlateIrRuntimeFactory.RuntimeBuildContext context);
    }

    private final Map<String, Factory> factories = new LinkedHashMap<>();

    public SlateComponentRegistry register(String name, Factory factory) {
        factories.put(name, factory);
        return this;
    }

    public Factory require(String name) {
        Factory factory = factories.get(name);
        if (factory == null) {
            throw new IllegalArgumentException("Unregistered component: " + name);
        }
        return factory;
    }
}