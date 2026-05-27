package top.huliawsl.slateui.world;

import top.huliawsl.slateui.layout.Rect;

@FunctionalInterface
public interface WorldSpaceProjection {

    Rect project(WorldSpaceAnchor anchor, int screenWidth, int screenHeight);

    static WorldSpaceProjection screenCenter() {
        return (anchor, screenWidth, screenHeight) -> new Rect(
            Math.max(0, (screenWidth - anchor.width()) / 2),
            Math.max(0, (screenHeight - anchor.height()) / 2),
            anchor.width(),
            anchor.height()
        );
    }
}
