@echo off
setlocal
title Initialize Dedicated Scratch Disk (E:)

echo ================================================================================
echo                     DEDICATED SCRATCH DISK SETUP (E:)
echo ================================================================================
echo.
echo Target Drive  : E:\ (Samsung SSD 850 EVO 500GB SATA III)
echo Purpose       : Reformat legacy boot clone into high-speed Scratch/Cache drive.
echo New Label     : SCRATCH (E:)
echo.
echo ================================================================================

REM Check for Administrative privileges and auto-elevate if needed
net session >nul 2>&1
if %errorlevel% neq 0 (
    echo Requesting Administrator privileges to format drive...
    powershell -NoProfile -Command "Start-Process '%~f0' -Verb RunAs"
    exit /b
)

echo.
echo WARNING: This will format Drive E: (500GB) and erase all legacy files.
echo (Your active Windows OS and all personal files are safe on NVMe C:).
echo.
choice /C YN /M "Are you sure you want to format Drive E: as SCRATCH?"
if errorlevel 2 goto cancel
if errorlevel 1 goto format

:format
echo.
echo Formatting Drive E: as NTFS with label 'SCRATCH'...
powershell -NoProfile -Command "& { Format-Volume -DriveLetter E -FileSystem NTFS -NewFileSystemLabel 'SCRATCH' -Confirm:$false; Write-Host '  [OK] Drive E: successfully formatted as SCRATCH' -ForegroundColor Green }"

echo.
echo Creating dedicated scratch and cache folders on E:\...
powershell -NoProfile -Command "& {
    $folders = @('E:\DaVinci_Cache', 'E:\Adobe_Scratch', 'E:\Temp_Downloads', 'E:\Build_Cache')
    foreach ($f in $folders) {
        if (-not (Test-Path $f)) {
            New-Item -ItemType Directory -Path $f -Force | Out-Null
            Write-Host \"  [OK] Created $f\" -ForegroundColor Cyan
        }
    }
}"

echo.
echo ================================================================================
echo Drive E:\ is now ready for dedicated high-speed scratch and cache use!
echo ================================================================================
echo.
pause
goto end

:cancel
echo.
echo Formatting cancelled. Drive E: was not modified.
pause

:end
