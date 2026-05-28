# Authoring Security

`.slate` files describe component trees and simple state bindings. They do not execute script code.

Rules:

- Unknown builtin props are rejected.
- Named slots are accepted only when the target component consumes them.
- Bindings are state paths, not arbitrary expressions.
- Commands are IDs looked up in `SlateCommandRegistry`.
- External files should be compiled before packaging and validated in CI.

For dynamic lists, use `key` with a stable item identifier. This keeps component identity stable across reorder operations.
