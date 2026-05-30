# HUD and World-space UI

v0.6 keeps HUD and world-space UI experimental while reusing the same component tree, layout, style, and draw command pipeline as screens.

## HUD

`SlateHudLayer` owns one root component and a `SlateHudConfig`. The config defines lifecycle visibility, anchor, safe area, scale, and performance budgets. `SlateHudManager` can hold multiple layers and mark them dirty together.

Supported anchors are `TOP_LEFT`, `TOP_RIGHT`, `BOTTOM_LEFT`, `BOTTOM_RIGHT`, and `CENTER`. Safe area is expressed as `Insets`, so mod overlays can avoid vanilla HUD regions.

Gameplay state should be exposed through the same state providers used by screens. Config-driven layout can rebuild layers by changing `SlateHudConfig` and calling `markDirty()`.

## World-space

`WorldSpaceSlateSurface` projects a `WorldSpaceAnchor` through a `WorldSpaceProjection`. `WorldSpacePolicy` records billboard mode, distance scaling, occlusion policy, frustum culling, entity or block attachment, raycast interaction, multiplayer sync policy, and command budget.

## Example entity label UI

Attach a surface with `WorldSpaceAttachment.entity(entityId, offset)` and render a small `Card` with a `Text` label and `ProgressBar`.

## Example block terminal UI

Attach a surface with `WorldSpaceAttachment.block(blockPos, offset)`, enable raycast interaction, and render a `Panel` containing status text plus command buttons.
