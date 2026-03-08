@echo off
chcp 65001 >nul
title 中药材种植与销售服务平台 - 环境初始化
color 0B

echo ========================================================
echo.
echo          中药材种植与销售服务平台 - 环境初始化
echo.
echo ========================================================
echo.
echo 此脚本将帮助您在 Windows 上初始化项目环境。
echo 请确保您已安装 MySQL 和 Redis。
echo.
pause

echo.
echo [1/3] 检查前端环境...
cd frontend
if not exist ".env.local" (
    echo 正在创建前端本地配置文件...
    copy .env.example .env.local
    echo 成功创建 .env.local
) else (
    echo 前端本地配置文件已存在。
)
cd ..

echo.
echo [2/3] 数据库初始化指引...
echo.
echo 请打开 MySQL 命令行或 Navicat 等客户端，执行以下操作：
echo.
echo 1. 创建数据库:
echo    CREATE DATABASE herb_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
echo.
echo 2. 导入表结构和数据:
echo    USE herb_platform;
echo    SOURCE %CD%\database\schema.sql;
echo    SOURCE %CD%\database\data.sql;
echo.
echo 请在完成上述数据库操作后按任意键继续...
pause >nul

echo.
echo [3/3] 准备就绪！
echo.
echo 现在您可以双击运行 start.bat 来启动项目了。
echo.
pause
