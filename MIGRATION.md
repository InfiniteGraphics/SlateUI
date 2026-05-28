# Migration Notes

## From Vanilla `Screen`

- Move layout calculations into components instead of hand-positioned widgets.
- Use `MutableStateProvider` for simple settings state.
- Register side effects through `SlateCommandRegistry`.
- Use `SlateScreen` as the Minecraft host for the component tree.

## From Direct Minecraft Text

- Use `SlateText.literal(...)` for fixed text.
- Use `SlateText.translatable(...)` for localization keys.
- Let the Minecraft adapter convert `SlateText` to `Component`.

## From Hard-coded Textures

- Use `Image` with `resource`, `u`, `v`, `regionWidth`, `regionHeight`, `textureWidth`, and `textureHeight`.
- Keep resources in the mod or resource pack that owns the UI.
- Missing resources fall back through renderer diagnostics instead of crashing the component tree.

## Known Boundaries

- Container, HUD, world-space, modal, popup, tooltip, and override behavior should be treated as experimental.
- Full module separation is intentionally deferred; the current boundary is enforced through adapter interfaces and tests.
