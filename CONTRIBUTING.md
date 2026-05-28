# Contributing

SlateUI changes should stay small, reviewable, and tied to a runtime or authoring problem.

## Local Checks

```powershell
$env:JAVA_HOME = "D:\ENV\jdk\temurin-21.0.11"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat test
```

## Scope Rules

- Keep core screen runtime changes in `common`.
- Keep loader entrypoints thin.
- Do not add resource files unless the change explicitly needs them.
- Do not make experimental surfaces part of the stable compatibility promise.
- Prefer tests for style merge, layout, input, compiler, and lifecycle behavior.

## Commit Rules

- Use one commit per behavior change.
- Use English commit messages.
- Do not mix documentation, runtime, compiler, and formatting-only changes unless the change is intentionally documentation-only.
