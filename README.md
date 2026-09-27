# TimezoneApp

当前版本：1.9，原生 Android 世界时钟（英文系统显示 Timezone），支持真实时区时间与桌面小组件。

- APK：`dist/TimezoneApp.apk`，Android 8.0 及以上。
- 构建：`powershell -ExecutionPolicy Bypass -File .\build.ps1`。
- 列表左侧统一显示地球图标，城市名下只显示国家，右侧显示实时 HH:MM 和 UTC 偏移，自动适用夏令时。
- 添加页支持中英文搜索、滚动选择、保存、取消与重复提示。城市列表保存在本机。
- 左滑后点删除，2 秒内可以撤销；右滑后添加桌面卡片。点击或长按城市也可打开操作菜单。
- 右上角设置可选择跟随系统、中文、English，并保存选择。
- 桌面卡片默认 2×1，黑白底色使用同一透明度；每张卡片只能从已添加城市中选择，点击桌面卡片重新配置。配置页选中栏显示城市与 UTC，弹窗左侧为地球图标、右侧为当地时间。
- 若桌面不支持应用内固定请求，长按桌面空白处 → 小组件 → 世界时钟（英文系统为 Timezone），拖入后选择城市和样式。
- 应用和桌面卡片均离线工作，时间读取设备时钟和系统时区数据库。
- 365 个可选城市按当前 UTC 偏移升序，添加页首次进入以北京为可视区域顶部锚点。城市照片不再显示或打包进 APK；历史照片及来源记录仍保留在项目中。
- 原独立 UI 预览保留：`preview/assets/index.html`（模拟数据，与正式 APK 分离）。
- 需求与阶段安排：`docs/需求开发文档.md`；节点记录：`docs/开发日志.md`。

使用已有 Java / Android SDK，无下载依赖；脚本只编译、打包和检查签名，不运行测试。签名密钥保存在本地 `build/preview.keystore`，不要删除，以便覆盖安装后续 APK。正式版包名为 `com.cantonadong.timezone`。由于 Android 将不同包名视为不同应用，旧版 `com.timezoneapp.preview` 的本地数据和桌面部件不会迁移到新版。

小组件使用系统 TextClock，宿主负责分钟更新，不依赖应用后台每分钟唤醒。参考：[TextClock](https://developer.android.com/reference/android/widget/TextClock)、[小组件配置](https://developer.android.com/develop/ui/views/appwidgets/configuration)。

城市照片来自 Wikipedia 免费许可页面图片。添加或替换城市时可运行 `D:/Program/Python/python.exe tools/fetch_city_photos.py` 更新离线图片、预览映射和署名信息。正常 APK 构建不需要网络。
