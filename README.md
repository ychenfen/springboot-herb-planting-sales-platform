# 中药材种植与销售服务平台

基于 `Spring Boot 2.7 + Vue 3 + MyBatis-Plus + Element Plus` 的中药材种植、销售、溯源平台。当前版本重点补齐了种植知识、销售闭环、四角色权限，以及 Windows 近零前置环境的一键启动能力。

## 功能概览

### 种植知识
- 中药材智能百科：按药材种类、种植季节、病害类型分类检索，支持 `黄芪 病害` 这类关键词直达。
- 病虫害识别演示：上传图片后，通过本地样例图库和图像相似度匹配返回相似病症与防治建议。
- 种植日历：按药材输出播种、施肥、除草、采收的时间线与提醒。

### 销售与溯源
- 供应大厅、采购需求、购物车、订单管理。
- 商品规格智能计价：支持按克、按斤、按批发门槛联动计算。
- 溯源信息展示：商品页可查看产地、种植时间、采收时间、质检状态、批次号、溯源码。
- 订单状态流转：待确认、待发货、待收货、已完成、已取消。

### 系统能力
- 四角色权限：普通用户、种植户、商家、管理员。
- 菜单、页面、统计范围按角色隔离。
- 本地演示版登录态使用内存 token 存储，Windows 首次运行不再依赖 Redis。

## 目录结构

```text
springboot-herb-planting-sales-platform/
├─ backend/                  Spring Boot 后端
├─ frontend/                 Vue 3 前端
├─ database/                 全量建库与种子数据
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

- 前端：http://localhost:5173
- 后端：http://localhost:8080/api
- 接口文档：http://localhost:8080/api/doc.html
- 本地 MySQL：`127.0.0.1:13306`
- 数据库名：`herb_platform`
- 数据库账号：`herb_local`
- 数据库密码：`herb123`

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
```

初始化内容包括：
- 四角色账号、角色、菜单权限映射
- 供应、需求、订单、收藏、溯源等业务基础数据
- 知识模块三张表：`herb_knowledge`、`herb_disease_case`、`herb_calendar_stage`
- 商品批发价和起批量字段

### 从旧库升级

如果你已有旧版数据库，不想重建：

```sql
SOURCE database/update_20260311_feature_upgrade.sql;
```

## 默认账号

| 角色 | 账号 | 密码 |
|---|---|---|
| 管理员 | `admin` | `admin123` |
| 种植户 | `farmer001` | `admin123` |
| 商家 | `buyer001` | `admin123` |
| 普通用户 | `user001` | `admin123` |

## 已完成验证

以下验证已在 `2026-03-11` 完成：
- `powershell -File scripts/bootstrap-windows.ps1 -Mode Init` 成功下载并初始化便携式 JDK、Node、MySQL
- 本地 MySQL `127.0.0.1:13306` 建库成功，`sys_user = 4`、`herb_knowledge = 3`、`herb_calendar_stage = 15`
- 后端 `backend\\mvnw.cmd -DskipTests compile` 通过
- 后端 `backend\\mvnw.cmd clean package -DskipTests` 通过
- 前端 `npm run build` 通过

## 建议验收顺序

1. 运行 `start.bat`
2. 打开 `http://localhost:5173`
3. 用四个默认账号分别登录，验证角色菜单隔离
4. 测试知识百科搜索、病害识别演示、种植日历
5. 测试供应下单、智能计价、订单状态流转、溯源信息展示
6. 打开 `http://localhost:8080/api/doc.html` 检查后端接口文档
