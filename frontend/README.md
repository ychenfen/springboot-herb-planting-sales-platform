# 前端项目说明

## 技术栈

- Vue 3
- Vite
- Element Plus
- Axios
- Vue Router
- Pinia
- ECharts

## 开发启动

```bash
cd frontend
npm install
npm run dev
```

访问地址：`http://localhost:5173`

## 接口代理

- 本地开发默认走 `/api` 代理到后端 `http://localhost:8080`。
- 代理配置位置：`frontend/vite.config.js`
- 请求封装位置：`frontend/src/utils/request.js`
- 可通过 `VITE_API_BASE`、`VITE_PROXY_TARGET`、`VITE_DEV_PORT` 覆盖默认配置（参考 `frontend/.env.example`）。

## 构建发布

```bash
npm run build
```

如需本地预览：

```bash
npm run preview
```
