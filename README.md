# SlateUI

SlateUI is a lightweight, Java-first Minecraft UI runtime for mod screens.

It is built for the screens mod authors ship often: settings pages, information pages, compact forms, lists, scroll areas, and debug-friendly tools.

## Good Fit

- Configuration screens.
- Information panels.
- Lightweight interactive tools.
- Screens that benefit from state binding and diagnostics.
- Layouts that should survive Minecraft version changes with less adapter churn.

## Not Promised Yet

- Stable container UI.
- Stable HUD or world-space UI.
- Visual editing.
- Full LDLib-style editor or ecosystem replacement.
- Resource-pack-only UI discovery without Java registration.

## Install Coordinates

Published artifacts should use:

```text
group: top.huliawsl
artifact: slateui
license: MIT
```

Local development currently uses the multi-loader Gradle project in this repository. Loader modules are `fabric`, `forge`, and `neoforge`; shared runtime code lives in `common`.

## Quick Start

```java
MutableStateProvider state = new MutableStateProvider()
    .set("settings.enabled", true)
    .set("settings.name", "Slate Tester");

SlateCommandRegistry commands = new SlateCommandRegistry()
    .register("settings.save", context -> saveConfig(context.payload()));

SlateComponent root = new Panel("Settings", List.of(
    new Text(provider -> "Name: " + state.get("settings.name"), SlateStyle.EMPTY),
    new Input(state, "Player name", provider -> String.valueOf(provider.get("settings.name")), null,
        (context, value) -> state.set("settings.name", value), SlateStyle.builder().width(180).build()),
    new Toggle(state, "Enable feature", provider -> Boolean.TRUE.equals(provider.get("settings.enabled")), null,
        (context, checked) -> state.set("settings.enabled", checked), SlateStyle.EMPTY),
    new Button("Save", "settings.save", SlateStyle.EMPTY)
), SlateStyle.builder().width(260).build());

Minecraft.getInstance().setScreen(new SlateScreen(
    Component.literal("Settings"), root, commands, state, Theme.DEFAULT, false
));
```

## Stable Screen Scope

- `OverlayRoot`, `Box`, `Stack`, `Panel`, `Text`, `Button`, `Input`, `Toggle`, `Image`, `ScrollView`, and authoring `<List>`.
- Java `SlateList` for list layout when writing components directly.
- Style width, height, min/max constraints, padding, gap, border, radius, focus border, text color, disabled, and clip content.
- Theme tokens for colors, spacing, and radii.
- `SlateText.literal(...)` and `SlateText.translatable(...)` through draw commands.
- Real texture rendering with UV and region props.
- Clipboard-backed input, selection, cursor rendering, horizontal scrolling, and max length.
- Tab focus traversal and focused Button / Toggle activation.
- Runtime diagnostics for component tree, layout, hit regions, draw commands, bindings, commands, state, and diagnostics logs.
- Runtime contracts are documented in `docs/runtime-contracts.md`.
- Gallery and example notes are documented in `docs/gallery.md`.

## Experimental Scope

These APIs can be used, but compatibility is intentionally looser:

- `SlotGrid`
- `Tooltip`
- `Popup`
- `Modal`
- HUD surfaces
- world-space surfaces
- server intent bridge
- resource override registry

## `.slate` Authoring

Example:

```xml
<template>
  <Panel id="settings" title="Settings" class="panel">
    <Input placeholder="Player name" value="{settings.name}" />
    <Toggle label="Enable feature" checked="{settings.enabled}" />
    <Button label="Save" command="settings.save" />
    <Image resource="minecraft:textures/gui/widgets.png"
           style-width="18"
           style-height="18"
           u="0"
           v="0"
           regionWidth="18"
           regionHeight="18"
           textureWidth="256"
           textureHeight="256" />
  </Panel>
</template>

<style scoped>
  Panel { padding: 12; gap: 8; borderRadius: 8; }
  .panel:hover { background: #111827; }
  #settings:focus { focusBorderColor: #60A5FA; }
</style>
```

The compiler rejects unknown props, unsupported style properties, invalid style blocks, and named slots that the target component does not consume.

## Dependencies and Terms

- Runtime code is distributed under the MIT license.
- Minecraft, loader APIs, Mojang mappings, Fabric, Forge, NeoForge, Sponge Mixin, Gson, JUnit, Kotlin, and Gradle retain their own licenses.
- Mods using SlateUI should follow the license terms of their selected loader and Minecraft distribution channel.

## Development

```powershell
$env:JAVA_HOME = "D:\ENV\jdk\temurin-21.0.11"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat test
```

Expected result: Java tests pass. Missing texture or translation resources should be treated as resource warnings unless a Java compile or test failure is reported.

## Supported Versions

| Loader | Minecraft | Policy |
| --- | --- | --- |
| Fabric | 1.21.x | tested |
| Forge | 1.21.x | tested |
| NeoForge | 1.21.x | tested |
| Forge | 1.20.1 | considered |
| Other versions | any | unsupported until added to `SlateCompatibilityMatrix` |

Version-specific Minecraft APIs live behind the Minecraft adapter module and loader modules. Core runtime APIs are checked separately by `slateui-core`.

## Authoring and Overrides

- Authoring safety rules are documented in `docs/authoring-security.md`.
- Resource override locations and priority are documented in `docs/resource-overrides.md`.
- Compiler schema can be exported with `SlateCompilerCli --schema`.
- Compiler watch mode is available as `SlateCompilerCli --watch <inputDir> <outputDir>`.
