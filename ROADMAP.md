# Roadmap

SlateUI is focused on a lightweight, Java-first Minecraft screen UI runtime.

## Done

- Screen component runtime with measure, layout, style, draw commands, state, binding, commands, and diagnostics.
- Stable core components: `OverlayRoot`, `Box`, `Stack`, `Panel`, `Text`, `Button`, `Input`, `Toggle`, `Image`, `ScrollView`, and authoring `List`.
- Explicit style merge semantics for zero, false, none, and token-backed values.
- Theme token resolution for layout and rendering.
- Non-destructive missing button commands.
- Real texture draw commands with region support.
- Input cursor logic, selection logic, clipboard, delete, paste, commit, visual selection, and horizontal scrolling.
- Focus traversal and focused activation.
- Renderer adapter path through `SlateRenderer`.
- `SlateText` preservation through draw commands.
- Strict named slot validation.
- Screen state listener cleanup.

## Next

- Extract a physical core module once the adapter boundary is stable.
- Add parser-level source locations instead of DOM-backed approximate locations.
- Add schema export and compiler configuration.
- Add resource-pack override file discovery and validation.
- Add stronger release matrix tests for loader and Minecraft versions.

## Stable Scope

- Screen runtime.
- Core Java components.
- Basic `.slate` authoring.
- Style and theme tokens.
- Text and texture rendering.
- Input and focus behavior.
- Diagnostics.

## Experimental Scope

- `SlotGrid`
- `Tooltip`
- `Popup`
- `Modal`
- HUD
- world-space UI
- server intent bridge
- resource override registry
