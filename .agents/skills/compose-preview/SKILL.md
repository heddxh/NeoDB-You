---
name: compose-preview
description: Creates Compose Preview functions for composables. Use when asked to add preview, create preview, or generate @Preview for Compose UI.
---

# Compose Preview

为 Jetpack Compose 函数创建预览。

## 基本用法

```kotlin
@Preview
@Composable
private fun XxxPreview() {
    AppTheme {
        Xxx()
    }
}
```

## @Preview 注解参数

| 参数                | 说明              | 示例                                                         |
|-------------------|-----------------|------------------------------------------------------------|
| `name`            | 预览名称            | `@Preview(name = "Dark Mode")`                             |
| `group`           | 分组名称            | `@Preview(group = "Buttons")`                              |
| `widthDp`         | 宽度 (dp)         | `@Preview(widthDp = 320)`                                  |
| `heightDp`        | 高度 (dp)         | `@Preview(heightDp = 640)`                                 |
| `showBackground`  | 显示背景            | `@Preview(showBackground = true)`                          |
| `backgroundColor` | 背景色 (ARGB Long) | `@Preview(backgroundColor = 0xFFFFFFFF)`                   |
| `showSystemUi`    | 显示系统 UI         | `@Preview(showSystemUi = true)`                            |
| `device`          | 设备规格            | `@Preview(device = "id:pixel_5")`                          |
| `uiMode`          | UI 模式           | `@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)`       |
| `locale`          | 语言              | `@Preview(locale = "zh-rCN")`                              |
| `fontScale`       | 字体缩放            | `@Preview(fontScale = 1.5f)`                               |
| `wallpaper`       | 壁纸              | `@Preview(wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE)` |

## Multipreview 注解

创建自定义多预览注解以复用配置：

```kotlin
@Preview(name = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class ThemePreviews

@Preview(name = "Phone", device = "spec:width=411dp,height=891dp")
@Preview(name = "Tablet", device = "spec:width=1280dp,height=800dp,dpi=240")
annotation class DevicePreviews
```

使用：

```kotlin
@ThemePreviews
@DevicePreviews
@Composable
private fun MyComponentPreview() {
    AppTheme {
        MyComponent()
    }
}
```

## 设备规格

### 预定义设备

- `id:pixel_5`, `id:pixel_fold`, `id:pixel_tablet`
- `id:wearos_small_round`, `id:wearos_large_round`

### 自定义规格

```kotlin
@Preview(device = "spec:width=411dp,height=891dp,dpi=420")
@Preview(device = "spec:width=1280dp,height=800dp,orientation=landscape")
```

## 项目约定

1. **Preview 函数命名**: `XxxPreview` 或 `PreviewXxx`
2. **使用 AppTheme**: 始终用 `AppTheme { }` 包裹预览内容
3. **私有函数**: Preview 函数应为 `private`
4. **Mock 数据**: 使用假数据填充预览，不依赖 ViewModel
5. **放置位置**: 在同一文件中，放在被预览组件下方

## 工作流程

1. 找到需要预览的 Composable 函数
2. 分析其参数，确定需要的 mock 数据
3. 在函数下方创建 Preview 函数
4. 用 `AppTheme` 包裹
5. 根据需要添加多个 `@Preview` 变体（亮/暗主题、不同尺寸等）

## 示例

### 简单组件预览

```kotlin
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
private fun GreetingPreview() {
    AppTheme {
        Greeting("Android")
    }
}
```

### 状态变体预览

```kotlin
@Preview(name = "Empty")
@Preview(name = "Loading")
@Preview(name = "Content")
@Composable
private fun MyScreenPreview() {
    AppTheme {
        // 根据需要展示不同状态
    }
}
```

### 亮暗主题预览

```kotlin
@PreviewLightDark
@Composable
private fun CardPreview() {
    AppTheme {
        MyCard(title = "Sample", content = "Preview content")
    }
}
```
