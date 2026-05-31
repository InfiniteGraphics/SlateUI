# 1.0 Quality Bar

The 1.0 release quality bar is represented by `SlateReleaseQualityBar.v1RequiredChecks()`.

CI runs:

- `test`
- `:slateui-core:test`
- `:slateui-minecraft:compileJava`
- `:fabric:build`
- `:forge:build`
- `:neoforge:build`

Manual launch validation remains required for example client screens because this repository may run without resource assets in the local agent environment. A successful manual check means the example mod launches, the demo screen opens, and missing resources only appear as expected missing texture or translation warnings.

No known memory leaks are tracked for the stable screen lifecycle: `SlateScreen.removed()` unmounts, disposes, and removes the state listener. Renderer crash coverage is based on draw command dispatcher tests and adapter compile checks for selected loader targets.
