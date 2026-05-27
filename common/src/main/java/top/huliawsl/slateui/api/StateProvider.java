package top.huliawsl.slateui.api;

import java.util.Map;

public interface StateProvider {

    StateProvider EMPTY = new StateProvider() {
        @Override
        public Object get(String path) {
            return null;
        }

        @Override
        public boolean contains(String path) {
            return false;
        }

        @Override
        public void addListener(StateListener listener) {
        }

        @Override
        public void removeListener(StateListener listener) {
        }
    };

    Object get(String path);

    boolean contains(String path);

    void addListener(StateListener listener);

    void removeListener(StateListener listener);

    default Map<String, Object> snapshot() {
        return Map.of();
    }
}
