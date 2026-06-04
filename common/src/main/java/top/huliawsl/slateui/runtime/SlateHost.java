package top.huliawsl.slateui.runtime;

import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.StateProvider;

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

    default void requestInvalidation(InvalidationType type, String reason) {
        requestRebuild((type == null ? InvalidationType.LAYOUT : type).name().toLowerCase() + ":" + reason);
    }

    void requestFocus(SlateComponent component);

    void clearFocus(SlateComponent component);

    SlateComponent focusedComponent();

    default String title() {
        return "";
    }

    default void openScreen(Object screenHandle) {
    }

    default void closeScreen() {
    }

    default void inspect() {
    }

    default void reportDiagnostic(String entry) {
    }

    default void requestPointerCapture(String reason) {
    }

    default void releasePointerCapture(String reason) {
    }

    default void setCursor(SlateCursor cursor) {
    }

    default StateProvider stateProvider() {
        return StateProvider.EMPTY;
    }
}
