<div align="center">
    <img alt="App Logo" width="200" height="200" style="display: block;" src="../icon.svg"/>
</div>
<div align="center">
    <a href="https://github.com/heddxh/NeoDB-You/actions/workflows/nightly.yml">
    <img src="https://img.shields.io/github/actions/workflow/status/heddxh/neodb-you/nightly.yml?style=for-the-badge&amp;logo=github&amp;label=Nightly" alt="Nightly build"/>
    </a>
    <a href="https://github.com/heddxh/NeoDB-You/blob/main/LICENSE">
    <img src="https://img.shields.io/github/license/heddxh/neodb-you?style=for-the-badge" alt="License"/>
    </a>
    <a href="https://github.com/heddxh/NeoDB-You/releases/latest">
    <img src="https://img.shields.io/github/v/release/heddxh/neodb-you?include_prereleases&amp;style=for-the-badge&amp;label=Version&amp;color=%238DCAFF" alt="Version"/>
    </a>
    <br>
    <img src="https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&amp;logo=kotlin&amp;logoColor=white" alt="Kotlin"/>
    <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?logo=jetpackcompose&amp;logoColor=fff&amp;&amp;style=for-the-badge" alt="Jetpack Compose"/>
    <img src="https://img.shields.io/badge/android%20studio-346ac1?style=for-the-badge&amp;logo=android%20studio&amp;logoColor=white" alt="Android Studio"/>
</div>

# NeoDB You

一个使用 Jetpack Compose 和 Material 3 构建的原生 Android 版 [NeoDB](https://neodb.net/) 应用。

> NeoDB 是一个自2021年以来开源的软件和全球社区平台。
> 它帮助用户管理和发现各种文化产品的收藏、评论和评分，
> 包括书籍、电影、音乐、播客、游戏和表演。
> 此外，用户可以分享他们的收藏，发布微博，并与 Fediverse 中的其他人互动。
> NeoDB 整合了 Goodreads、Letterboxd、RateYourMusic 和 Podchaser 等平台的功能。
> 它还通过容器化部署和 ActivityPub 协议支持自托管和互联。

这个应用是我在业余时间开发的。如果你喜欢它，请给我点个 Star ;)

## 截图

<img alt="home" height="400" src="img/cn/home.png"/><img alt="library.png" height="400" src="img/cn/library.png"/><img alt="settings.png" height="400" src="img/cn/settings.png"/><img alt="detail.png" height="400" src="img/detail.png"/>

## 功能

- 登录你喜欢的 [NeoDB 服务器](https://neodb.net/servers/)。
- 查看服务器上的热门条目。
- 使用关键词搜索条目。
- 访问任何条目的详细信息和评论。
- 撰写和修改你自己的评论。
- 通过热力图可视化你的媒体库。

## 下载

<a href="https://github.com/heddxh/NeoDB-You/releases/latest">
<img alt="Get it on GitHub" height="96" 
src="https://raw.githubusercontent.com/rubenpgrady/get-it-on-github/refs/heads/main/get-it-on-github.png"/>
</a>
<a href="https://apt.izzysoft.de/packages/day.vitayuzu.neodb">
<img height="96" alt="Git it on IzzyOnDroid" 
src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroid.png" />
</a>

每夜版: [GitHub Actions](https://github.com/heddxh/NeoDB-You/actions/workflows/nightly.yml)（解压产物并安装
APK）。

## 本地化

[![Weblate](https://hosted.weblate.org/widget/neodb-you/multi-auto.svg)](https://hosted.weblate.org/engage/neodb-you/)

想帮助将应用翻译成你的语言吗？请访问我们的 [Weblate 页面](https://hosted.weblate.org/engage/neodb-you/)。

## 常见问题

1. **登录时为什么会显示 “The requested scope is invalid, unknown, or malformed.”？**

   请键入你的 **NeoDB** 服务器域名，而不是你的 Fediverse/Mastodon 域名。对于中文用户，很可能是
   `neodb.social`。
   对于英文用户，很可能是 `eggplant.place`。

2. **在我的服务器上某些功能无法正常使用。**

   目前只在 [neodb.social](https://neodb.social/)
   和 [eggplant.place](https://eggplant.place/) 上进行了测试。
   如果你在使用其他服务器时遇到问题，请提交一个 issue！

## 致谢

### 开源项目

- [AboutLibraries](https://github.com/mikepenz/AboutLibraries)
- [Compose Shimmer](https://github.com/valentinilk/compose-shimmer)
- [Version Compare](https://github.com/G00fY2/version-compare)
- [Ktorfit](https://github.com/Foso/Ktorfit)
- [Coil](https://github.com/coil-kt/coil)

### 特别鸣谢

![NeoDB](https://avatars.githubusercontent.com/u/92268656?s=200&v=4)

感谢 [NeoDB](https://neodb.net/) 为 Fediverse 打造了这个令人难以置信的平台。

<img alt="Piecelet logo" width="200" height="200" src="https://github.com/Piecelet/neodb-app/raw/main/NeoDB/NeoDB/Assets.xcassets/AppIcon.appiconset/1024x1024%20copy%202%401x.png"> 

感谢 [Piecelet](https://github.com/Piecelet/neodb-app) 激励我创建这个项目。

![LineageOS](https://upload.wikimedia.org/wikipedia/commons/thumb/e/e5/LineageOS_Wordmark.svg/375px-LineageOS_Wordmark.svg.png)

感谢 [LineageOS](https://lineageos.org/)，没有他们我就没有运行 Android15 的实体机了（Pixel 4 XL）。
