# Runtime Contracts

## Component Lifecycle

Components are mounted when their host screen initializes and unmounted when the screen is removed. `disposeTree()` is called on removal so components can release listeners or cached external handles.

Lifecycle hooks:

- `onMount()`
- `onUnmount()`
- `onStateUpdated(String path)`
- `onThemeUpdated(Theme theme)`
- `onScreenResized(Size size)`
- `onDispose()`

Every component has a generated `identity()` and an optional stable `componentKey(...)`. Keys are reflected in debug paths and are intended for dynamic children.

## Layout

Layout is two-pass: `measure(context, available)` computes intrinsic size, then `layout(context, bounds)` assigns final bounds. Parents own child placement. Overflow is clipped only when `clipContent` is enabled.

Deferred layout features:

- percentage sizing
- flex grow and shrink
- absolute positioning

The diagnostics screen exposes component tree, layout tree, hit regions, style dump, and runtime summary.

## Style

Style precedence is component default style, scoped type selector, scoped class selector, scoped id selector, then inline `style-*` props.

Token resolution uses token first, direct value second, fallback last. Unset values are tracked separately from explicit zero, false, and none.

Pseudo-state support:

- normal
- hover
- active / pressed
- focused
- disabled

Runtime style validation rejects invalid numeric constraints such as `minWidth > maxWidth`.

## State and Binding

`MutableStateProvider.updateBatch(...)` batches dirty notifications. `ComputedStateProvider` invalidates dependent computed values when source paths change.

Binding rules:

- Missing required paths raise diagnostics through `BindingEvaluator.evaluateStrict(...)`.
- Authoring bindings are path-only and do not execute code.
- Collection bindings should use stable keys when reordering dynamic children.
- Async state updates should enter the UI through the host thread and call provider setters there.

## Commands

Commands return `CommandResult` through `executeResult(...)`. Missing commands do not run fallback behavior. Command IDs should use namespaces such as `modid.settings.save`.

Built-in commands:

- `screen.close`
- `screen.inspect`

## Input

`Input` supports cursor movement, selection, clipboard operations, mouse cursor placement, drag selection, double-click word selection, max length, validation, password display, and commit/change/input phases.

When unfocused, bound inputs sync from external state. While focused, local draft text is preserved until commit or focus loss.

## Rendering

Draw commands are dispatched through `SlateRenderer`. Advanced commands exist for texture regions, nine-slice textures, item icons, entity previews, transform stack, opacity, and diagnostics. Minecraft-specific rendering quality depends on the active renderer capabilities.
