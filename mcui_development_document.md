# SlateUI 开发文档

## 0. 当前结论

SlateUI 第一阶段定位为：

> 一个面向 Minecraft Mod Screen 的 Java-first、无重型依赖、Vue-inspired、Minecraft-native 组件式 UI Runtime。

更短的描述：

```text
Vue-like authoring, Minecraft-native runtime.
```

它不是 HTML/CSS/JS 浏览器环境，也不是 Vue runtime 的移植。它借鉴 Vue 的组件组合、props、slot、状态绑定、事件模型和 scoped style 思路，但底层仍然使用 Minecraft 原生的渲染、输入、资源、文本和 Screen 生命周期。

SlateUI 的目标是让 Minecraft Mod UI 开发从：

```text
手写坐标
手写 widget
手写事件回调
难以复用的 Screen 代码
```

升级成：

```text
组件树
props
children / slot
state binding
command bridge
scoped style
debuggable runtime
```

第一阶段重点不是模板语言，也不是完整 compiler，而是先把组件 runtime 做稳。

---

## 1. 项目定位

### 1.1 正式定位

```text
SlateUI is a Minecraft-native, Vue-inspired component UI runtime for mod screens.
```

中文：

```text
SlateUI 是一个面向 Minecraft Mod Screen 的原生组件式 UI Runtime。
```

它提供：

- Java-first 组件 API；
- 可选 Kotlin DSL；
- 后续可选 `.slate` 模板工作流；
- Vue-inspired component model；
- props；
- children / default slot；
- 后续 named slot；
- state binding；
- event-to-command；
- scoped style；
- Minecraft-native layout / render / input；
- Fabric / NeoForge adapter。

### 1.2 正确心智

SlateUI 应该让用户感觉自己在写：

```text
组件化 UI
```

而不是写：

```text
静态 HTML-like 标签树
```

示意：

```text
<Screen>
  <Panel title="Settings">
    <ToggleRow label="Enable feature" value="{config.enabled}" />
    <Button on:click="screen.close">Close</Button>
  </Panel>
</Screen>
```

这里的重点不是尖括号语法，而是：

- `Panel` 是组件；
- `ToggleRow` 是组件；
- 父组件可以传 props；
- 子组件可以接收 children；
- 事件可以触发 command；
- 数据可以从 StateProvider 绑定进 UI；
- 组件可以复用。

### 1.3 不是什么

SlateUI 不是：

- Minecraft 内嵌浏览器；
- Chromium / CEF / MCEF 替代品；
- Vue runtime for Minecraft；
- React runtime for Minecraft；
- 完整 HTML/CSS/JS runtime；
- DOM / CSSOM；
- npm component ecosystem；
- 任意 JS eval 平台；
- WebView；
- 小浏览器内核。

对外表达必须避免：

```text
HTML UI
Browser UI
DOM UI
WebView UI
```

更准确的表达是：

```text
Vue-inspired component authoring for Minecraft-native UI.
```

---

## 2. 核心原则

### 2.1 Component-first

SlateUI 的架构中心是 Component，而不是 HTML node。

核心概念顺序应该是：

```text
Component
Props
Children / Slot
State
Event
Command
Lifecycle
Layout
DrawCommand
Minecraft Renderer
```

不是：

```text
HTML tag
CSS selector
DOM tree
Browser layout
```

### 2.2 Java-first

第一版主入口必须是 Java API。

原因：

- Minecraft mod 生态 Java 用户最多；
- 不强迫用户引入 Kotlin runtime；
- 不强迫用户理解模板编译器；
- 不引入 JS runtime；
- 更适合作为前置库传播；
- 更容易调试 MVP runtime。

Kotlin DSL 可以做，但必须是可选模块。

### 2.3 Runtime-first

第一阶段先做组件 runtime，再做 `.slate` compiler。

原因：

- compiler 只是生成 component tree / IR 的工具；
- runtime 不稳时，compiler 会放大复杂度；
- Java API 可以作为早期用户入口；
- 未来 `.slate`、Kotlin DSL、Java builder 都可以进入同一套 component runtime。

### 2.4 Screen-first

第一阶段只服务普通 Minecraft Screen。

暂不进入：

- Container / Slot；
- HUD overlay；
- World-space UI；
- server-driven UI。

原因：Screen 是最容易闭环、最容易演示、最容易被 mod 作者采用的入口。

### 2.5 No heavy runtime dependency

运行时不强依赖：

- Architectury；
- Kotlin；
- Chromium / CEF；
- JS engine；
- Vue runtime；
- React runtime；
- 大型 Web runtime。

可以参考 Architectury 的多 Loader 项目结构，但不把 Architectury 作为用户必须安装的 runtime 依赖。

### 2.6 IR 是内部协议

IR 可以存在，但不是普通用户 API。

IR 用于：

- compiler；
- hot reload；
- cache；
- diagnostics；
- devtools；
- resource-pack override；
- schema versioning。

普通 mod 作者不应该手写 IR，也不应该被迫理解 IR。

---

## 3. 为什么从 HTML-like 改成 Vue-inspired

### 3.1 HTML-like 容易停在静态 UI

HTML-like 思路容易把问题收窄成：

```text
Box
Text
Button
Image
Grid
CSS
```

这更像“画界面”，不够像“开发 UI 系统”。

Minecraft Mod UI 实际需要：

- 状态变化；
- 条件显示；
- 列表渲染；
- 控件复用；
- 表单组件；
- 设置项组件；
- 任务卡片组件；
- 能量条组件；
- 父子组件通信；
- 局部交互状态。

这些更适合 Component Model。

### 3.2 Vue-inspired 更能形成生态

HTML-like 系统的生态偏：

```text
模板
主题
样式
```

Vue-inspired 系统的生态可以是：

```text
组件库
表单库
配置页组件
任务 UI 组件
机器 UI 组件
图标组件
布局组件
调试工具
```

例如未来可以出现：

- SlateUI Forms；
- SlateUI Config；
- SlateUI Quest Components；
- SlateUI Machine Components；
- SlateUI Mod Menu Bridge。

### 3.3 避免被浏览器标准绑架

如果主打 HTML/CSS，用户会自然期待：

- full Flexbox；
- full CSS Grid；
- `:has`；
- `nth-child`；
- media query；
- DOM API；
- JS runtime；
- npm packages。

如果主打 Vue-inspired component authoring，重点变成：

- 组件；
- props；
- slot；
- binding；
- event；
- command；
- scoped style。

这更符合 SlateUI 能承诺的范围。

---

## 4. Vue-like 到什么程度

### 4.1 应该借鉴的部分

SlateUI 应该借鉴：

- component composition；
- props；
- default slot；
- named slot，后续；
- local component state；
- state binding；
- event binding；
- conditional rendering，后续；
- list rendering，后续；
- keyed list identity；
- scoped style；
- component lifecycle；
- template compiler 思路。

### 4.2 不应该做的部分

SlateUI 第一阶段不做：

- Vue runtime；
- JavaScript reactive proxy；
- full Vue SFC compatibility；
- browser DOM；
- virtual DOM compatibility；
- arbitrary script execution；
- npm ecosystem compatibility；
- full template directive compatibility；
- full CSS compatibility。

### 4.3 正确边界

```text
Vue-like composition model
Java/Kotlin backend logic
Typed expression binding
Command bridge
Minecraft-native renderer
```

不是：

```text
Vue running inside Minecraft
```

---

## 5. 用户心智模型

### 5.1 三个用户动作

普通 mod 作者只需要理解三件事：

```text
1. 写组件树
2. 提供 StateProvider
3. 注册 Command
```

底层 layout、draw command、renderer backend、event propagation、IR schema 都应该隐藏。

### 5.2 与 Tauri 的类比

Tauri 模型：

```text
Frontend UI
  ↓ invoke
Rust backend command
```

SlateUI 模型：

```text
Java API / .slate component template
  ↓ command bridge
Mod Java/Kotlin logic
  ↓ optional server validation
Minecraft-native runtime/render/input
```

这是工作流类比，不是技术栈复刻。

---

## 6. 第一阶段正式范围

### 6.1 必做

MVP 0 / MVP 1 范围：

- Java component API；
- Fabric 1.21.x；
- NeoForge 1.21.x；
- component tree；
- props；
- children / default slot；
- Box；
- Text；
- Stack；
- Button；
- Image；
- Input；
- ScrollView；
- OverlayRoot 基础设施；
- local UI state；
- StateProvider binding；
- client-side Command Bridge；
- typed style；
- theme token；
- DrawCommand pipeline；
- debug rect overlay；
- error screen；
- example gallery。

### 6.2 暂不做

- real JS runtime；
- Vue runtime；
- `.vue` compatibility；
- 完整 `.slate` compiler；
- 完整 scoped style compiler；
- Container / Slot；
- World-space UI；
- HUD；
- 完整 CSS；
- 完整 DevTools；
- 复杂动画；
- Forge 1.20.1；
- 全 Minecraft 版本兼容；
- 完整 Gradle plugin；
- IDE plugin。

---

## 7. 模块结构

当前仓库在 MVP 0 阶段暂不做 Gradle 子模块拆分。

现阶段采用现有多 Loader 骨架推进：

- `common`：承载当前 MVP 0 的 API、runtime、layout、render、debug、demo；
- `fabric`：Fabric 入口与元数据；
- `neoforge`：NeoForge 入口与元数据；
- `forge`：当前不作为 MVP 0 正式目标，但 common 改动尽量保持可编译。

等 MVP 0 runtime 闭环稳定后，再考虑把代码从 `common` 拆分到 `slateui-core`、`slateui-minecraft` 等长期模块。

### 7.1 slateui-core

纯 UI 内核。

职责：

- component model；
- component instance tree；
- props model；
- children / slot model；
- local state model；
- lifecycle；
- layout model；
- style model；
- event model；
- command model；
- binding model；
- draw command model。

限制：

- 不依赖 Minecraft；
- 不依赖 Fabric；
- 不依赖 NeoForge；
- 不依赖 Kotlin；
- 不依赖 Architectury；
- 不依赖 LWJGL；
- 不依赖 JS engine。

### 7.2 slateui-minecraft

Minecraft 抽象层。

职责：

- Screen lifecycle bridge；
- renderer abstraction；
- input abstraction；
- text abstraction；
- resource abstraction；
- font metrics；
- scissor bridge；
- texture bridge；
- GUI scale bridge。

它可以知道 Minecraft 概念，但不绑定具体 Loader。

### 7.3 slateui-fabric

Fabric glue。

职责：

- Fabric entrypoint；
- Fabric resource reload hook；
- Fabric mod metadata；
- Fabric config dir；
- environment side；
- open screen bridge。

### 7.4 slateui-neoforge

NeoForge glue。

职责：

- NeoForge mod entry；
- event bus bridge；
- resource reload hook；
- mod metadata；
- config dir；
- environment side；
- open screen bridge。

### 7.5 slateui-authoring

第二阶段重点。

职责：

- `.slate` parser；
- component template compiler；
- style parser；
- scoped style compiler；
- diagnostics；
- IR serialization；
- source location；
- basic hot reload；
- Gradle task / plugin integration。

### 7.6 slateui-kotlin

可选 Kotlin DSL。

限制：

- 不能被 core 依赖；
- 不能成为基础 runtime 必需依赖；
- 只能作为 Java API 上的一层语法糖。

### 7.7 slateui-examples

示例与测试用 mod。

必须包含：

- simple component screen；
- nested component demo；
- props demo；
- settings-like screen；
- scroll list；
- theme demo；
- input demo；
- error demo；
- debug overlay demo。

---

## 8. Component Model

### 8.1 Component

所有 UI 都是组件。

内置基础组件：

- Box；
- Text；
- Stack；
- Button；
- Image；
- Input；
- ScrollView；
- OverlayRoot。

用户自定义组件示例：

- Panel；
- ToggleRow；
- SliderRow；
- QuestCard；
- EnergyBar；
- FluidTank；
- MachineStatusPanel；
- TabView。

### 8.2 Props

Props 是父组件传给子组件的只读输入。

原则：

- props 不应被子组件直接修改；
- 修改请求通过 event/command 发出；
- props 可以来自 literal，也可以来自 binding。

示意：

```text
<ToggleRow label="Music" value="{settings.music}" />
```

### 8.3 Children / Default Slot

组件必须能接收 children。

示意：

```text
<Panel title="Settings">
  <Text value="Audio" />
  <ToggleRow label="Music" value="{settings.music}" />
</Panel>
```

第一阶段至少支持 default slot。

Named slot 后续支持。

### 8.4 Named Slot

后续支持：

```text
<Panel>
  <template #header>
    <Text value="Settings" />
  </template>

  <template #default>
    <ToggleRow ... />
  </template>

  <template #footer>
    <Button ... />
  </template>
</Panel>
```

Named slot 不进 MVP 0。

### 8.5 Local Component State

组件可以拥有 UI 交互状态。

允许：

- focused；
- pressed；
- hovered；
- inputDraft；
- scrollOffset；
- dropdownOpen；
- selectedTab。

不允许用 local state 持有服务端权威业务状态。

### 8.6 Component Lifecycle

内部生命周期：

```text
create
mount
update props
measure
layout
event
build draw commands
unmount
dispose
```

普通用户不需要全部感知。自定义组件作者需要。

---

## 9. State / Binding 模型

### 9.1 状态分层

```text
Local UI State
Client Snapshot State
Server Authoritative State
```

### 9.2 原则

```text
组件可以拥有交互状态。
业务状态由 mod/backend/provider 拥有。
服务端权威状态只能由服务端确认。
```

### 9.3 StateProvider

第一版使用简单稳定的 StateProvider：

- screen 打开时传入 provider；
- binding 从 provider 只读数据；
- command 请求修改；
- provider notify dirty；
- runtime 局部刷新相关 binding / component。

不做完整 Vue reactivity。

### 9.4 Binding Expression

支持纯表达式，不允许副作用。

第一阶段可支持：

- path access；
- literal；
- boolean；
- comparison；
- string concat；
- ternary，后续；
- simple formatting。

示例：

```text
{player.name}
{machine.energy}
{count + '/' + max}
{enabled ? 'On' : 'Off'}
```

不允许：

- 任意 JS eval；
- 文件访问；
- 网络请求；
- 修改状态；
- 调用未注册函数。

### 9.5 Binding 更新策略

第一版推荐：

```text
Provider 手动 notify dirty path
Runtime 局部刷新绑定
必要时提供 tick fallback
```

不建议第一版做自动 proxy 或深层依赖追踪。

### 9.6 Computed / Watch

后续可以支持 computed。

Watch 要谨慎，容易引入副作用和调试复杂度。

MVP 不做 watch。

---

## 10. Event / Command 模型

### 10.1 Event

组件可以发事件：

- click；
- change；
- submit；
- focus；
- blur；
- scroll；
- key down；
- char typed。

事件可以：

- 被组件自己处理；
- 传给父组件；
- 触发 command。

### 10.2 Command Bridge

Command 是 UI 和 mod 逻辑之间的边界。

第一版只做 client command。

例子：

- screen.close；
- tab.switch；
- sound.play；
- local.toggle。

长期 command 字段：

- id；
- side；
- capability；
- argument schema；
- handler；
- source policy。

Command side：

```text
client-only
server-intent
restricted / dangerous
```

### 10.3 Server Authoritative Command

后续 server-authorized command 流程：

```text
UI event
  ↓
command
  ↓
server intent packet
  ↓
server validate
  ↓
server state update
  ↓
client provider refresh
  ↓
UI update
```

UI 不能直接修改服务端权威状态。

### 10.4 Event Propagation

第一版至少支持：

- hit test；
- target dispatch；
- consume；
- parent fallback。

长期可以加入 capture / bubble，但不需要完整复刻 DOM event。

重点场景：

- Button 内部 Text 被点击时，Button 应响应；
- ScrollView 应消费滚轮；
- Overlay/Modal 应拦截外部点击；
- Focus 应稳定且可调试。

---

## 11. Template / Authoring Model

### 11.1 文件后缀

建议使用：

```text
.slate
```

而不是：

```text
.html
.vue
.mcui
```

原因：

- `.html` 会让用户期待浏览器兼容；
- `.vue` 会让用户期待 Vue SFC 兼容；
- `.mcui` 偏旧定位，不够体现 SlateUI 品牌和组件系统。

### 11.2 `.slate` 是组件模板，不是 HTML

`.slate` 应表达 SlateUI component tree。

它可以长得像：

```text
<template>
  <Screen>
    <Panel title="Settings">
      <ToggleRow label="Music" value="{settings.music}" />
      <Button on:click="screen.close">Close</Button>
    </Panel>
  </Screen>
</template>

<style scoped>
  .panel { ... }
</style>
```

但第一阶段不承诺：

- Vue SFC 兼容；
- JS script；
- full CSS；
- DOM。

### 11.3 Script 策略

MVP 不做 `<script>`。

后续如果需要，可以考虑：

- metadata-only script；
- typed declaration；
- command declaration；
- props declaration。

不建议核心 runtime 引入真实 JS。

### 11.4 条件渲染

后续支持类似：

```text
<Text if="{machine.active}" value="Running" />
```

或者使用 SlateUI 自己的 directive。

不必完全模仿 Vue 指令。

### 11.5 列表渲染

后续支持：

```text
<QuestCard for="quest in quests" key="quest.id" quest="{quest}" />
```

必须有 key 概念。

原因：

- 保留 focus；
- 保留 local state；
- 支持局部更新；
- 支持未来 animation；
- 降低 diff 复杂度。

---

## 12. Layout 设计

### 12.1 第一版布局

支持：

- StackRow；
- StackColumn；
- Absolute；
- Overlay；
- ScrollView。

FixedGrid 放 MVP 1 或 MVP 2。

不承诺完整 Flexbox / CSS Grid。

### 12.2 Measurement Contract

所有组件必须遵守测量协议：

```text
measure(availableSize) -> desiredSize
layout(finalRect)
```

需要考虑：

- min size；
- preferred size；
- max size；
- content size；
- parent constraints；
- text measurement；
- image intrinsic size；
- scroll content size。

### 12.3 GUI Scale

定义：

```text
px = Minecraft GUI scaled coordinate unit
```

不要把 UI px 和 framebuffer pixel 混淆。

### 12.4 ScrollView

ScrollView 是第一阶段重要组件。

至少需要：

- clip；
- wheel event；
- content measurement；
- scroll offset；
- focus into view，后续；
- nested scroll，后续；
- virtualized list，后续。

---

## 13. Style 设计

### 13.1 第一版做 typed style

不做完整 CSS。

支持属性：

- width；
- height；
- minWidth；
- minHeight；
- maxWidth；
- maxHeight；
- margin；
- padding；
- gap；
- background；
- border；
- radius；
- opacity；
- text color；
- align；
- overflow；
- hover；
- active；
- focus；
- disabled。

### 13.2 单位

- px：Minecraft GUI scaled unit；
- %：parent relative；
- auto；
- content；
- slot：18px，先预留给未来 inventory/container。

### 13.3 Theme Token

基础 token：

- color.surface；
- color.primary；
- color.text；
- color.muted；
- color.border；
- radius.sm；
- radius.md；
- radius.lg；
- spacing.xs；
- spacing.sm；
- spacing.md；
- spacing.lg；
- animation.fast，预留。

### 13.4 Scoped Style

组件模板中的 style 默认应 scoped。

避免不同 mod 之间 `.panel`、`.title` 全局污染。

全局主题应通过 token，而不是全局 selector。

---

## 14. Render Pipeline

组件不直接调用 Minecraft renderer。

固定流程：

```text
Component Instance Tree
  ↓
Runtime Tree
  ↓
Layout Tree
  ↓
DrawCommand List
  ↓
Minecraft Renderer Backend
```

DrawCommand 应支持：

- push clip；
- pop clip；
- draw rect；
- draw border；
- draw text；
- draw image；
- draw debug rect；
- 后续 draw slot。

需要 layer / z-order / pass 概念：

- background；
- content；
- foreground；
- overlay；
- tooltip；
- debug。

---

## 15. Text / i18n / Accessibility

### 15.1 Text 抽象

TextNode 不应只存普通 String。

应预留：

- literal text；
- translatable text；
- bound text；
- formatted text。

底层桥接 Minecraft text/component 系统。

### 15.2 i18n

模板和 Java API 都应鼓励使用 translation key。

支持：

- static translation key；
- bound translation args；
- fallback literal。

### 15.3 Narration / Accessibility

第一版不必完整实现，但组件模型要预留：

- role；
- narration label；
- focus order；
- disabled reason。

Button/Input 尤其需要预留。

---

## 16. Adapter 设计

Adapter 分两层，不混在一起。

### 16.1 LoaderAdapter

负责 Loader 差异：

- mod loaded check；
- mod metadata；
- config dir；
- resource reload hook；
- environment side；
- logger bridge；
- optional integration detection。

### 16.2 MinecraftVersionAdapter

负责 Minecraft 版本差异：

- draw text；
- draw texture；
- fill rect；
- scissor；
- font metrics；
- screen size；
- input bridge；
- resource location；
- translation text；
- GUI scale coordinate conversion。

### 16.3 为什么分开

如果不分开，会得到大量组合类：

```text
Fabric121Adapter
Fabric1215Adapter
NeoForge121Adapter
NeoForge1215Adapter
```

更好的模型是：

```text
FabricLoaderAdapter + Mc121VersionAdapter
NeoForgeLoaderAdapter + Mc121VersionAdapter
```

---

## 17. Resource Pack 与安全

资源包可以覆盖：

- 视觉资源；
- texture；
- theme token；
- style，后续；
- layout，后续；
- 文案。

资源包不能：

- 提升 capability；
- 新增危险 command；
- 访问本地文件；
- 发任意网络请求；
- 执行脚本；
- 绕过 server validation。

所有 template/style/IR 应携带 schema version。

Runtime 发现版本不兼容时，应显示明确错误，不应直接 crash。

---

## 18. Error / Diagnostics

第一版必须有错误恢复。

必须支持：

- error screen；
- missing resource fallback；
- missing style fallback；
- unknown command warning；
- binding failure placeholder；
- layout failure boundary；
- component tree dump；
- layout dump；
- command log；
- debug rect overlay。

目标：UI 坏了时，开发者知道哪里坏，而不是只看到空屏或 crash。

---

## 19. DevTools 路线

### MVP 0

- debug rect overlay；
- component tree dump；
- layout dump；
- command log；
- error screen。

### MVP 1 / 2

- hover inspector；
- computed style viewer；
- binding viewer；
- focus viewer；
- dirty flag viewer；
- component props viewer。

### MVP 3+

- full in-game inspector；
- hot reload；
- source map；
- profiler；
- draw command viewer。

完整 DevTools 很重要，但不能阻塞 MVP 0 runtime 闭环。

---

## 20. Public API 分层

### 20.1 Stable Public API

给普通 mod 作者使用，必须少而稳：

- SlateUI；
- SlateScreen；
- Component builder；
- StateProvider；
- CommandContext；
- Theme；
- open screen；
- command registration。

### 20.2 Experimental API

可以破坏性更新，但必须明确标记：

- custom component；
- custom layout；
- custom style property；
- animation；
- authoring/compiler integration；
- resource-pack override API。

### 20.3 Internal SPI

不对普通用户承诺兼容：

- renderer backend；
- loader adapter；
- Minecraft version adapter；
- IR loader；
- draw command executor；
- event dispatcher；
- layout internals；
- computed style internals。

---

## 21. MVP 路线图

### MVP 0：组件 runtime 闭环

目标：Fabric + NeoForge 都能打开同一个简单组件式 UI。

包含：

- Java component API；
- component tree；
- props；
- children / default slot；
- Box；
- Text；
- Stack；
- Button；
- typed style；
- client command；
- draw command pipeline；
- debug rect overlay；
- error screen；
- example screen。

验收标准：

```text
能打开一个 Screen。
开发环境标题页能看到 SlateUI MVP0 调试按钮。
点击调试按钮能打开示例 Screen。
Screen 中有嵌套组件。
父组件能给子组件传 props。
组件能接收 children。
点击 Button 能触发 command，比如关闭 screen。
debug overlay 能显示 layout rect。
Fabric 和 NeoForge 1.21.x 都能运行。
```

### MVP 1：真实可用组件系统

加入：

- Image；
- Input；
- ScrollView；
- StateProvider；
- simple binding；
- local component state；
- theme token；
- hover / active / focus；
- example gallery。

验收标准：

```text
能做一个真实 mod 设置页 / 信息页。
能显示动态 state。
能输入文本。
能滚动列表。
主题 token 可替换。
组件能复用。
```

### MVP 2：Slate template

加入：

- `.slate` template；
- component syntax；
- props binding；
- default slot；
- scoped style subset；
- compiler output JSON IR；
- diagnostics；
- source location；
- basic hot reload；
- Gradle task。

验收标准：

```text
开发者可以不用 Java builder 写 UI，而是用 .slate 创建组件式 Screen。
错误信息能定位到文件和行列。
模板能编译到同一套 component runtime。
```

### MVP 3：Vue-like 进阶能力

加入：

- conditional rendering；
- list rendering；
- keyed update；
- computed；
- named slot；
- component registry；
- tooltip / popup / modal；
- Kotlin DSL；
- early inspector。

### MVP 4：Minecraft 深水区

加入：

- server intent command；
- Container / Slot；
- HUD；
- World-space UI；
- advanced inspector；
- resource-pack component/theme override；
- multi-version support；
- Forge 1.20.1 consideration。

---

## 22. 测试策略

### 22.1 Core tests

- component creation；
- props passing；
- children / slot projection；
- local state update；
- style resolution；
- layout measurement；
- event hit test；
- command dispatch；
- binding resolution；
- dirty update。

### 22.2 Snapshot tests

推荐加入：

```text
Component tree -> layout rect snapshot
Component tree -> draw command snapshot
```

这比一开始做真实截图回归更容易。

### 22.3 Example mod tests

每个 supported loader 至少跑：

- simple screen；
- nested component screen；
- props screen；
- input screen；
- scroll screen；
- theme screen；
- error screen。

---

## 23. 发布策略

玩家看到：

```text
SlateUI - Fabric
SlateUI - NeoForge
```

开发者 Maven 看到：

```text
slateui-core
slateui-minecraft
slateui-fabric
slateui-neoforge
slateui-authoring
slateui-kotlin
```

README 必须强调：

```text
No Architectury required.
No Kotlin required.
No Chromium required.
No JavaScript runtime required.
Java-first.
Vue-inspired component model.
Minecraft-native renderer.
Fabric + NeoForge first.
```

---

## 24. 文档策略

内部文档可以讲架构。

对外文档必须按任务组织：

- Create your first SlateUI screen；
- Create a reusable component；
- Pass props to a child component；
- Use children / default slot；
- Add a button command；
- Bind Java state to text；
- Style a component；
- Add an input；
- Add a scroll list；
- Debug layout；
- Package your mod；
- Fabric setup；
- NeoForge setup。

不要一上来讲 IR、draw command、layout tree、computed style。

---

## 25. 命名定案

项目名：

```text
SlateUI
```

模块前缀：

```text
slateui-
```

模板后缀：

```text
.slate
```

对外 slogan 候选：

```text
Vue-like authoring for Minecraft-native UI.
```

或者：

```text
A component UI runtime for Minecraft mods.
```

避免：

```text
HTML UI
DOM UI
Browser UI
WebView UI
Vue for Minecraft
```

---

## 26. 当前最终决策

项目从这里开始，不再按 HTML-like UI 设计。

第一步只做 MVP 0：

```text
Java component API
组件树
props
children / default slot
Box / Text / Stack / Button
点击 Button 触发 client command
开发环境标题页注入 SlateUI MVP0 调试按钮
debug overlay 显示 layout rect
Fabric + NeoForge 1.21.x 都能跑
```

MVP 0 成功后，再进入 MVP 1：Input、ScrollView、StateProvider、local state、theme token、example gallery。

`.slate` compiler、hot reload、DevTools、Container、World-space 全部后移。

---

## 27. 一句话总结

> SlateUI 是一个 Java-first、Vue-inspired、无重型依赖、面向 Minecraft Mod Screen 的原生组件式 UI Runtime。

它的价值不是“在 Minecraft 里写 HTML”，而是让 Minecraft Mod UI 开发从手写坐标和事件回调，升级成组件化、可复用、可绑定状态、可调试、可扩展的原生 UI 开发体验。
