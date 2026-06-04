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

    /**
     * Legacy global pointer capture hook. Prefer capturePointer(component, button, reason)
     * for component-level drag capture.
     */
    default void requestPointerCapture(String reason) {
    }

    /**
     * Legacy global pointer capture release hook. Prefer releasePointer(component, reason).
     */
    default void releasePointerCapture(String reason) {
    }

    default void capturePointer(SlateComponent component, int button, String reason) {
        requestPointerCapture(reason);
    }

    default void releasePointer(SlateComponent component, String reason) {
        releasePointerCapture(reason);
    }

    default SlateComponent capturedPointer() {
        return null;
    }

    default boolean isPointerCaptured(SlateComponent component) {
        return component != null && capturedPointer() == component;
    }

    default void setCursor(SlateCursor cursor) {
    }

    default SlateDragDropManager dragDropManager() {
        return new SlateDragDropManager();
    }

    default StateProvider stateProvider() {
        return StateProvider.EMPTY;
    }
}
