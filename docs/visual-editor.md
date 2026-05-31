# Visual Editor

The editor surface starts as a read-only preview. `SlateEditorPreview` renders a component tree into draw commands and exposes component tree and property panels for inspection.

Edit operations are represented by `SlateEditorAction`: resize, reorder, style edit, theme token selection, command selection, binding selection, and source navigation. This keeps the runtime small while leaving a clear bridge for an in-game editor or a desktop editor app.

`SlateEditorDocument` owns `.slate` import/export text. Source map navigation is represented as stable line and column targets.
