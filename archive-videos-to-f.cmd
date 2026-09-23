@echo off
setlocal
title Archive Inactive Videos from J: to 18TB F: Drive

echo ================================================================================
echo               ARCHIVE INACTIVE VIDEOS UTILITY (J:\ -> F:\)
echo ================================================================================
echo.
echo Source Drive      : J:\Videos (Samsung 870 EVO 4TB SSD)
echo Destination Drive : F:\Videos\Archive (WD My Book 18TB External HDD)
echo.
echo Purpose:
echo   Transfers ~538 GB of completed event and travel footage (2019-2025) to F:
echo   using multi-threaded Robocopy with integrity size verification.
echo.
echo Preserved on J: (Untouched):
echo   - J:\Videos\Volleyball (Active 2026 footage - 222 GB)
echo   - J:\Videos\Vlogs (373 GB)
echo   - J:\Videos\LUTs & OBS
echo.
echo ================================================================================
echo.
pause

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0tools\archive_videos.ps1"

echo.
pause
