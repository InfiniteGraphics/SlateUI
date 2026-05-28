# Version Adapter Notes

## Support Levels

- `SUPPORTED`: intended release target.
- `TESTED`: covered by local or CI build/test.
- `CONSIDERED`: tracked for compatibility work, not promised.
- `EXPERIMENTAL`: available for early use.
- `UNSUPPORTED`: outside the current policy.

## Current Matrix

- Fabric 1.21.x uses the Fabric loader module and shared Minecraft adapter code.
- Forge 1.21.x uses the Forge loader module and shared Minecraft adapter code.
- NeoForge 1.21.x uses the NeoForge loader module and shared Minecraft adapter code.
- Forge 1.20.1 is tracked as a consideration only.

## Adapter Boundary

`slateui-core` must not import `net.minecraft.*`. Minecraft-specific code is compiled through `slateui-minecraft` and the loader modules.

Clipboard, text measurement, rendering, command host operations, and screen hosting are adapter responsibilities.
