# Multi-version adoption guide

Use a matrix lane when validating SlateUI against a specific Minecraft line:

```bash
./gradlew -Pslateui.matrixLane=mc1210 :fabric:build
./gradlew -Pslateui.matrixLane=mc1210 :neoforge:build
./gradlew -Pslateui.matrixLane=mc1201 :forge:build
```

Available lanes live in `gradle/slateui-version-matrix.properties` and are backed by `versions/<lane>/gradle.properties`.

## Publication naming

Loader artifacts use this form:

```text
top.huliawsl:slateui-fabric-mc1_21:<version>
top.huliawsl:slateui-neoforge-mc1_21:<version>
top.huliawsl:slateui-forge-mc1_20_1:<version>
```

Advanced users may depend on:

```text
top.huliawsl:slateui-core:<version>
top.huliawsl:slateui-bom:<version>
```

## Resource layout

Data-driven UI files should use:

```text
assets/<modid>/slate/screens/*.slate
assets/<modid>/slate/themes/*.json
assets/<modid>/slate/components/*.slate
assets/<modid>/slate/state/*.json
assets/<modid>/slate/schema/*.json
```

## Platform services

Use `Services.PLATFORM.snapshot()` to inspect the runtime lane, support level, game/config directories, and loader name.
