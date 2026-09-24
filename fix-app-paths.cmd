@echo off
setlocal
title Fix Application Paths & Registry Pointers (I: to J:)

echo ================================================================================
echo             SYSTEM & APPLICATION PATH REPAIR UTILITY (I:\ -^> J:\)
echo ================================================================================
echo.
echo Purpose:
echo   Updates machine-level Windows Registry (HKLM) uninstall keys for Adobe Apps
echo   and Final Fantasy VII Remake/Rebirth to reflect active drive letters.
echo.
echo ================================================================================

REM Check for Administrative privileges and auto-elevate if needed
net session >nul 2>&1
if %errorlevel% neq 0 (
    echo Requesting Administrator privileges to update machine registry keys...
    powershell -NoProfile -Command "Start-Process '%~f0' -Verb RunAs"
    exit /b
)

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0tools\fix_system_paths.ps1"

echo.
pause
