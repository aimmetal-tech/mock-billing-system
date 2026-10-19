# 校园一卡通计费与账户系统的设计模式重构

> 2026 设计模式课程大作业 · X 组 · 题目（2）：已有项目应用设计模式进行重构

把一个「能跑但结构烂掉」的校园一卡通计费与账户系统，在不改变任何外部功能的前提下，重构成由十余种设计模式支撑的清晰分层结构，并用同一套场景脚本逐字符比对证明行为等价。

仓库并列保留两套可运行代码：重构前 `bad-system/`（上帝类 + 巨型 switch + 散落校验）与重构后 `refactored-system/`（按职责分层、按模式分包）。

## 环境要求

| 项 | 要求 |
| --- | --- |
| JDK | 17（已验证 17.0.12） |
| Maven | 3.8+，用于构建与跑测试；**若模块已构建过**，可直接用 `java -cp <模块>/target/classes` 运行，无需 Maven |
| 依赖 | 零第三方运行时依赖；仅测试期用 JUnit 5 |

## 快速开始

### 1. 构建

```bash
# 在仓库根目录执行（父工程聚合两个模块）
mvn -q clean package
```

### 2. 控制台演示（重构前 / 重构后各跑一次，对比同一组操作）

```bash
mvn -q -pl bad-system exec:java          # 重构前：Main
mvn -q -pl refactored-system exec:java   # 重构后：ConsoleApp
```

已构建过、没有 Maven 时可直接运行：

```bash
java -cp bad-system/target/classes edu.dlut.campuscard.bad.Main
java -cp refactored-system/target/classes edu.dlut.campuscard.app.ConsoleApp
```

### 3. 行为等价：同一场景脚本，双执行器逐字符比对

场景脚本在 `scenarios/`，两套执行器读取同一份、输出格式完全相同的归一化文本。

```bash
# 用已编译的类直接跑（路径相对仓库根目录）
java -cp bad-system/target/classes        edu.dlut.campuscard.bad.BadScenarioRunner scenarios/basic_consume.json > out-bad.txt
java -cp refactored-system/target/classes edu.dlut.campuscard.app.SceneRunner     scenarios/basic_consume.json > out-good.txt

# 无差异输出 = 逐字符一致
Compare-Object (Get-Content out-bad.txt) (Get-Content out-good.txt)
```

用 Maven 运行时覆盖入口类与参数：

```bash
mvn -q -pl bad-system exec:java -Dexec.mainClass=edu.dlut.campuscard.bad.BadScenarioRunner -Dexec.args="../scenarios/basic_consume.json ../out-bad.txt"
mvn -q -pl refactored-system exec:java -Dexec.mainClass=edu.dlut.campuscard.app.SceneRunner -Dexec.args="../scenarios/basic_consume.json ../out-good.txt"
```

`scenarios/` 共 6 个脚本：`basic_consume`、`discount_stack`、`lost_card`、`boundary_reject`、`report_unused_merchant` 要求逐字符一致；`defect_unreachable_branch` 记录被修复的不可达分支，预期出现差异。

### 4. 行为等价单元测试（把逐字符比对固化为测试）

```bash
mvn -q -pl refactored-system test
```

测试类 `refactored-system/src/test/java/edu/dlut/campuscard/BehaviorEquivalenceTest.java`，测试期引用 `bad-system` 作为基线。

### 5. Web 双系统并排演示

两个极简 REST 壳（JDK 内置 HttpServer），暴露同一套接口语义：

```bash
# 在仓库根目录各开一个终端
java -cp bad-system/target/classes        edu.dlut.campuscard.bad.BadApiServer 8081
java -cp refactored-system/target/classes edu.dlut.campuscard.app.ApiServer    8082
```

然后浏览器打开 `web/index.html`（Vue 3 CDN 单页）：页面左右并排请求 8081 / 8082，同一输入下两边响应与状态一致，直观演示行为等价。

接口：`POST /api/consume`、`POST /api/recharge`、`POST /api/loss`、`POST /api/recover`、`GET /api/report`、`GET /api/state`。

## 目录结构

| 目录 | 内容 |
| --- | --- |
| `bad-system/` | 重构前代码：上帝类 `CampusCardSystem` + 全局可变 `AppConfig`，含植入的 11 类缺陷与 1 处潜伏缺陷 |
| `refactored-system/` | 重构后代码：`model` / `config` / `state` / `pricing` / `validation` / `notify` / `account` / `gateway` / `service` / `app` 十个分包 |
| `scenarios/` | 6 个场景脚本（JSON），两套系统共用 |
| `uml/` | PlantUML 源（`.puml`）与渲染图（`rendered/*.png`） |
| `web/` | 演示壳前端单页（Vue 3 CDN） |
| `docs/` | 课程要求文档与参考 PPT（只读） |
| `projects/defense-ppt_20261019/` | 答辩 PPT 工程，详见该目录下 `README.md` |
| `.agents/skills/` | 本地技能包（PPT Master 等），非交付内容 |

`target/` 为构建产物，可随时 `mvn clean` 清理。

## 文档与交付物

根目录三个提交件，按评分细则命名：

| 文件 | 说明 |
| --- | --- |
| `2026设计模式+X组+文档.pdf` | 大作业文档（提交以 PDF 为准） |
| `2026设计模式+X组+文档.docx` | 文档可编辑母本 |
| `2026设计模式+X组+答辩PPT.pptx` | 答辩 PPT（15 页，含讲稿备注） |

打包提交时，把源码（`bad-system/`、`refactored-system/`、`scenarios/`、`uml/`、`web/`）与上述三件放进一个压缩包，命名为 `2026设计模式+X组`。
