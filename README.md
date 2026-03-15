# 中药材种植与销售服务平台

基于 `Spring Boot 2.7 + Vue 3 + MyBatis-Plus + Element Plus` 的中药材种植、销售、溯源平台。当前版本重点补齐了种植知识、销售闭环、四角色权限，以及 Windows 近零前置环境的一键启动能力。

## 技术架构

### 后端技术栈

| 技术 | 版本 | 说明 |
|---|---|---|
| Spring Boot | 2.7.x | 核心框架 |
| MyBatis-Plus | 3.5.x | ORM 框架 |
| MySQL | 8.x | 关系型数据库 |
| Redis | 可选 | 缓存与分布式 Token |
| JWT | - | 身份认证 |
| Swagger/Knife4j | - | 接口文档 |
| Lombok | - | 简化代码 |
| BCrypt | - | 密码加密 |

### 前端技术栈

| 技术 | 版本 | 说明 |
|---|---|---|
| Vue | 3.x | 渐进式前端框架 |
| Vite | 5.x | 构建工具 |
| Element Plus | - | UI 组件库 |
| ECharts | 5.x | 数据可视化 |
| Vue Router | 4.x | 路由管理 |
| Pinia | - | 状态管理 |
| Axios | - | HTTP 请求 |

## 功能概览

### 种植管理
- **地块管理**：地块信息登记、编辑、状态管理，支持省市区定位和土壤类型分类。
- **作物管理**：作物种植记录、生长阶段跟踪、产量预估与实际产量对比。
- **农事记录**：播种、施肥、浇水、除草、施药、采收等全流程农事活动记录。

### 销售协同
- **供应大厅**：种植户发布药材供应信息，支持零售价和批发价设置。
- **采购需求**：商家发布采购需求，支持质量要求和目标价格设定。
- **订单管理**：完整的订单状态流转（待确认→待发货→待收货→已完成/已取消/已退款）。
- **我的收藏**：收藏感兴趣的供应或需求信息，方便后续跟进。

### 质量溯源
- **溯源管理**：创建溯源批次，关联作物和种植记录，生成溯源码。
- **溯源节点**：记录种植、施肥、浇水、施药、采收、加工、检测、包装、运输、销售等全链路节点。
- **溯源查询**：通过溯源码查询完整的药材生产轨迹，支持时间线展示。

### 种植知识
- **中药材智能百科**：按药材种类、种植季节、病害类型分类检索，支持关键词直达。
- **病虫害识别演示**：上传图片后，通过本地样例图库和图像相似度匹配返回相似病症与防治建议。
- **种植日历**：按药材输出播种、施肥、除草、采收的时间线与提醒。

### 数据分析
- **仪表盘**：平台核心数据概览，包括用户数、作物数、订单数、溯源批次等统计卡片和图表。
- **产量分析**：作物品种分布、种植户产量排行、供需趋势对比。
- **销售分析**：订单趋势分析、药材销量排行、地区分布。

### 系统管理
- **用户管理**：用户增删改查、角色分配、状态管理。
- **角色管理**：角色定义与权限分配。
- **权限管理**：菜单权限和操作权限的树形管理。
- **系统日志**：操作日志记录与查询。
- **系统配置**：系统参数动态配置。
- **数据字典**：业务字典统一管理。
- **通知管理**：系统通知发布与管理。

### 系统能力
- **四角色权限**：普通用户、种植户、商家、管理员。
- 菜单、页面、统计范围按角色隔离。
- 本地演示版登录态使用内存 token 存储，Windows 首次运行不再依赖 Redis。

## 目录结构

```text
springboot-herb-planting-sales-platform/
├─ backend/                  Spring Boot 后端
│  ├─ src/main/java/com/herb/platform/
│  │  ├─ annotation/         自定义注解（SysLog、RequireUserType）
│  │  ├─ aspect/             AOP 切面（日志记录）
│  │  ├─ config/             配置类（MyBatisPlus、Redis、WebMvc、Swagger）
│  │  ├─ controller/         控制器层（20+ 个 Controller）
│  │  ├─ dto/                数据传输对象
│  │  ├─ entity/             实体类（20+ 个实体）
│  │  ├─ exception/          异常处理
│  │  ├─ interceptor/        拦截器（JWT 认证、权限校验）
│  │  ├─ mapper/             MyBatis Mapper 接口
│  │  ├─ service/            业务逻辑层
│  │  ├─ utils/              工具类（JWT、密码、Redis、图像特征）
│  │  └─ vo/                 视图对象
│  └─ src/main/resources/
│     ├─ mapper/             MyBatis XML 映射文件
│     └─ application*.yml    配置文件
├─ frontend/                 Vue 3 前端
│  ├─ src/
│  │  ├─ api/                API 接口封装（10 个模块）
│  │  ├─ directives/         自定义指令（权限指令）
│  │  ├─ layouts/            布局组件（MainLayout）
│  │  ├─ router/             路由配置
│  │  ├─ stores/             Pinia 状态管理
│  │  ├─ utils/              工具函数（request、format、constants）
│  │  └─ views/              页面组件（26 个页面）
│  │     ├─ analysis/        数据分析（产量分析、销售分析）
│  │     ├─ dashboard/       仪表盘
│  │     ├─ error/           错误页面（404）
│  │     ├─ knowledge/       种植知识（百科、病害识别、种植日历）
│  │     ├─ login/           登录与注册
│  │     ├─ notice/          通知中心
│  │     ├─ planting/        种植管理（地块、作物、农事记录）
│  │     ├─ sales/           销售协同（供应、需求、订单、收藏）
│  │     ├─ system/          系统管理（用户、角色、权限、日志、配置、字典、通知）
│  │     └─ trace/           溯源管理与查询
│  └─ vite.config.js
├─ database/                 全量建库与种子数据
│  ├─ schema.sql             完整表结构（20+ 张表）
│  ├─ data.sql               基础种子数据
│  └─ data_enhance.sql       增强演示数据
├─ docs/                     项目文档
├─ scripts/bootstrap-windows.ps1
├─ init-windows.bat          仅初始化运行环境与数据库
├─ start.bat                 一键初始化并启动前后端
└─ .runtime/                 首次运行后生成的本地运行时目录
```

## Windows 一键启动

### 适用场景

推荐直接用于 Windows 10/11 本地演示、答辩和功能验收。默认假设机器上没有 JDK、Node、MySQL、Maven，也不要求额外安装 Redis。

### 你只需要准备

- 能联网
- PowerShell 5.1+
- 至少约 3GB 可用磁盘空间

### 一键启动步骤

直接双击或在 PowerShell / CMD 中运行：

```bat
start.bat
```

如果只想先准备环境，不立刻启动前后端：

```bat
init-windows.bat
```

### 首次运行会自动完成

- 下载便携式 JDK 11 到 `.runtime/tools/jdk`
- 下载便携式 Node.js LTS 到 `.runtime/tools/node`
- 下载便携式 MySQL 8.4 到 `.runtime/tools/mysql`
- 缺少 VC++ Runtime 时尝试静默安装
- 初始化本地数据库 `herb_platform`
- 导入 `database/schema.sql` 和 `database/data.sql`
- 安装前端依赖
- 使用 `backend/mvnw.cmd` 打包后端
- 打开两个 PowerShell 窗口分别启动前后端

### 本地默认运行参数

| 项目 | 地址/参数 |
|---|---|
| 前端 | http://localhost:5173 |
| 后端 | http://localhost:8080/api |
| 接口文档 | http://localhost:8080/api/doc.html |
| 本地 MySQL | `127.0.0.1:13306` |
| 数据库名 | `herb_platform` |
| 数据库账号 | `herb_local` |
| 数据库密码 | `herb123` |

说明：
- `.runtime/` 已加入 `.gitignore`，不会污染仓库。
- 本地演示启动不要求 Redis；如果你后续要接回 Redis 做缓存或分布式 token，再按配置文件补充即可。
- 第一次运行下载和解压时间较长，通常需要数分钟。

## 手动启动

### 后端

```bat
cd backend
mvnw.cmd clean package -DskipTests
java -jar target\herb-platform-1.0.0.jar
```

如需复用一键脚本创建的本地数据库，先设置环境变量：

```bat
set DB_HOST=127.0.0.1
set DB_PORT=13306
set DB_NAME=herb_platform
set DB_USERNAME=herb_local
set DB_PASSWORD=herb123
```

### 前端

```bat
cd frontend
npm install
npm run dev
```

## 数据库初始化

### 全新初始化

`database/schema.sql` 已包含完整表结构，`database/data.sql` 已包含种子数据。导入顺序固定为：

```sql
SOURCE database/schema.sql;
SOURCE database/data.sql;
-- 可选：导入增强演示数据
SOURCE database/data_enhance.sql;
```

初始化内容包括：
- 四角色账号、角色、菜单权限映射
- 供应、需求、订单、收藏、溯源等业务基础数据
- 知识模块三张表：`herb_knowledge`、`herb_disease_case`、`herb_calendar_stage`
- 商品批发价和起批量字段

### 增强演示数据

`database/data_enhance.sql` 提供了更丰富的演示数据，包括：
- 3 个额外用户（farmer002、buyer002、user002）
- 4 个地块（覆盖甘肃、云南、四川产区）
- 6 个作物（丹参、川芎、金银花、党参等）
- 14 条农事记录
- 4 条供应信息、4 条采购需求
- 7 条订单数据（覆盖各状态）
- 3 条溯源信息和 7 个溯源节点
- 4 种药材知识（丹参、川芎、金银花、党参）及对应病虫害案例和种植日历

### 从旧库升级

如果你已有旧版数据库，不想重建：

```sql
SOURCE database/update_20260311_feature_upgrade.sql;
```

## 默认账号

| 角色 | 账号 | 密码 | 说明 |
|---|---|---|---|
| 管理员 | `admin` | `admin123` | 拥有所有权限 |
| 种植户 | `farmer001` | `admin123` | 种植管理、供应发布、溯源管理 |
| 商家 | `buyer001` | `admin123` | 采购需求、订单管理 |
| 普通用户 | `user001` | `admin123` | 浏览、下单、收藏 |

增强数据中额外提供：

| 角色 | 账号 | 密码 |
|---|---|---|
| 种植户 | `farmer002` | `admin123` |
| 商家 | `buyer002` | `admin123` |
| 普通用户 | `user002` | `admin123` |

## 核心模块说明

### 后端模块

| 模块 | 文件数 | 说明 |
|---|---|---|
| Controller | 20 | 覆盖认证、种植、销售、溯源、统计、系统管理等全部接口 |
| Service | 21 | 完整的业务逻辑实现 |
| Mapper | 21 | 数据访问层，含 6 个自定义 XML 映射 |
| Entity | 20+ | 数据库实体映射 |
| DTO/VO | 15+ | 请求/响应数据封装 |
| Config | 5 | MyBatisPlus、Redis、WebMvc、Swagger、跨域配置 |
| Interceptor | 2 | JWT 认证拦截器、权限校验拦截器 |

### 前端模块

| 模块 | 页面数 | 说明 |
|---|---|---|
| 登录注册 | 2 | 登录页面、注册页面 |
| 仪表盘 | 1 | 多维度数据概览 |
| 种植管理 | 3 | 地块、作物、农事记录 |
| 销售协同 | 4 | 供应、需求、订单、收藏 |
| 溯源管理 | 2 | 溯源管理、溯源查询 |
| 数据分析 | 2 | 产量分析、销售分析 |
| 种植知识 | 3 | 百科、病害识别、种植日历 |
| 系统管理 | 7 | 用户、角色、权限、日志、配置、字典、通知 |
| 通知中心 | 1 | 个人通知查看 |
| 错误页面 | 1 | 404 页面 |

## 已完成验证

以下验证已在 `2026-03-11` 完成：

- `powershell -File scripts/bootstrap-windows.ps1 -Mode Init` 成功下载并初始化便携式 JDK、Node、MySQL
- 本地 MySQL `127.0.0.1:13306` 建库成功
- 后端 `backend\\mvnw.cmd -DskipTests compile` 通过
- 后端 `backend\\mvnw.cmd clean package -DskipTests` 通过
- 前端 `npm run build` 通过

## 建议验收顺序

1. 运行 `start.bat`
2. 打开 `http://localhost:5173`
3. 用四个默认账号分别登录，验证角色菜单隔离
4. 测试种植管理：地块 → 作物 → 农事记录
5. 测试销售协同：供应发布 → 采购需求 → 下单 → 订单流转
6. 测试知识百科搜索、病害识别演示、种植日历
7. 测试溯源管理：创建溯源 → 添加节点 → 溯源查询
8. 测试数据分析：仪表盘 → 产量分析 → 销售分析
9. 测试系统管理：用户管理 → 角色管理 → 权限管理
10. 打开 `http://localhost:8080/api/doc.html` 检查后端接口文档
