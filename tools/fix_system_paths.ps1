Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "   UPDATING MACHINE-LEVEL REGISTRY & SHORTCUTS (I: -> J:)   " -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

# 1. Update Adobe Creative Cloud Registry Keys (HKLM)
Write-Host "`n[1/3] Updating Adobe Creative Cloud Registry Keys (HKLM)..."
$keys = @('COMP_1_4_32', 'COPS_1_0_32', 'LRCC_9_5_1', 'LTRM_15_4_1', 'PHSPBETA_27_12', 'PHSP_27_8', 'SEPS_0_25_1')
foreach ($k in $keys) {
    $p = "HKLM:\SOFTWARE\WOW6432Node\Microsoft\Windows\CurrentVersion\Uninstall\$k"
    if (Test-Path $p) {
        $props = Get-ItemProperty $p
        if ($props.InstallLocation -match '^[I|i]:') {
            Set-ItemProperty -Path $p -Name 'InstallLocation' -Value ($props.InstallLocation -replace '^[I|i]:', 'J:') -Force
            Write-Host "  [OK] Updated $k InstallLocation -> $($props.InstallLocation -replace '^[I|i]:', 'J:')" -ForegroundColor Green
        }
        if ($props.DisplayIcon -match '^[I|i]:') {
            Set-ItemProperty -Path $p -Name 'DisplayIcon' -Value ($props.DisplayIcon -replace '^[I|i]:', 'J:') -Force
            Write-Host "  [OK] Updated $k DisplayIcon -> $($props.DisplayIcon -replace '^[I|i]:', 'J:')" -ForegroundColor Green
        }
    }
}

# 2. Update Steam Game Registry Install Paths (HKLM)
Write-Host "`n[2/3] Updating Steam App Registry Install Paths (HKLM)..."
$ff7Remake = "HKLM:\SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\Steam App 1462040"
if (Test-Path $ff7Remake) {
    Set-ItemProperty -Path $ff7Remake -Name "InstallLocation" -Value "C:\Program Files (x86)\Steam\steamapps\common\FINAL FANTASY VII REMAKE" -Force
    Write-Host "  [OK] Updated Final Fantasy VII Remake InstallLocation to C:\" -ForegroundColor Green
}
$ff7Rebirth = "HKLM:\SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\Steam App 2909400"
if (Test-Path $ff7Rebirth) {
    Set-ItemProperty -Path $ff7Rebirth -Name "InstallLocation" -Value "C:\Program Files (x86)\Steam\steamapps\common\FINAL FANTASY VII REBIRTH" -Force
    Write-Host "  [OK] Updated Final Fantasy VII Rebirth InstallLocation to C:\" -ForegroundColor Green
}

# 3. Update Start Menu system shortcuts in ProgramData
Write-Host "`n[3/3] Updating Start Menu system shortcuts in ProgramData..."
$wsh = New-Object -ComObject WScript.Shell
$dir = "C:\ProgramData\Microsoft\Windows\Start Menu\Programs"
Get-ChildItem -Path $dir -Recurse -Filter '*.lnk' -ErrorAction SilentlyContinue | ForEach-Object {
    try {
        $lnk = $wsh.CreateShortcut($_.FullName)
        $mod = $false
        if ($lnk.TargetPath -match '^[I|i]:') {
            $lnk.TargetPath = $lnk.TargetPath -replace '^[I|i]:', 'J:'
            $mod = $true
        }
        if ($lnk.WorkingDirectory -match '^[I|i]:') {
            $lnk.WorkingDirectory = $lnk.WorkingDirectory -replace '^[I|i]:', 'J:'
            $mod = $true
        }
        if ($mod) {
            $lnk.Save()
            Write-Host "  [OK] Repaired system shortcut: $($_.Name)" -ForegroundColor Cyan
        }
    } catch {}
}

Write-Host "`n============================================================" -ForegroundColor Cyan
Write-Host "All machine-level paths, registry keys, and shortcuts repaired!" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
