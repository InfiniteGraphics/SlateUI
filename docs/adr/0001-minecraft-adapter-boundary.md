# ADR 0001: Minecraft adapter boundary

## Decision

SlateUI keeps the reusable runtime API in common Java packages and treats Minecraft-specific rendering, text measurement, screens, HUD hooks, and world-space hooks as adapter code.

## Rules

- `slateui-core` remains free of direct `net.minecraft` imports.
- Loader modules provide service implementations for Fabric, Forge, and NeoForge.
- Multi-version behavior is represented by matrix lanes, not scattered conditionals.
- Data-driven UI uses canonical resource paths under `assets/<modid>/slate`.

## Consequences

- Loader/platform services can grow without leaking loader APIs into component code.
- New Minecraft versions should primarily touch version lane properties and adapter implementations.
- CI can prove external consumers can depend on published loader artifacts.
