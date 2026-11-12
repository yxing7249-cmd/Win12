# Windows 12 TV Desktop (Android TV Launcher & Virtual Mouse)

这是一个专为 **Android TV（智能电视/电视盒子）** 设计的 **Windows 12 风格桌面启动器 (Launcher)**。通过内置的智能虚拟光标系统，解决传统电视大屏上应用难以用普通遥控器自由点击、拖动和浏览网页的痛点。

## ✨ 核心功能特点

1. **Fluent Design 桌面环境**：支持动态流体壁纸、毛玻璃拟态居中任务栏 (Dock)、开始菜单及多主题切换。
2. **智能虚拟鼠标系统**：
   - 支持电视遥控器方向键平滑移动光标（带长按加速）。
   - 支持确认键点击波纹特效，并能穿透触发底层所有按钮、列表与网页。
   - 支持**按住拖动（Drag & Drop）**与**页面滚动模式**（菜单键切换）。
3. **丰富的桌面小组件**：
   - 实时硬件状态监控（内存、CPU 负载）、日历时钟与快捷控制中心。
4. **安全与便捷控制**：
   - 内置家长控制密码锁屏保护（默认密码 `1234`）。
   - 支持开机自启动（`BootReceiver`）。
5. **GitHub Actions 自动构建**：
   - 内置 `.github/workflows/android-build.yml`，推送代码到 `main` 分支后可在 GitHub Actions 页面自动编译并下载 APK。

## 🚀 上传到 GitHub 并自动编译 APK

1. 在 GitHub 上新建一个空仓库（例如 `Win12TvLauncher`）。
2. 解压本压缩包，进入项目根目录执行以下命令推送代码：
   ```bash
   git init
   git add .
   git commit -m "Initial commit: Win12 TV Desktop"
   git branch -M main
   git remote add origin https://github.com/你的用户名/Win12TvLauncher.git
   git push -u origin main
   ```
3. 推送完成后，打开 GitHub 仓库的 **Actions** 标签页，等待云端自动编译完成即可在 **Artifacts** 中直接下载 `Win12TvLauncher-debug-apk` 安装包！
