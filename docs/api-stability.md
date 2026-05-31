# API Stability

SlateUI 1.0 freezes the screen-first Java API. Public packages are listed by `SlateApiSurface.publicPackages()`. Internal packages are listed by `SlateApiSurface.internalPackages()` and are not covered by compatibility guarantees.

`@SlatePublicApi`, `@SlateExperimentalApi`, and `@SlateInternalApi` are the local API status markers. They are intentionally small and do not require an external annotation dependency.

Stable component APIs:

- `OverlayRoot`
- `Box`
- `Stack`
- `Panel`
- `Text`
- `Button`
- `Input`
- `Toggle`
- `Image`
- `ScrollView`
- authoring `List`

The style model, command model, and authoring IR version are frozen through `SlateApiSurface`. Theme token names listed in `ThemeTokens.defaults()` are part of the stable contract.

Binary compatibility checks live in `V10ReleaseReadinessTest`. Any future public API removal should update that test and the migration policy in the same change.
