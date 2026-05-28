# Resource Overrides

Override roots:

- `assets/<modid>/slateui/overrides/components`
- `assets/<modid>/slateui/overrides/themes`
- `assets/<modid>/slateui/overrides/textures`

Priority order:

1. Runtime registered overrides.
2. Resource-pack overrides.
3. Mod bundled IR and theme defaults.

Supported modes:

- component override
- theme-only override
- layout-only override
- texture override through normal Minecraft resource lookup

Override diagnostics are available from `SlateOverrideRegistry.diagnostics()`.
