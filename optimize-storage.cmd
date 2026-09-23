@echo off
setlocal
title Windows Storage & Pagefile Optimizer

echo ================================================================================
echo                WINDOWS STORAGE & PAGEFILE POST-MIGRATION OPTIMIZER
echo ================================================================================
echo.
echo Target System : ASUS ROG STRIX Z790-A GAMING WIFI II
echo Boot Drive    : Samsung SSD 970 EVO Plus 2TB (C:)
echo.
echo Actions Performed:
echo   [1] Remove obsolete phantom pagefile reference ('i:\pagefile.sys') from registry
echo   [2] Set clean system-managed pagefile on NVMe C:\ drive
echo   [3] Execute manual TRIM pass on C:\ (Optimize-Volume -ReTrim)
echo.
echo ================================================================================

REM Check for Administrative privileges and auto-elevate if needed
net session >nul 2>&1
if %errorlevel% neq 0 (
    echo Requesting Administrator privileges to apply system storage tweaks...
    powershell -NoProfile -Command "Start-Process '%~f0' -Verb RunAs"
    exit /b
)

echo.
echo Applying memory management registry fix...
powershell -NoProfile -Command "& { Set-ItemProperty -Path 'HKLM:\SYSTEM\CurrentControlSet\Control\Session Manager\Memory Management' -Name 'PagingFiles' -Value @('c:\pagefile.sys 0 0'); Write-Host '  [OK] Registry PagingFiles set to c:\pagefile.sys 0 0' -ForegroundColor Green }"

echo.
echo Sending TRIM optimization to NVMe C:\ drive...
powershell -NoProfile -Command "& { Optimize-Volume -DriveLetter C -ReTrim -Verbose; Write-Host '  [OK] TRIM pass completed on Samsung 970 EVO Plus (C:)' -ForegroundColor Green }"

echo.
echo ================================================================================
echo Optimization completed successfully!
echo ================================================================================
echo.
pause
