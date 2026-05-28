package top.huliawsl.slateui.runtime;

import top.huliawsl.slateui.api.SlateComponent;

public interface SlateHost {

    SlateHost NOOP = new SlateHost() {
        @Override
        public void requestRebuild(String reason) {
        }

        @Override
        public void requestFocus(SlateComponent component) {
        }

        @Override
        public void clearFocus(SlateComponent component) {
        }

        @Override
        public SlateComponent focusedComponent() {
            return null;
        }
    };

    void requestRebuild(String reason);

    void requestFocus(SlateComponent component);

    void clearFocus(SlateComponent component);

    SlateComponent focusedComponent();

    default String title() {
        return "";
    }
}
