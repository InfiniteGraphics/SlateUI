# Architecture

SlateUI is organized around a small screen runtime with Minecraft-specific work kept behind adapters where possible.

## Runtime Flow

1. Components measure with `SlateLayoutContext`.
2. Components layout into `Rect` bounds.
3. Components emit draw commands.
4. `DrawCommandDispatcher` sends commands to a `SlateRenderer`.
5. Minecraft rendering is handled by `MinecraftSlateRenderer`.

## Boundary Interfaces

- `SlateHost` owns screen-level requests such as rebuild and focus.
- `SlateTextMeasurer` measures text without exposing Minecraft `Font` to layout code.
- `SlateClipboard` isolates clipboard access.
- `SlateRenderer` isolates low-level drawing.
- `SlateInteractionContext` carries command, host, state, theme, diagnostics, and clipboard services.

## Minecraft-specific Code

Minecraft classes are expected in adapter and host code:

- `SlateScreen`
- `MinecraftSlateRenderer`
- `MinecraftTextMeasurer`
- `MinecraftCommandContext`
- inspector and error screens
- HUD and world-space experimental surfaces

Component, layout, style, state, binding, and draw command models should avoid direct Minecraft imports.

## Experimental Surfaces

These APIs are available for experimentation but are not covered by the same compatibility expectations as the screen core:

- `SlotGrid`
- `Tooltip`
- `Popup`
- `Modal`
- HUD
- world-space surfaces
- server intent bridge
- resource override registry
