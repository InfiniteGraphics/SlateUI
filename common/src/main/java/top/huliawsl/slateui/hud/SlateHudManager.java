package top.huliawsl.slateui.hud;

import java.util.ArrayList;
import java.util.List;

public final class SlateHudManager {

    private final List<SlateHudLayer> layers = new ArrayList<>();

    public SlateHudManager addLayer(SlateHudLayer layer) {
        if (layer != null) {
            layers.add(layer);
        }
        return this;
    }

    public List<SlateHudLayer> layers() {
        return List.copyOf(layers);
    }

    public void markAllDirty() {
        for (SlateHudLayer layer : layers) {
            layer.markDirty();
        }
    }
}
