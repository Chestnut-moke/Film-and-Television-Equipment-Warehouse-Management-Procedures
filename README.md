# 🎬 影视设备仓库管理系统 (Film & Video Equipment Warehouse)

[![Android Build](https://img.shields.io/badge/Platform-Android-green.svg?style=flat-square)](#)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg?style=flat-square)](#)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue.svg?style=flat-square)](#)
[![Theme-Forest](https://img.shields.io/badge/Theme-Forest--Green%20%26%20Notion--White-brightgreen.svg?style=flat-square)](#)

影视设备仓库管理系统是一款专为影视剧组、广告拍摄及个人工作室设计的、基于 **Kotlin** 与 **Jetpack Compose** 打造的单屏全功能设备流转和租借调度系统。

本应用采用了精美的 **Forest-Green（森林绿）与 Notion-White（极简砂白）** 撞色视觉风格，致力于提供轻量、高效、极简的就手体验，解决影视拍摄中多设备出库难跟踪、交还易遗漏、排期冲突等痛点。

---

## 🌟 核心特色与功能

### 📦 1. 设备档案与全生命周期管理
*   **档案维护**：支持名称、型号、唯一条形码/二维码、设备分类（如 *摄像机、镜头、灯光、音频、辅助支撑* 等）的一键登记。
*   **全机备注**：内置无级设备备注模块，可快速修改设备保管事项、配件清单等技术细节。
*   **流转状态指示灯**：卡片上直观显示设备的在库空闲状态，或显示当前租借人、借出时间、计划交还截止日期。

### 🔄 2. 扫码与手动双规极速出纳
*   **模拟/真实扫码出库**：支持通过扫码（或条码检索仿真）一击出库。一旦扫入在库条码，即可呼出借阅向导登记领用人并设定期限。
*   **极速手动出库登记**：对未贴签设备，在详情中可通过“手动借出”，输入合规的人数及续约滑动条迅速放行。
*   **一键扫码/手动退库归还**：支持针对已借出设备一键还库入仓，无需繁琐交接，状态精准恢复。

### 📅 3. 租期日程全览与单设备时间轴
*   **共享日历看板（Full Schedule Calendar）**：图形化列出每日已占用的借出详情、计划到期线。点击计划条可呼出流转交互菜单。
*   **单设备流转时间轴（Single-device Timeline）**：切换到单设备视角后，可快速选定设备查看其专属的使用波段、历史使用者。
*   **合同展期与快速续租**：支持在日程视图里直接点击某个借出中的设备进行 **续租 (Extend Lease)** 操作（1天~30天滑动条递增，新还期动态预估），或者进行**一键交还（Return）** 归档。
*   **到期/逾期强提醒**：系统内置时间差计算机制，绿色友好展示“还租倒计时”，若有逾期则红色高亮报错“已逾期 X 天”。

### 📈 4. 数据穿透：高频/长短租使用数据排行
*   **周转率看板**：清晰列出所有设备在历史中被调配的**周转频次（Frequency）**以及**累计出库借出总时长（Duration）**。
*   **精准统计**：按次数、按天数自动降序生成排行榜，哪些是“大热抢手爆款”，哪些是“冷门吃灰常客”一目了然，方便进行淘汰或追加采购决策。

---

## 🎨 极简美学设计规范 (Design Language)

本系统由深邃的**深林绿（Forest Green）**和干净的**Notion现代白（Clean White）**为主基调设计：
-   **文字排版 (Typography)**：标题使用高对比度的大字号排版，数据与历史周转频次标签配以优雅淡色。
-   **暗色模式 (Dark/Night Support)**：完美适配 Android 系统日夜模式切换。在暗色模式下，背景自动化为高级墨黑，卡片变更为石墨灰，高频文字和徽章则使用淡薄荷绿高亮，全天候保障调色现场或暗房中的辨识度。
-   **交互反馈 (Micro-interaction)**：出库、归还、续租等关键节点配备了流畅的滑动输入、气泡卡片与优雅的 Material 3 Ripple 微动。

---

## 🛠️ 技术底座与依赖 (Tech Stack)

*   **开发语言**：Kotlin
*   **UI 框架**：Jetpack Compose (声明式 UI，Material Design 3 规范)
*   **局部&生命周期监听**：`collectAsStateWithLifecycle`
*   **持久化层**：基于 Room Database 实现的本地 SQLite 长期存储。设备在库状态、领用历史、租约时间线均提供事务性落盘保障，关机/退后台数据不丢失。
*   **构建系统**：Gradle Kotlin DSL + Version Catalog 集中化版本控制。

---

## 📂 项目模块结构 (Project Structure)

```text
/app/src/main/java/com/example/
│
├── MainActivity.kt        # 主活动：集成 Edge-to-Edge 四主屏状态导航
│
├── data/                  # 数据模块 (Room & 仓储)
│   ├── AppDatabase.kt     # Room 数据库统一入口
│   ├── Equipment.kt       # 设备实体定义 (字段：id, name, model, barcode, status, renter...)
│   ├── RentalRecord.kt    # 借出日志留存实体 (记录借出、交还、延期时间戳)
│   ├── EquipmentDao.kt    # 数据库读写 DAO
│   └── EquipmentRepository.kt # 仓储，包装合并底层事务，实现租地、展期、退还业务逻辑
│
└── ui/                    # 表现层 (Views, ViewModels)
    ├── WarehouseViewModel.kt # 核心状态流 (StateFlow 封装过滤、新增、出退库、扫码等)
    │
    ├── theme/             # 主题配色 (日夜模式视觉自适应)
    │   ├── Color.kt       # 极简 Notion / 墨绿配色的颜色梯度定制
    │   ├── Theme.kt       # M3 Light / Dark ColorScheme 动态重合
    │   └── Type.kt        # 文本字型匹配
    │
    └── screens/           # 页面流
        ├── WarehouseScreen.kt # 仓库主仓、添加设备、一键登记
        ├── ScannerScreen.kt   # 扫码拟真枪出库
        ├── CalendarScreen.kt  # 租期日程、一键交还与办理续约弹窗
        └── HistoryScreen.kt   # 周转排行与系统日志
```

---

## 🚀 开启本地构建和运行 

要在本地或串流模拟器中编译并启动本项目，请按以下步骤操作：

### 1. 配置环境
确保您本地安装了：
*   **Android Studio** (推荐 Jellyfish 或 Iguana 以上版本)
*   **JDK 17** (Gradle 8+ 等现代 AGP 环境的基石)

### 2. 构建与运行命令
您可以直接在项目根目录下，使用 Gradle 工具链执行构建：

```bash
# 1. 运行本地 Lint 检测和代码合规性
gradle :app:lintDebug

# 2. 执行内置的单元和 JUnit 数据层流转测试
gradle test

# 3. 编译并解包生成 APK
gradle assembleDebug
```

---

## 📖 交互流程推荐 (Quick Guide)

1.  **添加一架新摄像机**：
    在「仓库首仓」轻触右上角 `+ 新增装备`，输入名称 *“RED V-RAPTOR XL 8K”*，型号输入 *“XL V-Mount”*，条码任意输入如 *“8k-v-001”*。

2.  **办理借出**：
    在设备详情卡片点击 `手动借出`，或直接前往「扫码出库」页面，模拟扫描 *“8k-v-001”*。输入借用人 *“灯光李组长”*，选择借出天数为 *7天*。你会发现设备卡片状态瞬间切换成了酷炫的“橙黄色借出中”。

3.  **办理延期与还库**：
    前往「设备日程」或在「设备时间轴」中锁定此摄像机。点击日程条，在随之拉起的 `租赁流转管理` 弹窗中：
    -   点击 `办理续租` 左右拖动滑动条即可无感延长借用期。
    -   点击 `一键退库归还`，设备恢复闲置并自动进入仓库。

4.  **复盘周到率统计**：
    随时轻触底部导航中的「周转分析」，直观阅览排行榜，随时随地掌握影视资产的损耗与流转频率。

---

🌿 *设计灵感：Forest for focus, Notion for clarity. 影视设备的高效周转，始于行云流水。*
