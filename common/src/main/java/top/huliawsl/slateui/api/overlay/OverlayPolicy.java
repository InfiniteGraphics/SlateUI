package top.huliawsl.slateui.api.overlay;

import java.util.List;
import top.huliawsl.slateui.layout.Rect;

public final class OverlayPolicy {

    public static final OverlayPolicy DEFAULT = OverlayPolicy.builder().build();

    private final boolean transparentBackground;
    private final boolean pauseScreen;
    private final boolean closeOnEsc;
    private final OverlayInputMode pointerInputMode;
    private final boolean keyboardCaptured;
    private final List<Rect> pointerCaptureZones;

    private OverlayPolicy(Builder builder) {
        this.transparentBackground = builder.transparentBackground;
        this.pauseScreen = builder.pauseScreen;
        this.closeOnEsc = builder.closeOnEsc;
        this.pointerInputMode = builder.pointerInputMode;
        this.keyboardCaptured = builder.keyboardCaptured;
        this.pointerCaptureZones = List.copyOf(builder.pointerCaptureZones == null ? List.of() : builder.pointerCaptureZones);
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean transparentBackground() {
        return transparentBackground;
    }

    public boolean pauseScreen() {
        return pauseScreen;
    }

    public boolean closeOnEsc() {
        return closeOnEsc;
    }

    public OverlayInputMode pointerInputMode() {
        return pointerInputMode;
    }

    public boolean keyboardCaptured() {
        return keyboardCaptured;
    }

    public List<Rect> pointerCaptureZones() {
        return pointerCaptureZones;
    }

    public boolean capturesPointer(double mouseX, double mouseY) {
        return switch (pointerInputMode) {
            case CAPTURE_ALL -> true;
            case PASS_THROUGH -> false;
            case CAPTURE_ZONES -> pointerCaptureZones.stream().anyMatch(zone -> zone.contains(mouseX, mouseY));
        };
    }

    public static final class Builder {

        private boolean transparentBackground = true;
        private boolean pauseScreen;
        private boolean closeOnEsc = true;
        private OverlayInputMode pointerInputMode = OverlayInputMode.CAPTURE_ALL;
        private boolean keyboardCaptured = true;
        private List<Rect> pointerCaptureZones = List.of();

        private Builder() {
        }

        public Builder transparentBackground(boolean transparentBackground) {
            this.transparentBackground = transparentBackground;
            return this;
        }

        public Builder pauseScreen(boolean pauseScreen) {
            this.pauseScreen = pauseScreen;
            return this;
        }

        public Builder closeOnEsc(boolean closeOnEsc) {
            this.closeOnEsc = closeOnEsc;
            return this;
        }

        public Builder pointerInputMode(OverlayInputMode pointerInputMode) {
            this.pointerInputMode = pointerInputMode == null ? OverlayInputMode.CAPTURE_ALL : pointerInputMode;
            return this;
        }

        public Builder keyboardCaptured(boolean keyboardCaptured) {
            this.keyboardCaptured = keyboardCaptured;
            return this;
        }

        public Builder pointerCaptureZones(List<Rect> pointerCaptureZones) {
            this.pointerCaptureZones = pointerCaptureZones == null ? List.of() : pointerCaptureZones;
            return this;
        }

        public Builder addPointerCaptureZone(Rect zone) {
            if (zone == null) {
                return this;
            }
            java.util.ArrayList<Rect> zones = new java.util.ArrayList<>(pointerCaptureZones);
            zones.add(zone);
            this.pointerCaptureZones = zones;
            return this;
        }

        public OverlayPolicy build() {
            return new OverlayPolicy(this);
        }
    }
}
