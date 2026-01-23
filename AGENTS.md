# NeoDB You - Project Context

## Project Overview

**NeoDB You** 是一个 [NeoDB](https://neodb.net/) 的原生 Android 客户端，用于管理书籍、电影、游戏等文化产品的收藏和评价。

### Key Technologies

| 类别 | 技术栈 |
|------|--------|
| Language | Kotlin 2.3+ |
| UI | Jetpack Compose + Material 3 Expressive |
| Navigation | AndroidX Navigation 3 |
| DI | Hilt |
| Networking | Ktorfit + Ktor Client |
| Image | Coil 3 |
| Async | Kotlin Coroutines & Flows |
| Persistence | DataStore Preferences |
| Build | Gradle Kotlin DSL + Version Catalogs |

## Building and Running

**永远不要自己构建项目**。完成修改后，告诉我 "请构建项目以验证更改" 即可。

## Architecture

```
app/src/main/kotlin/day/vitayuzu/neodb/
├── MainActivity.kt          # 主入口，Navigation 3 设置
├── OauthActivity.kt          # OAuth2 认证处理
├── NeoDBYouApp.kt            # Application，Hilt @HiltAndroidApp
├── data/
│   ├── schema/               # API 响应数据类 (kotlinx.serialization)
│   │   └── detail/           # 详情页专用 schema
│   ├── RemoteSource.kt       # Ktorfit API 接口定义 (NeoDbApi)
│   ├── NeoDBRepository.kt    # 数据仓库，暴露 Flow
│   ├── AuthRepository.kt     # 认证逻辑
│   ├── AppSettingsManager.kt # DataStore 偏好管理
│   └── NetworkHiltModule.kt  # Hilt 网络模块
├── ui/
│   ├── component/            # 可复用 Compose 组件
│   ├── model/                # UI 层数据模型 (Entry, Mark, Post, Detail)
│   ├── page/                 # 页面
│   │   ├── home/             # 首页 (HomeScreen + HomeViewModel)
│   │   ├── library/          # 收藏库 (LibraryPage + LibraryViewModel)
│   │   ├── detail/           # 详情页 (DetailPage + DetailViewModel)
│   │   ├── search/           # 搜索页
│   │   ├── settings/         # 设置页
│   │   └── onboarding/       # 引导页
│   └── theme/                # Material 3 主题 (Color, Type, Shape, Transition)
└── util/
    ├── AppNavigator.kt       # Navigation 3 路由定义 (sealed interface AppDestination)
    ├── EntryType.kt          # 条目类型枚举 (book, movie, tv, music, game...)
    ├── ShelfType.kt          # 书架类型枚举 (wishlist, progress, complete, dropped)
    ├── Constant.kt           # 常量
    └── TimeDateHelper.kt     # 日期时间工具
```

## Key Patterns & Conventions

### Navigation 3

路由在 `AppNavigator.kt` 中定义：
```kotlin
sealed interface AppDestination : NavKey
data object Home : TopLevelDestination
data object Library : TopLevelDestination  // RequireLogin
data object Settings : TopLevelDestination
data object Search : AppDestination
data class Detail(val type: EntryType, val uuid: String) : AppDestination
```

导航使用 `LocalNavigator.current goto <destination>`。

### Compose UI

- **Modifier 位置**: `modifier: Modifier` 作为最后一个必需参数
- **状态管理**: ViewModel 使用 `MutableStateFlow`，不用 `MutableState`
- **收集状态**: `val uiState by viewModel.uiState.collectAsStateWithLifecycle()`
- **SharedTransition**: 使用 `LocalSharedTransitionScope` 和 `sharedBoundsTransition()`

### ViewModel 模式

```kotlin
@HiltViewModel
class XxxViewModel @Inject constructor(
    private val repository: NeoDBRepository,
) : ViewModel() {
    val uiState: StateFlow<XxxUiState>
        field = MutableStateFlow(XxxUiState())  // 使用 Kotlin 2.3 explicit backing fields
}

data class XxxUiState(...)
```

### Networking (Ktorfit)

API 接口定义在 `RemoteSource.kt` 的 `NeoDbApi` 接口中：
```kotlin
@GET("me/shelf/{type}")
suspend fun fetchMyShelf(@Path("type") type: ShelfType, @Query("page") page: Int): PagedMarkSchema
```

### Data Layer

- **Schema**: `data/schema/` 中定义 API 响应数据类，使用 `@Serializable`
- **UI Model**: `ui/model/` 中定义 UI 层使用的数据类，从 Schema 转换
- **Repository**: 返回 `Flow<T>`，使用 `.log()` 扩展记录日志

### Hilt Modules

- `NeoDBYouApp.kt`: `AppScopeModule` (CoroutineScope, IO Dispatcher)
- `NetworkHiltModule.kt`: API 和 HttpClient
- `AppSettingsManager.kt`: `DataStoreModule`

## Code Style

### 严格遵守的规则

1. **EditorConfig + Ktlint**: 参见 `.editorconfig`
2. **无通配符导入**: `ij_kotlin_name_count_to_use_star_import = 2147483647`
3. **Compose 规则**: https://mrmans0n.github.io/compose-rules/
4. **最大行长**: 100 字符
5. **缩进**: 4 空格

### i18n

- 所有用户可见字符串必须放入 `res/values/strings.xml`
- 如果添加或修改字符串，**请在完成时告诉我**
- 支持的语言: 英语(默认), 简体中文, 意大利语, 葡萄牙语(巴西)

### 枚举命名

`EntryType` 和 `ShelfType` 使用小写命名 (如 `book`, `movie`)，因为直接用于 API 路径。

## Dependencies

依赖定义在 `gradle/libs.versions.toml`：
- 添加新依赖前**必须征得我的同意**
- 添加依赖时使用 Version Catalog 格式

## Git Workflow

1. **分支命名**: `feature/xxx`, `fix/xxx`, `refactor/xxx`
2. **Commits**: 使用 Conventional Commits (`feat:`, `fix:`, `docs:`, `refactor:`)
3. **不要直接提交到 main**

## Common Tasks Quick Reference

| 任务 | 关键文件 |
|------|----------|
| 添加新页面 | `ui/page/xxx/`, `AppNavigator.kt`, `MainActivity.kt` |
| 添加 API | `RemoteSource.kt` (NeoDbApi), `NeoDBRepository.kt` |
| 添加新 Schema | `data/schema/` |
| 修改主题 | `ui/theme/` |
| 添加组件 | `ui/component/` |
| 修改设置 | `AppSettingsManager.kt`, `ui/page/settings/` |
| 添加字符串 | `res/values/strings.xml` |
