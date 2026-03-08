# API接口文档

## 基础信息

- Base URL: `http://localhost:8080/api`
- 认证方式: `Authorization: Bearer <token>`
- 响应格式: `Result` 统一封装

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1710000000000
}
```

- 在线文档: `http://localhost:8080/api/doc.html` (Knife4j)
- 权限说明:
  - `/system/*` 系统管理接口仅管理员可访问（后端强制校验）。
  - `/system/permission/menus` 与 `/system/permission/user` 供所有已登录用户获取菜单/权限。
  - 业务模块按用户类型限制：种植相关仅种植户，需求发布仅采购商，订单流程按买卖角色限制。

## 认证模块 `/auth`

- `POST /auth/login` 登录
- `POST /auth/register` 注册
- `GET /auth/info` 获取当前用户信息
- `POST /auth/logout` 退出登录
- `PUT /auth/password` 修改密码
- `PUT /auth/profile` 更新个人信息

## 系统管理 `/system`

### 用户
- `GET /system/user/page`
- `GET /system/user/{id}`
- `POST /system/user`
- `PUT /system/user`
- `DELETE /system/user/{id}`
- `PUT /system/user/{id}/status`
- `PUT /system/user/{id}/password`
- `PUT /system/user/{id}/roles`

## 统计分析 `/statistics`

- `GET /statistics/dashboard` （登录用户，数据按用户类型自动过滤）
- `GET /statistics/order-trend` （登录用户，数据按用户类型过滤）
- `GET /statistics/supply-demand-trend` （登录用户，数据按用户类型过滤）
- `GET /statistics/herb-sales-ranking` （登录用户，数据按用户类型过滤）
- `GET /statistics/farmer-yield-ranking` （管理员/种植户）
- `GET /statistics/crop-distribution` （管理员/种植户）
- `GET /statistics/user-type-distribution` （仅管理员）
- `GET /statistics/region-distribution` （管理员/种植户）

### 角色
- `GET /system/role/page`
- `GET /system/role/list`
- `GET /system/role/{id}`
- `POST /system/role`
- `PUT /system/role`
- `DELETE /system/role/{id}`
- `PUT /system/role/{id}/permissions`

### 权限
- `GET /system/permission/tree`
- `GET /system/permission/list`
- `GET /system/permission/menus`
- `GET /system/permission/user`
- `POST /system/permission`
- `PUT /system/permission`
- `DELETE /system/permission/{id}`

### 数据字典
- `GET /system/dict/page`
- `GET /system/dict/types`
- `GET /system/dict/type/{dictType}`
- `POST /system/dict`
- `PUT /system/dict`
- `DELETE /system/dict/{id}`

### 系统配置
- `GET /system/config/page`
- `GET /system/config/types`
- `GET /system/config/{id}`
- `POST /system/config`
- `PUT /system/config`
- `DELETE /system/config/{id}`

### 系统日志
- `GET /system/log/page`
- `GET /system/log/{id}`
- `DELETE /system/log/{id}`
- `DELETE /system/log/clear`

### 系统通知
- `GET /system/notice/page`
- `GET /system/notice/{id}`
- `POST /system/notice`
- `PUT /system/notice`
- `DELETE /system/notice/{id}`

## 通知中心 `/notice`

- `GET /notice/page`
- `GET /notice/{id}`
- `GET /notice/unread-count`
- `PUT /notice/{id}/read`
- `PUT /notice/read-all`

## 种植管理

### 地块 `/field`
- `GET /field/page`
- `GET /field/list`
- `GET /field/{id}`
- `POST /field`
- `PUT /field`
- `DELETE /field/{id}`

### 作物 `/crop`
- `GET /crop/page`
- `GET /crop/{id}`
- `POST /crop`
- `PUT /crop`
- `DELETE /crop/{id}`
- `POST /crop/{id}/harvest`

### 农事记录 `/farm-record`
- `GET /farm-record/page`
- `GET /farm-record/{id}`
- `POST /farm-record`
- `PUT /farm-record`
- `DELETE /farm-record/{id}`

## 销售对接

### 供应 `/supply`
- `GET /supply/market`
- `GET /supply/my`
- `GET /supply/{id}`
- `POST /supply`
- `PUT /supply`
- `PUT /supply/{id}/offline`
- `DELETE /supply/{id}`

### 需求 `/demand`
- `GET /demand/market`
- `GET /demand/my`
- `GET /demand/{id}`
- `POST /demand`
- `PUT /demand`
- `PUT /demand/{id}/cancel`
- `DELETE /demand/{id}`

### 订单 `/order`
- `GET /order/page`
- `GET /order/{id}`
- `POST /order`
- `PUT /order/{id}/confirm`
- `PUT /order/{id}/deliver`
- `PUT /order/{id}/complete`
- `PUT /order/{id}/cancel`

### 收藏 `/favorite`
- `GET /favorite/page`
- `POST /favorite`
- `DELETE /favorite`

## 质量溯源 `/trace`

- `GET /trace/page`
- `GET /trace/{id}`
- `GET /trace/public/{traceCode}` (公开)
- `POST /trace`
- `PUT /trace`
- `DELETE /trace/{id}`
- `POST /trace/{id}/publish`
- `POST /trace/{id}/qrcode`

### 溯源节点
- `POST /trace/node`
- `PUT /trace/node`
- `DELETE /trace/node/{nodeId}`

## 统计分析 `/statistics`

- `GET /statistics/dashboard`
- `GET /statistics/order-trend`
- `GET /statistics/supply-demand-trend`
- `GET /statistics/herb-sales-ranking`
- `GET /statistics/farmer-yield-ranking`
- `GET /statistics/crop-distribution`
- `GET /statistics/user-type-distribution`
- `GET /statistics/region-distribution`

## 说明

- 分页参数统一使用 `pageNum` / `pageSize`。
- 需登录接口统一在请求头携带 `Authorization`。
- 公开接口示例：`/auth/login`、`/auth/register`、`/trace/public/**`。
- 前端权限控制可调用 `/system/permission/user` 获取权限编码列表。
- 详细字段与示例请以 Knife4j 文档为准。
