# 中药材种植与销售服务平台

## 项目简介

基于Spring Boot和Vue3的中药材种植与销售服务平台，实现从种植管理到销售溯源的完整业务闭环。

### 核心功能
- **种植管理**：地块管理、作物管理、农事记录、生长跟踪
- **销售对接**：供应发布、需求发布、信息检索、在线沟通
- **质量溯源**：二维码生成、溯源查询、全流程追溯
- **数据分析**：产量统计、销售分析、趋势图表

## 技术栈

### 后端技术
- Spring Boot 2.7.x
- MyBatis-Plus 3.5.x
- MySQL 8.0
- Redis 6.x
- JWT Token 认证
- Lombok
- Swagger/Knife4j API文档

### 前端技术
- Vue 3.3.x
- Element Plus
- Axios
- Vue Router
- Pinia 状态管理
- ECharts 数据可视化

## 项目结构

```
herb-platform/
├── backend/                    # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/herb/platform/
│   │   │   │       ├── config/         # 配置类
│   │   │   │       ├── controller/     # 控制器
│   │   │   │       ├── service/        # 服务层
│   │   │   │       ├── mapper/         # 数据访问层
│   │   │   │       ├── entity/         # 实体类
│   │   │   │       ├── dto/            # 数据传输对象
│   │   │   │       ├── vo/             # 视图对象
│   │   │   │       ├── common/         # 公共类
│   │   │   │       ├── exception/      # 异常处理
│   │   │   │       └── utils/          # 工具类
│   │   │   └── resources/
│   │   │       ├── mapper/             # MyBatis映射文件
│   │   │       ├── application.yml     # 配置文件
│   │   │       └── application-dev.yml # 开发环境配置
│   │   └── test/                       # 测试代码
│   └── pom.xml                         # Maven配置
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── api/                # API接口
│   │   ├── assets/             # 静态资源
│   │   ├── components/         # 公共组件
│   │   ├── views/              # 页面组件
│   │   ├── router/             # 路由配置
│   │   ├── store/              # 状态管理
│   │   ├── utils/              # 工具函数
│   │   ├── App.vue             # 根组件
│   │   └── main.js             # 入口文件
│   ├── package.json
│   └── vite.config.js
├── database/                   # 数据库脚本
│   ├── schema.sql              # 表结构
│   ├── data.sql                # 初始化数据
│   └── update/                 # 更新脚本
└── docs/                       # 文档
    ├── 快速开始指南.md
    ├── 代码生成指南.md
    ├── API接口文档.md
    ├── 数据库设计.md
    ├── 部署文档.md
    └── 开发指南.md
```

## 核心模块设计

### 1. 用户认证与权限管理 (RBAC)
- 用户注册/登录
- JWT Token认证
- 基于角色的访问控制
- 用户角色：种植户、采购商、管理员

### 2. 种植管理模块
- 地块信息管理
- 作物品种管理
- 农事活动记录
- 生长周期跟踪
- 产量预测分析

### 3. 销售对接模块
- 供应信息发布
- 采购需求发布
- 多条件搜索筛选
- 在线沟通交流
- 订单管理

### 4. 质量溯源模块
- 溯源码生成(二维码)
- 生产流程记录
- 溯源信息查询
- 节点信息展示

### 5. 数据可视化模块
- 产量统计分析
- 销售数据报表
- 价格趋势图表
- 用户行为分析

## 数据库设计

### 核心表结构
- `sys_user` - 用户表
- `sys_role` - 角色表
- `sys_permission` - 权限表
- `sys_user_role` - 用户角色关联表
- `herb_field` - 地块表
- `herb_crop` - 作物表
- `herb_farm_record` - 农事记录表
- `herb_supply` - 供应信息表
- `herb_demand` - 需求信息表
- `herb_order` - 订单表
- `herb_trace` - 溯源信息表
- `herb_trace_node` - 溯源节点表

详细设计见 [数据库设计文档](docs/数据库设计.md)

## 项目当前状态

✅ **已完成**：
- 完整的数据库设计（20张表）
- 后端所有模块开发（13个Controller）
- 前端所有页面开发（15个Vue页面）
- 前后端联调测试
- 数据库初始化脚本

📋 **功能模块**：
- ✅ 用户认证与权限管理
- ✅ 种植管理（地块、作物、农事记录）
- ✅ 销售对接（供应、需求、订单）
- ✅ 质量溯源（溯源管理、溯源查询）
- ✅ 数据分析（仪表盘、销售分析、产量分析）
- ✅ 系统管理（用户、角色、字典）

## 快速开始

### 📖 重要文档

开始之前，强烈建议阅读以下文档：

**Windows 快速启动：**
本项目已完全兼容 Windows 环境，提供了两个便捷脚本：
1. 双击运行 `init-windows.bat`：初始化前端配置并查看数据库导入指南
2. 双击运行 `start.bat`：一键自动编译后端、启动后端服务，并安装前端依赖启动前端服务

1. **[下一步操作指南.md](下一步操作指南.md)** - 详细的开发步骤（必读）
2. **[项目总览.md](项目总览.md)** - 项目整体状态和进度
3. **[快速开始指南.md](docs/快速开始指南.md)** - 环境搭建详细教程
4. **[代码生成指南.md](docs/代码生成指南.md)** - 使用Claude Code快速开发
5. **[API接口文档.md](docs/API接口文档.md)** - 接口概览与示例
6. **[数据库设计.md](docs/数据库设计.md)** - 表结构与设计说明
7. **[部署文档.md](docs/部署文档.md)** - 部署与发布流程
8. **[开发指南.md](docs/开发指南.md)** - 开发约定与规范
9. **[项目检查清单.md](项目检查清单.md)** - 开发进度跟踪
10. **[项目实施计划.md](项目实施计划.md)** - 完整开发计划（40000+字）

### 环境要求
- JDK 11+
- MySQL 8.0+
- Redis 6.0+
- Node.js 16+
- Maven 3.6+

> **注意：** 在 Windows 上部署时，请务必确保 Redis 服务已启动（默认端口 6379），否则后端应用将无法启动。

### 数据库初始化（第一步）

```bash
# 登录MySQL
mysql -u root -p

# 执行初始化脚本
source database/schema.sql
source database/data.sql

# 验证初始化
USE herb_platform;
SHOW TABLES;  -- 应显示20张表
SELECT * FROM sys_user;  -- 应显示3个测试账号
```

**默认账号**：
| 用户名 | 密码 | 角色 |
|-------|------|------|
| admin | admin123 | 系统管理员 |
| farmer001 | admin123 | 种植户 |
| buyer001 | admin123 | 采购商 |

### 配置后端（第二步）

编辑 `backend/src/main/resources/application-dev.yml`：

```yaml
spring:
  datasource:
    username: root          # 修改为你的MySQL用户名
    password: your_password # 修改为你的MySQL密码

  redis:
    password:  # 如果Redis有密码，填写在这里
```

### 使用Claude Code生成代码（可选）

当前代码已提供，如需扩展模块或重新生成，可参考 **[代码生成指南.md](docs/代码生成指南.md)**：

**第一个提示词示例**：
```
请生成Spring Boot主启动类

类名: HerbPlatformApplication
包路径: com.herb.platform
注解: @SpringBootApplication, @MapperScan("com.herb.platform.mapper")

保存到: backend/src/main/java/com/herb/platform/HerbPlatformApplication.java
```

### 后端启动

```bash
# 进入后端目录
cd backend

# 安装依赖
mvn clean install

# 启动应用
mvn spring-boot:run
```

访问：
- API文档：http://localhost:8080/api/doc.html
- Druid监控：http://localhost:8080/api/druid

### 前端启动

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

访问 http://localhost:5173

## 配置说明

### 后端配置 (application.yml)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/herb_platform
    username: root
    password: your_password
  redis:
    host: localhost
    port: 6379
    password:
```

### 前端配置

前端默认通过 Vite 代理 `/api` 到 `http://localhost:8080`，配置位于 `frontend/vite.config.js`。
如需调整后端地址，可修改 `server.proxy` 或 `frontend/src/utils/request.js`。

## 项目亮点

1. **业务闭环创新**：从种植到销售的完整链路
2. **技术栈先进**：Spring Boot + Vue3 + Redis 现代化架构
3. **权限设计完善**：RBAC模型保证安全性
4. **性能优化**：Redis缓存、索引优化、分页查询
5. **可扩展性强**：模块化设计，易于扩展新功能
6. **用户体验好**：响应式设计，操作便捷

## 开发进度

- [x] 项目架构设计
- [x] 数据库设计
- [x] 后端核心功能开发
  - [x] 用户认证模块
  - [x] 种植管理模块
  - [x] 销售对接模块
  - [x] 质量溯源模块
  - [x] 数据分析模块
  - [x] 系统管理模块
- [x] 前端页面开发
  - [x] 登录注册页面
  - [x] 种植管理界面
  - [x] 销售对接界面
  - [x] 溯源查询界面
  - [x] 数据分析界面
  - [x] 系统管理界面
- [x] 前后端联调测试
- [ ] 生产环境部署

## API文档

启动后端后访问：http://localhost:8080/api/doc.html

## 参考文献

1. Spring Boot官方文档
2. Vue3官方文档
3. MyBatis-Plus官方文档
4. Element Plus组件库文档

## 联系方式

如有问题，请提交Issue或联系开发团队。

## 许可证

MIT License
