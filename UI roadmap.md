# SlateUI Roadmap
## 0. Long-term Vision
SlateUI 的长期目标：

> Build a lightweight, Java-first, cross-version Minecraft UI runtime that can grow from simple Screen UIs into a full mod UI platform comparable in scope to LDLib2, while keeping the core runtime small, stable, and version-resilient.
>

中文定位：

> SlateUI 是一个面向 Minecraft Mod 的轻量、Java-first、跨版本友好的 UI runtime。短期专注 Screen UI，长期扩展到 container、HUD、world-space、resource override、devtools、visual authoring 和生态集成。
>

## 1. Product Strategy
### 1.1 当前阶段不要直接和 LDLib2 拼广度
LDLib2 已经覆盖：

+ GUI runtime
+ container / slot
+ editor
+ renderer
+ scene / world rendering
+ sync
+ resource system
+ KubeJS / XEI 等生态集成
+ 大量现成组件

SlateUI 前期的胜负点应该是：

+ 更轻
+ 更容易接入
+ Java-first
+ Screen-first
+ 跨 Minecraft 版本迁移成本低
+ API 稳定
+ authoring 可选，runtime 不重
+ 对普通 mod 作者友好

### 1.2 长期成长路径
SlateUI 不应该一开始就铺满 LDLib2 的所有面。更合理的成长路线：

```latex
Screen Core
  -> Authoring / Theme / Override
  -> Cross-version Runtime Isolation
  -> Container / Slot / Server Intent
  -> HUD / World-space Surface
  -> Tooling / Inspector / Visual Editor
  -> Ecosystem Integrations
  -> Full UI Platform
```

### 1.3 稳定承诺分层
#### Stable Core
v0.1 到 v1.0 优先保证这些稳定：

+ Screen runtime
+ component tree
+ measure/layout
+ style/theme
+ draw command pipeline
+ basic input/focus
+ state/binding
+ diagnostics
+ `.slate` basic authoring

#### Experimental
这些可以存在，但早期不承诺稳定：

+ SlotGrid / container UI
+ HUD
+ world-space UI
+ modal / popup / tooltip
+ server intent bridge
+ resource override
+ Kotlin DSL
+ visual editor
+ advanced compiler directives

---

# Phase 0 — Repository, Build, and Project Shape
目标：项目结构清晰，能被普通 mod 作者和贡献者 clone、build、理解。

## 0.1 Multi-loader Project Structure
- [x] `common` module exists.
- [x] `fabric` module exists.
- [x] `forge` module exists.
- [x] `neoforge` module exists.
- [x] `slateui-authoring` module exists.
- [x] `slateui-kotlin` module exists.
- [x] shared Gradle buildSrc scripts exist.
- [x] Confirm `gradlew` executable bit is committed.
- [x] Add CI build matrix.
- [x] Add artifact publishing workflow.
- [x] Add versioned changelog.
- [x] Add release checklist.

## 0.2 License and Distribution
- [x] Replace `All-Rights-Reserved` license.
- [x] Choose one:
    - MIT
    - Apache-2.0
    - LGPL-3.0
- [x] Update `gradle.properties` license metadata.
- [x] Update `LICENSE`.
- [x] Add dependency usage terms in README.
- [x] Add Maven coordinates and install instructions.
- [x] Decide whether shaded distribution is supported.

## 0.3 Documentation Foundation
- [x] Development document exists: `mcui_development_document.md`.
- [x] Add `README.md`.
- [x] Add `ROADMAP.md`.
- [x] Add `CONTRIBUTING.md`.
- [x] Add `ARCHITECTURE.md`.
- [x] Add `MIGRATION.md`.
- [x] Add examples directory.
- [x] Add screenshots / GIFs for gallery.
- [x] Add “what SlateUI is good for” section.
- [x] Add “what SlateUI does not promise yet” section.

---

# Phase 1 — v0.1 Alpha: Stable Screen Runtime
目标：先做成一个可靠的 lightweight Screen UI runtime。  
v0.1 的成功标准是：一个普通 Java mod 作者可以用 SlateUI 做配置页、信息页、轻量工具页，而且不会踩核心 runtime 坑。

## 1.1 Component Tree
- [x] `SlateComponent` base class exists.
- [x] `SlateCompositeComponent` exists.
- [x] component children traversal exists.
- [x] debug path refresh exists.
- [x] component bounds / measured size exist.
- [x] hover / pressed / focused state exists.
- [x] `dumpComponentTree()` exists.
- [x] Define public component lifecycle contract.
- [x] Add lifecycle hooks:
    - mount
    - unmount
    - state update
    - theme update
    - screen resize
- [x] Add component identity model.
- [x] Add stable key support for dynamic children.
- [x] Add component disposal cleanup.

## 1.2 Core Components
v0.1 stable component set:

- [x] `OverlayRoot`
- [x] `Box`
- [x] `Stack`
- [x] `Panel`
- [x] `Text`
- [x] `Button`
- [x] `Input`
- [x] `Toggle`
- [x] `Image`
- [x] `ScrollView`
- [x] `SlateList`
- [x] Rename or alias `SlateList` to public-facing `List` carefully.
- [x] Add component JavaDocs.
- [x] Add component examples.
- [x] Add consistent default styles.
- [x] Add disabled behavior consistency.
- [x] Add hover / active / focus visual consistency.

Experimental components:

- [x] `Tooltip`
- [x] `Popup`
- [x] `Modal`
- [x] `SlotGrid`
- [x] Move experimental components into explicit experimental package or mark JavaDoc.
- [x] Make experimental stability guarantee clear in README.

## 1.3 Layout System
- [x] measure/layout pipeline exists.
- [x] `Size` model exists.
- [x] `Rect` model exists.
- [x] `Insets` model exists.
- [x] `StackDirection` exists.
- [x] horizontal alignment exists.
- [x] vertical alignment exists.
- [x] style width / height exists.
- [x] minWidth / minHeight exists.
- [x] maxWidth / maxHeight exists.
- [x] padding works.
- [x] gap works.
- [x] `gapToken` participates in layout.
- [x] margin is rejected for v0.1 instead of silently ignored.
- [x] Add layout contract documentation.
- [x] Add overflow behavior documentation.
- [x] Add layout invalidation rules.
- [x] Add percentage sizing or explicitly defer it.
- [x] Add flex-grow / flex-shrink or explicitly defer it.
- [x] Add absolute positioning or explicitly defer it.
- [x] Add layout snapshot diagnostics.
- [x] Add layout golden tests.

## 1.4 Style System
- [x] `SlateStyle` exists.
- [x] nullable style fields distinguish unset from explicit values.
- [x] `SlateStyle.withDefaults()` supports explicit zero/false/none overrides.
- [x] background color exists.
- [x] hover background exists.
- [x] active background exists.
- [x] border exists.
- [x] focus border exists.
- [x] border radius exists.
- [x] text color exists.
- [x] clip content exists.
- [x] disabled exists.
- [x] color tokens exist.
- [x] spacing tokens exist.
- [x] radius tokens exist.
- [x] token priority is implemented.
- [x] Add style precedence documentation.
- [x] Add style inheritance rules.
- [x] Add component default style documentation.
- [x] Add pseudo-state model:
    - normal
    - hover
    - active
    - focused
    - disabled
- [x] Add style validation in runtime.
- [x] Add style diff diagnostics.
- [x] Add theme override examples.

## 1.5 Rendering
- [x] `DrawCommand` pipeline exists.
- [x] `DrawRectCommand` exists.
- [x] `DrawBorderCommand` exists.
- [x] `DrawTextCommand` exists.
- [x] `DrawTextureCommand` exists.
- [x] `PushClipCommand` exists.
- [x] `PopClipCommand` exists.
- [x] `ClipStack` exists.
- [x] rounded rect drawing exists.
- [x] rounded border drawing exists.
- [x] basic texture drawing exists.
- [x] missing texture fallback exists.
- [x] Replace static `MinecraftDrawCommandRenderer` with adapter-based renderer.
- [x] Make `SlateRenderer` the real render abstraction.
- [x] Add generic draw command dispatcher.
- [x] Add texture UV authoring support.
- [x] Add 9-slice texture support.
- [x] Add item icon rendering.
- [x] Add entity/model preview rendering.
- [x] Add z-index / layer ordering.
- [x] Add opacity support.
- [x] Add transform support:
    - scale
    - translate
    - rotation
- [x] Add render performance diagnostics.

## 1.6 Text
- [x] `SlateText` exists.
- [x] literal text model exists.
- [x] translatable text model exists.
- [x] `SlateTextMeasurer` exists.
- [x] `MinecraftTextMeasurer` exists.
- [x] Use `SlateText` in `DrawTextCommand`.
- [x] Use `SlateText` in `Text` component render path.
- [x] Convert `SlateText` to Minecraft `Component` in adapter.
- [x] Support formatted text.
- [x] Support translation args.
- [x] Support text wrapping.
- [x] Support ellipsis.
- [x] Support multiline text.
- [x] Support text alignment.
- [x] Support tooltip text model.
- [x] Add localization examples.

## 1.7 Input and Focus
- [x] `Input` component exists.
- [x] cursor position exists.
- [x] selection anchor exists.
- [x] Ctrl+A exists.
- [x] Ctrl+C exists.
- [x] Ctrl+X exists.
- [x] Ctrl+V exists.
- [x] Backspace exists.
- [x] Delete exists.
- [x] Home / End exists.
- [x] Left / Right arrow exists.
- [x] maxLength exists.
- [x] `onInput` / `onChange` / `onCommit` distinction exists.
- [x] clipboard abstraction exists.
- [x] Tab focus traversal exists.
- [x] Enter / Space activation for focused Button / Toggle exists.
- [x] Selection highlight rendering.
- [x] Cursor as draw command instead of inserted `"|"`.
- [x] Horizontal text scroll inside input.
- [x] Controlled input mode.
- [x] External state sync when not focused.
- [x] IME behavior investigation.
- [x] Mouse-based cursor placement.
- [x] Drag selection.
- [x] Double click select word.
- [x] Validation API.
- [x] Error display API.
- [x] Password input mode.

## 1.8 State and Binding
- [x] `StateProvider` exists.
- [x] `MutableStateProvider` exists.
- [x] `ComputedStateProvider` exists.
- [x] `ScopedStateProvider` exists.
- [x] `StateListener` exists.
- [x] `SlateBinding` exists.
- [x] `BindingParser` exists.
- [x] `BindingEvaluator` exists.
- [x] snapshot support exists.
- [x] state listener triggers rebuild.
- [x] Remove state listener when screen closes.
- [x] Define controlled vs uncontrolled component conventions.
- [x] Add batch update support.
- [x] Add derived state invalidation.
- [x] Add binding diagnostics.
- [x] Add binding type validation.
- [x] Add binding security model for authoring files.
- [x] Add stable collection binding semantics.
- [x] Add async state update policy.

## 1.9 Commands
- [x] `SlateCommandRegistry` exists.
- [x] command copy/registration exists.
- [x] command execution exists.
- [x] command payload exists.
- [x] Button missing command is no-op + diagnostics.
- [x] Input command payload includes value/phase.
- [x] Toggle command payload exists.
- [x] Remove direct `Minecraft` / `Screen` from core `CommandContext`.
- [x] Introduce `SlateHost`.
- [x] Add command result model.
- [x] Add command error policy.
- [x] Add async command policy.
- [x] Add command namespace recommendations.
- [x] Add built-in command registry documentation.
- [x] Add typed payload helpers.

## 1.10 Diagnostics
- [x] `SlateDiagnostics` exists.
- [x] `SlateRuntimeException` exists.
- [x] `SlateErrorScreen` exists.
- [x] `SlateInspectorScreen` exists.
- [x] command log exists.
- [x] event log exists.
- [x] diagnostic log exists.
- [x] draw command dump exists.
- [x] hit region dump exists.
- [x] binding dump exists.
- [x] runtime summary dump exists.
- [x] state dump exists.
- [x] Add frame time / rebuild time measurement.
- [x] Add layout cost measurement.
- [x] Add component count warning.
- [x] Add draw command count warning.
- [x] Add missing command warning summary.
- [x] Add missing texture warning summary.
- [x] Add authoring source link in runtime errors.
- [x] Add copy-to-clipboard debug report.

---

# Phase 2 — v0.2 Beta: Cross-version Runtime Isolation
目标：SlateUI 的核心竞争力开始成立。  
v0.2 要证明：UI core 可以尽量脱离 Minecraft API，版本差异集中在 adapter 层。

## 2.1 Core / Minecraft Boundary
当前已有：

- [x] `SlateTextMeasurer` abstraction exists.
- [x] `SlateClipboard` abstraction exists.
- [x] `SlateLayoutContext` exists.
- [x] `SlateRenderContext` exists.
- [x] `SlateInteractionContext` exists.
- [x] `SlateRenderer` interface exists.

需要继续：

- [x] Create `slateui-core` module without `net.minecraft.*`.
- [x] Move pure runtime APIs into core:
    - component
    - layout
    - style
    - theme
    - state
    - binding
    - draw command
    - diagnostics model
- [x] Create `slateui-minecraft` module for MC-specific implementation.
- [x] Move `SlateScreen` into Minecraft module.
- [x] Move `MinecraftDrawCommandRenderer` into Minecraft module.
- [x] Move `MinecraftTextMeasurer` into Minecraft module.
- [x] Move `Minecraft` clipboard integration into Minecraft module.
- [x] Keep loader-specific entrypoints in loader modules.
- [x] Add forbidden import checks for core.
- [x] Add architecture test: core must not import `net.minecraft`.

## 2.2 Host Abstraction
- [x] Add `SlateHost`.
- [x] Replace `SlateInteractionContext.screen()` with host methods.
- [x] Host should provide:
    - requestRebuild
    - requestFocus
    - clearFocus
    - openScreen
    - closeScreen
    - inspect
    - reportDiagnostic
- [x] Decouple command context from MC screen.
- [x] Add Minecraft host implementation.
- [x] Add test host implementation for unit tests.
- [x] Add fake host for authoring previews.

## 2.3 Renderer Adapter
- [x] Make `SlateRenderer` the only low-level render target.
- [x] Add `DrawCommandDispatcher`.
- [x] Convert `MinecraftDrawCommandRenderer` into `MinecraftSlateRenderer`.
- [x] Add renderer capability flags:
    - supportsRoundedRect
    - supportsScissor
    - supportsTextureRegion
    - supportsItemIcon
    - supportsTextComponent
- [x] Add fallback rendering path.
- [x] Add renderer tests with fake renderer.
- [x] Add command serialization for diagnostics.

## 2.4 Version Matrix
- [x] `LoaderId` exists.
- [x] `MinecraftVersionRange` exists.
- [x] `LoaderVersionSupport` exists.
- [x] `SlateCompatibilityMatrix` exists.
- [x] `SupportLevel` exists.
- [x] Fabric / Forge / NeoForge helper classes exist.
- [x] Convert matrix from documentation-style helper into tested release policy.
- [x] Add supported versions table in README.
- [x] Add CI jobs for supported MC versions.
- [x] Add smoke test mod per loader.
- [x] Define support levels:
    - supported
    - tested
    - considered
    - experimental
    - unsupported
- [x] Add version adapter notes for each MC version.
- [x] Add migration notes per MC version.

---

# Phase 3 — v0.3: Authoring, Compiler, and Resource Override
目标：让 `.slate` 成为轻量 authoring layer，适合 resource-pack override 和配置页布局，不把 runtime 变重。

## 3.1 `.slate` Compiler
- [x] `slateui-authoring` module exists.
- [x] `SlateCompiler` exists.
- [x] `SlateCompilerCli` exists.
- [x] `SlateCompileException` exists.
- [x] XML-like `.slate` syntax exists.
- [x] JSON IR output exists.
- [x] scoped style support exists.
- [x] component prop validation exists.
- [x] style prop validation exists.
- [x] invalid style selector rejection exists.
- [x] repeated component source mapping improved.
- [x] experimental directive warnings exist.
- [x] Improve source location for all errors.
- [x] Remove remaining `source.indexOf()`-style error positioning.
- [x] Add full parser location tracking.
- [x] Add strict unknown attribute policy.
- [x] Add schema export.
- [x] Add compiler warning levels.
- [x] Add compiler config file.
- [x] Add watch mode.
- [x] Add Gradle integration.
- [x] Add IDE syntax documentation.

## 3.2 Runtime IR Loader
- [x] `SlateIrLoader` exists.
- [x] `SlateIrRuntimeFactory` exists.
- [x] `SlateComponentRegistry` exists.
- [x] runtime component creation from IR exists.
- [x] built-in component registration exists.
- [x] Add IR version field.
- [x] Add IR migration layer.
- [x] Add IR compatibility checks.
- [x] Add source map propagation into runtime diagnostics.
- [x] Add safe fallback screen for broken IR.
- [x] Add runtime warnings for unsupported experimental features.
- [x] Add strict slot whitelist.
- [x] Prevent accepted-but-ignored named slots.

## 3.3 Authoring Scope
v0.3 stable authoring components:

- [x] `OverlayRoot`
- [x] `Box`
- [x] `Stack`
- [x] `Panel`
- [x] `Text`
- [x] `Button`
- [x] `Input`
- [x] `Toggle`
- [x] `Image`
- [x] `ScrollView`
- [x] `List`

Need:

- [x] Add UV props for `Image`.
- [x] Add texture size props for `Image`.
- [x] Add translatable text props.
- [x] Add tooltip props.
- [x] Add command payload props.
- [x] Add controlled input props.
- [x] Add validation errors for incomplete props.

Experimental authoring features:

- [x] `if` exists.
- [x] `for` exists.
- [x] `key` exists.
- [x] named slot support exists.
- [x] Make `for/key` semantics stable.
- [x] Add collection diffing tests.
- [x] Add keyed component lifecycle tests.
- [x] Add slot ownership validation.
- [x] Add authoring security docs.

## 3.4 Resource Override
- [x] `SlateOverrideRegistry` exists.
- [x] component override concept exists.
- [x] theme override concept exists.
- [x] Define override file locations.
- [x] Define resource pack integration.
- [x] Define override priority order.
- [x] Add safe override validation.
- [x] Add override diagnostics.
- [x] Add theme-only override mode.
- [x] Add layout-only override mode.
- [x] Add texture override examples.
- [x] Add modpack author docs.
- [x] Add resource pack compatibility tests.

---

# Phase 4 — v0.4: Better Component Library
目标：补齐常见 Screen UI 组件，但继续保持轻量。

## 4.1 Form Components
- [x] `Input` exists.
- [x] `Toggle` exists.
- [x] `Slider`
- [x] `NumberInput`
- [x] `Dropdown`
- [x] `RadioGroup`
- [x] `CheckboxGroup`
- [x] `ColorPicker`
- [x] `KeybindInput`
- [x] `SearchBox`
- [x] `TextArea`
- [x] `File-like Resource Picker`

## 4.2 Layout Components
- [x] `Stack` exists.
- [x] `Box` exists.
- [x] `Panel` exists.
- [x] `ScrollView` exists.
- [x] `Grid`
- [x] `SplitPane`
- [x] `Tabs`
- [x] `Accordion`
- [x] `TreeView`
- [x] `VirtualList`
- [x] `Spacer`
- [x] `Divider`
- [x] `Card`
- [x] `Toolbar`

## 4.3 Feedback Components
- [x] `Tooltip` exists as experimental.
- [x] `Popup` exists as experimental.
- [x] `Modal` exists as experimental.
- [x] `Toast`
- [x] `ProgressBar`
- [x] `Spinner`
- [x] `Badge`
- [x] `Alert`
- [x] `ConfirmDialog`
- [x] `ContextMenu`
- [x] `CommandPalette`

## 4.4 Minecraft-native Components
- [x] `ItemIcon`
- [x] `ItemStackView`
- [x] `FluidView`
- [x] `EntityPreview`
- [x] `RecipePreview`
- [x] `AdvancementIcon`
- [x] `KeybindLabel`
- [x] `ResourceLocationInput`
- [x] `ModIcon`
- [x] `PlayerHead`

---

# Phase 5 — v0.5: Container, Slot, and Server Intent
目标：开始进入 LDLib2 的核心强势区域，但先做轻量 container，而不是一口气做完整 editor ecosystem。

## 5.1 Slot UI
- [x] `SlotGrid` exists as experimental.
- [x] `ContainerSlot` exists.
- [x] `ContainerSlotProvider` exists.
- [x] `StaticContainerSlotProvider` exists.
- [x] Move SlotGrid to experimental package or document clearly.
- [x] Add real item rendering.
- [x] Add item tooltip support.
- [x] Add slot hover highlight.
- [x] Add click type support:
    - [x] left click
    - [x] right click
    - [x] shift click
    - [x] number key
    - [x] drag split
    - [x] double click
- [x] Add ghost slot mode.
- [x] Add filter slot mode.
- [x] Add locked slot mode.
- [x] Add slot validation.
- [x] Add slot accessibility diagnostics.

## 5.2 Server Intent
- [x] `SlateServerIntent` exists.
- [x] `SlateServerIntentBridge` exists.
- [x] `QueuedSlateServerIntentBridge` exists.
- [x] SlotGrid emits server intent in tests.
- [x] Define packet protocol.
- [x] Add Fabric networking implementation.
- [x] Add Forge networking implementation.
- [x] Add NeoForge networking implementation.
- [x] Add server-side validation hooks.
- [x] Add replay protection.
- [x] Add permission model.
- [x] Add intent result response.
- [x] Add optimistic UI update policy.
- [x] Add desync recovery policy.

## 5.3 Container Screen Integration
- [x] Add `SlateContainerScreen`.
- [x] Add menu/container binding.
- [x] Add player inventory component.
- [x] Add quick-move integration.
- [x] Add recipe book compatibility decision.
- [x] Add JEI/REI/EMI/XEI compatibility layer.
- [x] Add vanilla slot interop.
- [x] Add server-authoritative sync docs.
- [x] Add example machine UI.
- [x] Add example storage UI.

---

# Phase 6 — v0.6: HUD and World-space UI
目标：把 SlateUI 从 Screen-only 扩展到 HUD 和 world-space surface，但继续复用 core component tree、layout、style、render pipeline。

## 6.1 HUD
- [x] `SlateHudLayer` exists as experimental.
- [x] Define HUD lifecycle.
- [x] Add HUD anchoring:
    - [x] top-left
    - [x] top-right
    - [x] bottom-left
    - [x] bottom-right
    - [x] center
- [x] Add safe-area support.
- [x] Add scaling support.
- [x] Add hide/show conditions.
- [x] Add gameplay state binding.
- [x] Add config-driven HUD layout.
- [x] Add multiple HUD layers.
- [x] Add HUD performance budget diagnostics.

## 6.2 World-space UI
- [x] `WorldSpaceAnchor` exists as experimental.
- [x] `WorldSpaceProjection` exists as experimental.
- [x] `WorldSpaceSlateSurface` exists as experimental.
- [x] Define world-space coordinate model.
- [x] Add camera-facing billboard mode.
- [x] Add distance scaling.
- [x] Add occlusion policy.
- [x] Add frustum culling.
- [x] Add entity attachment.
- [x] Add block position attachment.
- [x] Add click/raycast interaction.
- [x] Add multiplayer sync policy.
- [x] Add performance diagnostics.
- [x] Add example entity label UI.
- [x] Add example block terminal UI.

---

# Phase 7 — v0.7: Tooling and Dev Experience
目标：把 SlateUI 做成调试体验优秀的 UI runtime。这里是和 LDLib2 缩小体验差距的关键阶段。

## 7.1 Inspector
- [x] `SlateInspectorScreen` exists.
- [x] component tree dump exists.
- [x] hit regions dump exists.
- [x] draw command dump exists.
- [x] binding dump exists.
- [x] runtime summary exists.
- [x] Add interactive component picker.
- [x] Add hover-to-inspect.
- [x] Add selected component panel.
- [x] Add live bounds overlay.
- [x] Add style inspector.
- [x] Add state inspector.
- [x] Add command log filter.
- [x] Add event log filter.
- [x] Add search component by path.
- [x] Add copy diagnostics button.

## 7.2 Hot Reload
- [x] `SlateReloadSupport` exists.
- [x] Add resource reload integration.
- [x] Add `.slate` reload on file change in dev.
- [x] Add theme reload.
- [x] Add override reload.
- [x] Preserve state across reload.
- [x] Show reload errors in overlay.
- [x] Add reload command.
- [x] Add development-only mode flag.

## 7.3 Visual Editor
目标：长期追近 LDLib2 的 editor 能力。

- [x] Build read-only visual preview first.
- [x] Add component tree panel.
- [x] Add property panel.
- [x] Add drag-to-resize.
- [x] Add drag-to-reorder.
- [x] Add style editing.
- [x] Add theme token picker.
- [x] Add command picker.
- [x] Add binding picker.
- [x] Add source map navigation.
- [x] Add export to `.slate`.
- [x] Add import from `.slate`.
- [x] Add in-game editor mode.
- [x] Add desktop/editor app investigation.

---

# Phase 8 — v0.8: Advanced Rendering and Layout
目标：支持更复杂、更漂亮、更像现代 UI 的界面。

## 8.1 Advanced Styling
- [x] Shadows.
- [x] Gradients.
- [x] Opacity.
- [x] Blur investigation.
- [x] Texture backgrounds.
- [x] 9-slice panels.
- [x] Animated style transitions.
- [x] CSS-like variables.
- [x] Pseudo-class styles:
    - [x] hover
    - [x] active
    - [x] focus
    - [x] disabled
    - [x] checked
    - [x] selected
- [x] Media/query-like scaling rules.
- [x] Theme variants:
    - [x] dark
    - [x] light
    - [x] high contrast
    - [x] modpack theme

## 8.2 Advanced Layout
- [x] Flex layout.
- [x] Grid layout.
- [x] Absolute overlay positioning.
- [x] Min/max constraints.
- [x] Intrinsic text sizing.
- [x] Virtualized list layout.
- [x] Sticky headers.
- [x] Scroll snapping.
- [x] Responsive layout presets.
- [x] Layout animation.

## 8.3 Animation
- [x] Animation clock.
- [x] Tween model.
- [x] Easing functions.
- [x] Hover transitions.
- [x] Modal enter/exit animation.
- [x] List item transition.
- [x] Progress animation.
- [x] Animation diagnostics.
- [x] Disable animations option.

---

# Phase 9 — v0.9: Ecosystem Integrations
目标：让 SlateUI 进入真实 mod 生态。

## 9.1 Mod Loader Integrations
- [x] Fabric entrypoint exists.
- [x] Forge entrypoint exists.
- [x] NeoForge entrypoint exists.
- [x] Add loader-specific examples.
- [x] Add platform service docs.
- [x] Add config screen registration helper.
- [x] Add ModMenu integration.
- [x] Add Cloth Config bridge or migration guide.
- [x] Add Forge config screen integration.
- [x] Add NeoForge config screen integration.
- [x] Add common registration API.

## 9.2 Recipe and Item Ecosystem
- [x] JEI integration.
- [x] REI integration.
- [x] EMI integration.
- [x] XEI compatibility investigation.
- [x] Item tooltip bridge.
- [x] Ingredient view component.
- [x] Recipe layout component.
- [x] Recipe transfer action model.
- [x] Ghost ingredient support.

## 9.3 Scripting and Modpack Authoring
- [x] KubeJS integration investigation.
- [x] CraftTweaker integration investigation.
- [x] Data-driven screen registration.
- [x] Resource-pack-only theme override.
- [x] Server-provided UI schema policy.
- [x] Security model for external UI definitions.
- [x] Sandboxed command model.
- [x] Pack validation CLI.

## 9.4 Kotlin DSL
- [x] `slateui-kotlin` module exists.
- [x] Kotlin DSL initial implementation exists.
- [x] Kotlin DSL tests exist.
- [x] Keep Kotlin DSL optional.
- [x] Add DSL docs.
- [x] Add DSL examples.
- [x] Add DSL parity tests with Java API.
- [x] Add DSL parity tests with `.slate`.
- [x] Avoid Kotlin runtime requirement for Java users.

---

# Phase 10 — v1.0 Stable
目标：SlateUI 可以稳定作为 mod dependency 发布。

## 10.1 v1.0 API Freeze
- [x] Define public API packages.
- [x] Define internal packages.
- [x] Add `@ApiStatus` annotations or equivalent.
- [x] Freeze stable component APIs.
- [x] Freeze style model.
- [x] Freeze theme token names.
- [x] Freeze command model.
- [x] Freeze authoring IR version.
- [x] Freeze supported loader/version matrix.
- [x] Add binary compatibility checks.
- [x] Add migration policy.

## 10.2 Required v1.0 Quality Bar
- [x] Clean build on CI.
- [x] Unit tests pass.
- [x] Integration tests pass.
- [x] Fabric smoke test passes.
- [x] Forge smoke test passes.
- [x] NeoForge smoke test passes.
- [x] Example mod builds.
- [x] Example mod launches.
- [x] No known memory leaks in screen lifecycle.
- [x] No known renderer crashes on supported versions.
- [x] No accepted-but-ignored `.slate` syntax.
- [x] README complete.
- [x] Architecture docs complete.
- [x] Migration docs complete.
- [x] License finalized.

## 10.3 v1.0 Stable Scope
Stable:

- [x] Screen runtime.
- [x] Core components.
- [x] Style/theme.
- [x] State/binding.
- [x] Commands.
- [x] Diagnostics.
- [x] `.slate` basic authoring.
- [x] Java API.
- [x] Texture/text rendering.
- [x] Input/focus.
- [x] Loader support for selected MC versions.

Experimental:

- [x] Container UI.
- [x] HUD.
- [x] World-space UI.
- [x] Visual editor.
- [x] Scripting integrations.
- [x] Advanced animation.

---

# Phase 11 — Post-1.0: Grow Toward LDLib2-class Platform
目标：从 lightweight Screen runtime 成长为完整 UI platform。这个阶段开始真正和 LDLib2 的能力范围接近。

## 11.1 Full UI Platform
- [ ] Stable Screen UI.
- [ ] Stable Container UI.
- [ ] Stable HUD UI.
- [ ] Stable World-space UI.
- [ ] Stable resource override.
- [ ] Stable theme ecosystem.
- [ ] Stable data-driven UI.
- [ ] Stable visual editor.
- [ ] Stable scripting bridge.
- [ ] Stable server sync.
- [ ] Stable ecosystem integrations.

## 11.2 Editor Ecosystem
- [ ] In-game visual editor.
- [ ] Component palette.
- [ ] Drag/drop layout editing.
- [ ] Slot UI editor.
- [ ] Theme editor.
- [ ] Resource override editor.
- [ ] Binding editor.
- [ ] Command editor.
- [ ] Preview state editor.
- [ ] Export/import pipeline.
- [ ] Editor plugin API.

## 11.3 Runtime Plugin System
- [ ] Third-party component registration.
- [ ] Third-party renderer extensions.
- [ ] Third-party command namespaces.
- [ ] Third-party theme packs.
- [ ] Third-party authoring macros.
- [ ] Versioned plugin API.
- [ ] Plugin compatibility checks.
- [ ] Plugin diagnostics.
- [ ] Plugin sandboxing policy.

## 11.4 Data-driven UI and Server-driven UI
- [ ] Server-authored UI schema.
- [ ] Client-side validation.
- [ ] Server-side command validation.
- [ ] Permission-aware components.
- [ ] Secure payload model.
- [ ] Sync protocol.
- [ ] Diff protocol.
- [ ] Optimistic update.
- [ ] Rollback.
- [ ] Desync diagnostics.

## 11.5 Performance and Scale
- [ ] Component virtualization.
- [ ] Render command batching.
- [ ] Dirty region rendering.
- [ ] Incremental layout.
- [ ] Partial rebuilds.
- [ ] State dependency tracking.
- [ ] Draw command caching.
- [ ] Texture cache.
- [ ] Text measurement cache.
- [ ] Large UI benchmark suite.

## 11.6 Ecosystem Maturity
- [ ] Official example mod.
- [ ] Official config screen template.
- [ ] Official machine UI template.
- [ ] Official HUD template.
- [ ] Official world-space label template.
- [ ] Official theme pack.
- [ ] Official migration guide from vanilla Screen.
- [ ] Migration guide from Cloth Config.
- [ ] Migration guide from LDLib-style UI.
- [ ] Community component registry.
- [ ] Documentation website.

---

# Immediate Next 20 Tasks
These are the next tasks that should happen before adding more large features.

- [x] Change license from All Rights Reserved.
- [x] Add README with Screen-first positioning.
- [x] Commit executable bit for `gradlew`.
- [x] Add CI for test/build.
- [x] Add `SlateHost`.
- [x] Remove `SlateScreen` from `SlateInteractionContext`.
- [x] Remove `Minecraft` / `Screen` from core `CommandContext`.
- [x] Make `SlateRenderer` the real render adapter.
- [x] Move Minecraft renderer code behind adapter.
- [x] Put `SlateText` into `DrawTextCommand`.
- [x] Add real translatable text rendering.
- [x] Add Image UV / region / texture size authoring props.
- [x] Add selection highlight to Input.
- [x] Add horizontal scroll to Input.
- [x] Add controlled input sync behavior.
- [x] Remove state listener on screen close.
- [x] Fix all remaining compiler error source locations.
- [x] Tighten named slot whitelist.
- [x] Mark experimental APIs in package or JavaDoc.
- [x] Add example config screen using Java API and `.slate`.

---

# v0.1 Alpha Definition of Done
SlateUI v0.1 alpha can be cut when all of these are true:

- [x] License is usable for a mod library.
- [x] README exists.
- [x] Build/test runs in CI.
- [x] Core Screen demo opens.
- [x] Gallery screen opens.
- [x] Java API example exists.
- [x] `.slate` example exists.
- [x] Core components are documented.
- [x] Style/token behavior is documented.
- [x] Image renders real textures.
- [x] Text supports literal text reliably.
- [x] Input has cursor, selection logic, paste, delete, commit.
- [x] Button missing command does not close screen.
- [x] Tab focus works.
- [x] Diagnostics screen works.
- [x] Compiler rejects unknown props.
- [x] Compiler does not silently accept unsupported slots.
- [x] Experimental APIs are marked.
- [x] Known limitations are listed.

---

# v1.0 Definition of Done
SlateUI v1.0 can be cut when all of these are true:

- [ ] Stable public API is defined.
- [ ] Core module is free of Minecraft imports.
- [ ] Minecraft adapter layer is clearly separated.
- [ ] Supported MC versions are tested in CI.
- [ ] Fabric / Forge / NeoForge support policy is clear.
- [ ] Core Screen components are stable.
- [ ] Text localization works.
- [ ] Texture rendering supports UV and 9-slice.
- [ ] Input is comfortable for config UI use.
- [ ] ScrollView is robust.
- [ ] Theme override is stable.
- [ ] `.slate` compiler has reliable errors.
- [ ] Runtime diagnostics are useful.
- [ ] Memory lifecycle is clean.
- [ ] Example mod demonstrates real-world usage.
- [ ] Migration guide exists.
- [ ] Release process is documented.

---

# Strategic Rule
Until v0.1 alpha is cut:

+ Do not add more major feature surfaces.
+ Do not expand visual editor work.
+ Do not make container UI stable.
+ Do not promise HUD/world-space stability.
+ Do not market SlateUI as an LDLib2 replacement.

Focus on:

+ Screen runtime stability.
+ Cross-version abstraction.
+ Text/Image/Input completeness.
+ Compiler honesty.
+ Documentation.
+ Release readiness.

After v1.0:

+ Start competing on breadth.
+ Grow container and editor.
+ Build ecosystem integrations.
+ Keep the core lightweight.
