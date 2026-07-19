# NeoDB You - Agent Instructions

[NeoDB](https://neodb.net/) 的原生 Android 客户端，管理书籍、电影、游戏等文化产品的收藏和评价。

技术栈：Kotlin 2.4 / Jetpack Compose + Material 3 Expressive / Navigation 3 / Hilt / Ktorfit + Ktor /
Coil 3 / Coroutines & Flow / DataStore。单模块项目（只有 `:app`，无根 `build.gradle.kts`），依赖统一在
`gradle/libs.versions.toml`，需要 JDK 21。

## 构建与验证

简单更改或 UI 迭代不需要构建验证；大型更改和重构需要构建确认正确性。

```bash
./gradlew :app:compileDebugKotlin  # 只编译 Kotlin，最快的正确性检查
./gradlew :app:testDebugUnitTest   # JVM 单元测试（app/src/test）
./gradlew assembleDebug            # Debug 构建（applicationId 后缀 .debug，可与正式版共存）
./gradlew assembleFastRelease      # Release 但跳过 R8/资源压缩/lint，用于快速验证
./gradlew assembleRelease          # 完整 Release（CI 使用；无 keystore.properties 时产出未签名 APK）
./gradlew lint                     # Android Lint
```

- 单元测试是纯 JVM 的（无 `src/androidTest`，无模拟器）。`isReturnDefaultValues = true`：未 mock 的
  `android.*` 方法（如 `android.util.Log`）返回默认值而不是抛异常；依赖真实 Android 框架的代码（如
  `android.icu`、`android.net.Uri`）无法在单元测试覆盖，不要为它们写 JVM 测试。
- 修改纯逻辑（`util/`、schema→model 映射、Repository 数据处理）时**应补充/更新对应单元测试**。
- CI：`.github/workflows/check.yml` 在 PR 以及 `main`、`claude/**` 分支 push 时运行单元测试 + lint。
- Ktlint **未接入 Gradle**：格式规则全在 `.editorconfig`（含 compose-rules 配置），`./gradlew lint`
  不检查格式。

## 必须遵守的约定

- 格式以 `.editorconfig` 为准，另遵守 [compose-rules](https://mrmans0n.github.io/compose-rules/)
  。无通配符导入，行宽 100，缩进 4 空格。
- ViewModel 状态用 `MutableStateFlow`（不用 `MutableState`），并使用 Kotlin explicit backing fields：
  `val uiState: StateFlow<T>` 声明 + `field = MutableStateFlow(...)`。
- 导航：路由定义在 `util/AppNavigator.kt`（`sealed interface AppDestination`），跳转用
  `LocalNavigator.current goto <destination>`。
- `EntryType`、`ShelfType` 枚举值用小写命名（如 `book`），因为直接拼进 API 路径。
- 数据流向：`data/schema/`（`@Serializable` API 响应）→ Repository（返回 `Flow`，用 `.log()` 扩展记日志）→
  `ui/model/`（UI 层模型）。

## i18n

- 所有用户可见字符串放入 `res/values/strings.xml`；**添加或修改字符串后，完成时告诉我**。
- 英语（默认）和简体中文可自行添加；其他语言由社区维护，**不要提供翻译**。

## 依赖

- 添加新依赖前**必须征得我的同意**，使用 Version Catalog 格式写入 `libs.versions.toml`。

## Git

- `main` 保持稳定可发布；non-trivial 更改（多文件、新功能、重构）先从 `main` 创建分支：`feature/xxx`、
  `fix/xxx`、`refactor/xxx`、`chore/xxx`。
- Conventional Commits，原子提交：重构与功能分开，schema 更改单独提交，UI 与业务逻辑分开；strings.xml
  可随相关代码一起提交。
- 完成后等待我确认，再合并到 `main`。

## 其他

- OAuth 回调通过 deep link `day.vitayuzu.neodb://auth` 进入 `OauthActivity`（scheme 来自 manifest
  placeholder，等于 applicationId）。
- 发布前需手动递增 `app/build.gradle.kts` 中的 `versionCode`。
