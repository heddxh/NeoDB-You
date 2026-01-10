## 2026.01.10

> > 创建一个新的 worktree，帮我解决上架 fdroid
> 遇到的这个问题：https://gitlab.com/fdroid/fdroiddata/-/merge_requests/31338#note_2994919666 要求：
> - 写完代码不需要构建，我来执行
> - 详细阅读整一个 MR 的对话，查阅 Fdroid 相关文档，充分了解上下文

```txt
Agent: OpenCode 1.1.11
Gemini: Gemini Paid API
Claude: GitHub Copilot
GPT: GitHub Copilot
Tech Stack: Kotlin & Android & Jetpack Compose
```

需要做的任务：

- 修复崩溃。在连接 NeoDB 服务失败的情况下，首页应该静默失败而不是直接崩溃。
- 选择加入检查更新，以符合 F-Droid 的规则。

**结果**：

- Claude Opus 4.5
    - ✅一轮理解，找到问题并尝试修复
        - 首次访问 MR 评论链接失败后自己意识到可以用 GitLab API，成功得到信息。
    - ⛔自己画了一个图标但是 xml 格式错了，编译失败。
    - ⛔Opt-In 的配置项最终没有读，也就是说没修……
- Gemini 3.0 Pro
    - ⛔无法访问 MR 评论链接并主动向我要求提供
    - ✅复制给他后顺利理解并尝试修复问题并通过编译
    - ✅完成后在自己的 worktree 中创建了提交。
    - ⛔修复了崩溃，但是 Opt-In 的配置项最终没有在所有调用检查更新的地方读，启动还是自动检查了更新。
- GPT 5.2
    - ⛔无法访问 MR 评论链接，但是自己幻想了一个完全不相关的问题
    - ✅复制给他后顺利理解并修复问题
    - ⛔完成任务，但是引入了回归

最终三个模型都选择了添加一个 Opt-In 的检查更新配置项来完成任务，同时都修复了评论中提到的另一个崩溃问题。

回到主要任务，也就是默认不检查更新，具体而言原本程序中有两个地方会自动检查更新：启动 App
和进入设置页面，同时设置界面还可以手动触发。Claude 不知道在干什么，Gemini 忽略了 `MainActivity`，而 GPT
“聪明”的选择了在定义处读取配置项，但这会导致手动触发失效。

**总结**：在两轮对话以内，没有任何一个模型完美的完成任务。所以在 2026 年的年初，面对一个玩具 Android
项目，Coding Agent 已经 Production-Ready 了吗？

- 没有。
- 如果程序员仔细测试，审阅、监督的话，是可以提升效率的。

**What about all those tools?** 虽然没有穷尽的尝试所有工具，但我觉得 Agent 辅助编码至少还缺少以下功能：

- 真正的 Human-In-Loop。在 Agent 工作的时候我的信息会被
  pending，理想状态下我可以随时监督，评论，打断他的工作，并且他会在接受我的评论后自动继续工作。
- 代码审阅。如果不依赖 GitHub PR 等云端服务的话，本地/IDE/Agent 都没有提供很好的，可以与 LLM
  交互的代码审阅工具。比如，逐行/区域评论 LLM 生成的代码。
    - [GitHub - yoshiko-pg/difit: A lightweight command-line tool that spins up a local web server to display Git commit diffs in a GitHub-like Files changed view](https://github.com/yoshiko-pg/difit)
