# SlateUI framework hardening checklist

This checklist tracks the work needed for SlateUI to be a high-version, multi-loader, multi-version UI framework instead of only a screen runtime.

## Multi-version / multi-loader

- [x] Canonical compatibility matrix in `SlateCompatibilityMatrix.current()`.
- [x] Matrix lane property files under `versions/mc1201`, `versions/mc1210`, and `versions/mc1214`.
- [x] Gradle lane loader through `-Pslateui.matrixLane=<lane>`.
- [x] CI matrix workflow for Fabric / Forge / NeoForge lanes.
- [x] External consumer example projects for Fabric, Forge, and NeoForge.

## Platform service layer

- [x] Loader id and Minecraft version detection.
- [x] Mod loaded and mod version lookup.
- [x] Game/config directory service.
- [x] Config screen registration contract.
- [x] Keybind registration contract.
- [x] Client reload/tick/render hook contracts.
- [x] Networking bridge contract.
- [x] Runtime compatibility snapshot.

## Publication surface

- [x] Loader artifacts are named `slateui-<loader>-mc<line>`.
- [x] Core and BOM coordinates are modeled by `SlateArtifactCoordinates`.
- [x] `SlatePublicationPlan` can derive public coordinates from the compatibility matrix.
- [x] Sources and Javadocs remain enabled by default through `multiloader-common.gradle`.

## Resource / authoring layout

- [x] Canonical data-driven layout: `assets/<modid>/slate/screens`, `themes`, `components`, `state`, and `schema`.
- [x] `SlateResourceLocation` validates namespace/path and prevents traversal.
- [x] `SlateResourceLayout` exposes helpers for screen/theme/component paths.

## Security and diagnostics

- [x] Command capabilities: local safe, client action, server intent, dangerous external, debug only.
- [x] Command security policy is wired into `SlateCommandRegistry`.
- [x] External `.slate` policies distinguish local resource packs from server-provided UI.
- [x] Server intent policy checks protocol, nonce replay, timestamp skew, payload size, and command capability.
- [x] Debug command registry exposes dump commands for tree/layout/draw/style/state/focus/events/summary.

## Still intentionally experimental

These are intentionally not marked stable by this patch:

- Container UI parity with vanilla ScreenHandler behavior.
- World-space UI occlusion/input parity across loader versions.
- Visual editor authoring UX.
