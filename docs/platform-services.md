# Platform Services

SlateUI keeps loader integration behind small registration contracts.

Use `SlatePlatformRegistration` for common config screen registration. Fabric, Forge, and NeoForge modules expose loader-specific config screen integration placeholders without taking hard dependencies on optional UI libraries.

ModMenu integration is represented by `ModMenuIntegration`. Cloth Config migration is documented through `ClothConfigMigration`: map categories to panels, entries to form components, and save operations to commands.
