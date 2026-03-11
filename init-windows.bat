@echo off
set "ROOT_DIR=%~dp0"
powershell -NoProfile -ExecutionPolicy Bypass -File "%ROOT_DIR%scripts\bootstrap-windows.ps1" -Mode Init
if errorlevel 1 (
    echo.
    echo [ERROR] init-windows.bat failed.
    pause
    exit /b 1
)
