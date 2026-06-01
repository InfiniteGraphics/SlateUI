# Container, Slot, and Server Intent

SlateUI container support is intentionally lightweight in v0.5. The client builds UI with `SlateContainerScreen`, `SlotGrid`, `PlayerInventory`, and `SlateMenuBinding`; gameplay changes are represented as `SlateIntentPacket` and validated server-side before any inventory mutation.

## Server-authoritative flow

1. `SlotGrid` emits a command payload containing `slotIndex`, `itemId`, `count`, `clickType`, and `mode`.
2. The command bridge converts the payload to `SlateServerIntent`.
3. `SlateIntentPacket` carries the protocol version, intent id, screen title, payload, and nonce.
4. `SlateIntentSecurityPolicy` rejects unsupported protocol versions, duplicate nonces, and missing intent ids.
5. The server applies mod-specific permission and slot validation hooks, then returns `SlateIntentResult`.

`SlateIntentSyncPolicy.SERVER_AUTHORITATIVE` is the default. `OPTIMISTIC_WITH_ROLLBACK` is available for UIs that can preview a local change and recover when the server rejects it.

## Compatibility decisions

`SlateContainerPolicy` records quick-move support, recipe book compatibility, vanilla slot interop, and the JEI/REI/EMI/XEI adapter point. v0.5 does not ship those ecosystem adapters as hard dependencies.

## v1.1 target: native container shell

SlateUI does not replace Minecraft's authoritative inventory implementation. The native `Menu` / `ScreenHandler` remains responsible for slot contents, transaction ids, carried stack state, quick-move behavior, creative-mode rules, and server validation. SlateUI owns layout, non-slot widgets, visual slot placement, diagnostics, and the mapping from Slate slot coordinates to native slot indexes.

The v1.1 target is a stable ordinary machine GUI:

- Machine-specific slots are described with `NativeContainerSlotRole.MACHINE`.
- Output slots use `NativeContainerSlot.output(...)`; they are extract-only and reject insertion at the Slate contract level.
- Upgrade/filter slots use a `validatorId` so the server-side menu can apply the matching native validator.
- `PlayerInventoryLayout.vanilla(...)` describes the 27 inventory slots plus 9 hotbar slots without inventing a new inventory model.
- `SlateNativeContainerBinding.click(...)` maps Slate click semantics to native intents: left pickup/place, right split/place-one, shift quick-move, drag split, double-click pickup-all, number-key hotbar swap, and creative clone.
- `SlateIntentSyncPolicy.SERVER_AUTHORITATIVE` is the default; client-side visuals should wait for native menu sync or roll back optimistic previews.

Platform adapters should translate `NativeContainerClick.payload()` into the loader's native slot-click call. The adapter must never mutate inventory from SlateUI-only state.

## Example machine UI

Use a `SlotGrid` for machine input/output slots, a `ProgressBar` for work progress, and `PlayerInventory` below it. Bind both grids to server-backed providers and route clicks through one namespaced intent such as `example.machine.slot_click`.

For a native-backed machine screen, attach the same `SlateNativeContainerBinding` to the `SlateContainerScreen` and to the relevant `SlotGrid`. The `SlotGrid` command payload then includes `nativeSlotIndex`, `role`, `interaction`, `hotbarIndex`, `validatorId`, and `serverAuthoritative`.

## Example storage UI

Use a `SearchBox`, `VirtualList`, and `SlotGrid` pages backed by a server-side slot provider. Keep all move, filter, and lock decisions in server validation hooks.
