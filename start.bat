@echo off
chcp 65001 >nul
title 中药材种植与销售服务平台 - 启动脚本
color 0A

echo ========================================================
echo.
echo          中药材种植与销售服务平台 一键启动脚本
echo.
echo ========================================================
echo.
echo 环境要求检查：
echo 1. 请确保已安装 JDK 11 并配置环境变量
echo 2. 请确保已安装 Node.js (v16+) 并配置环境变量
echo 3. 请确保已安装 MySQL (v8.0+) 并在本地 3306 端口运行
echo 4. 请确保已安装 Redis 并在本地 6379 端口运行
echo.
echo 数据库配置要求：
echo 1. 请在 MySQL 中创建数据库：herb_platform
echo 2. 默认账号密码：root / root (可在 backend/src/main/resources/application-dev.yml 中修改)
echo 3. 请先导入 database/schema.sql，然后导入 database/data.sql
echo.
pause

echo.
echo [1/3] 正在检查依赖...
echo.

cd backend
call mvnw.cmd clean package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo 后端编译失败，请检查 Maven 环境或依赖！
    pause
    exit
)
cd ..

echo.
echo [2/3] 正在启动后端服务...
echo.

start "Herb-Platform-Backend" cmd /c "cd backend\target && java -jar herb-platform-1.0.0.jar"

echo 后端服务启动中，等待 10 秒...
timeout /t 10 /nobreak >nul

echo.
echo [3/3] 正在启动前端服务...
echo.

cd frontend
if not exist "node_modules" (
    echo 正在安装前端依赖...
    call npm install
)
start "Herb-Platform-Frontend" cmd /c "npm run dev"

echo.
echo ========================================================
echo.
echo 启动完成！
echo.
echo 前端访问地址: http://localhost:5173
echo 后端接口地址: http://localhost:8080/api
echo 接口文档地址: http://localhost:8080/api/doc.html
echo.
echo 默认账号：
echo 管理员：admin / admin123
echo 种植户：farmer001 / admin123
echo 采购商：buyer001 / admin123
echo.
echo ========================================================
pause
