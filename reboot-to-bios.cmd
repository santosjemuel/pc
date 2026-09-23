@echo off
setlocal
title Reboot to UEFI / BIOS Setup

echo ================================================================================
echo                     REBOOT TO UEFI / BIOS SETUP UTILITY
echo ================================================================================
echo.
echo Target System : ASUS ROG STRIX Z790-A GAMING WIFI II
echo Purpose       : Reboots computer directly into UEFI BIOS without pressing keys.
echo.
echo Useful for:
echo   [1] Enabling Intel XMP (boosting DDR5 from 4800 MHz to 6000+ MHz)
echo   [2] Launching ASUS EZ Flash 3 to apply the latest Intel 0x12B microcode BIOS
echo   [3] Verifying "Intel Default Settings" baseline profile
echo.
echo ================================================================================

REM Check for Administrative privileges and auto-elevate if needed
net session >nul 2>&1
if %errorlevel% neq 0 (
    echo Requesting Administrator privileges to access UEFI reboot flags...
    powershell -NoProfile -Command "Start-Process '%~f0' -Verb RunAs"
    exit /b
)

echo.
echo Press 'Y' to reboot into BIOS now, or 'N' to cancel.
choice /C YN /M "Proceed with UEFI reboot?"
if errorlevel 2 goto cancel
if errorlevel 1 goto reboot

:reboot
echo.
echo ================================================================================
echo   REBOOTING INTO UEFI IN 3 SECONDS... (Save your open work!)
echo ================================================================================
shutdown /r /fw /t 3 /c "Rebooting to ASUS UEFI Firmware Setup"
goto end

:cancel
echo.
echo Reboot cancelled. No changes were made.
pause

:end
