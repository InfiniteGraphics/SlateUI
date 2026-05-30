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

## Example machine UI

Use a `SlotGrid` for machine input/output slots, a `ProgressBar` for work progress, and `PlayerInventory` below it. Bind both grids to server-backed providers and route clicks through one namespaced intent such as `example.machine.slot_click`.

## Example storage UI

Use a `SearchBox`, `VirtualList`, and `SlotGrid` pages backed by a server-side slot provider. Keep all move, filter, and lock decisions in server validation hooks.
