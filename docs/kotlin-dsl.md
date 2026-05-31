# Kotlin DSL

The Kotlin DSL remains optional. Java users can depend on the Java API and `slateui-kotlin` stays in a separate module.

Example:

```kotlin
overlayRoot {
    stack(StackDirection.COLUMN) {
        text("Settings")
        button("Done", "screen.close")
    }
}
```

Parity expectations:

- DSL components map to the same Java component classes.
- DSL output should be representable by `.slate` authoring when the target component is supported.
- DSL helpers must not introduce a Kotlin runtime requirement for Java-only consumers.
