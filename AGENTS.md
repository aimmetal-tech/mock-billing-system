# 设计模式大作业

## 项目目标和内容简介

`docs/`目录下存放的是设计模式课程的PPT和大作业要求

**题目选择**：题目（2）

**题目大意**："题目（2）讲的是把一份能跑但结构烂掉的现成代码，在不改变任何功能的前提下，重构成由 5 种以上设计模式支撑的清晰类结构，并交出一整套能证明"行为等价"的前后对比证据与文档。"

实现：校园一卡通（CampusCard）计费与账户系统，Java 17 + Maven 多模块、零第三方依赖，并列保留重构前 `bad-system/` 与重构后 `refactored-system/` 两套可运行代码，共用同一套场景脚本与同一个 Web 演示壳。

## 仓库结构

| 目录 | 内容 |
| --- | --- |
| `docs/` | 课程要求文档与参考 PPT（`设计模式大作业模板.doc`、`设计模式评分细则.docx`、`设计模式课程命题说明.docx`、`参考刘伟设计模式ppt/`） |
| `bad-system/` | 重构前代码（上帝类 `CampusCardSystem` + 全局可变 `AppConfig`，含植入缺陷） |
| `refactored-system/` | 重构后代码（按职责分层、按模式分包：model / config / state / pricing / validation / notify / account / gateway / service / app） |
| `scenarios/` | 6 个场景脚本（JSON），两套系统共用，用于逐字符等价比对 |
| `uml/` | PlantUML 源（`.puml`）与渲染图（`rendered/*.png`），文档与 PPT 的图源 |
| `web/` | Vue 3 CDN 单页演示壳（左右并排请求重构前 8081 / 重构后 8082） |
| `projects/defense-ppt_20261019/` | 答辩 PPT 工程，详见其 `README.md` |
| `README.md` | 仓库入口：环境要求、构建与运行、控制台/场景比对/单元测试/Web 演示的启动说明 |
| `.agents/skills/` | 本地技能包（PPT Master 等），非交付内容 |

`target/`、`projects/**/backup/`、`projects/**/live_preview/` 为构建/运行时产物，可随时清理。

## 要求文档与交付物

**要求文档**（只读参考，勿改）：`docs/` 下三份要求文件与参考 PPT。

**交付物**（根目录，按提交规范命名）：

| 文件 | 规范 |
| --- | --- |
| `2026设计模式+X组+文档.pdf` | 大作业文档，PDF 统一格式 |
| `2026设计模式+X组+文档.docx` | 文档可编辑母本（提交以 PDF 为准） |
| `2026设计模式+X组+答辩PPT.pptx` | 答辩 PPT（源工程见 `projects/defense-ppt_20261019/`） |
| `2026设计模式+X组+项目`（打包名） | 项目源码包，含 `bad-system/`、`refactored-system/`、`scenarios/`、`uml/`、`web/` |

提交时把上述内容放进一个压缩包，命名为 `2026设计模式+X组`。

**整理约定**：文档转换的中间产物（`*.conversion_profile.json`、转换出的 `*.md`、`*_files/`）不要留在根目录与 `docs/`；它们已归档进 `projects/defense-ppt_20261019/sources/`。

## 文档书写和格式要求

对于大作业模板，当填写.docx的时候，不要直接对其修改，应该先复制一份，并命名好，再修改。原有的字体和目录必须保证不变，新增加的文字必须符合模板的要求。

**字体要求**：

默认中文字体使用宋体。

像"1. 项目介绍"这种标题就采用宋体二号字体，接着的"1.1"就采用宋体三号字体。
