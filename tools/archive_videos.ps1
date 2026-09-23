# Archive inactive/completed video folders from J:\Videos to F:\Videos\Archive
$sourceBase = "J:\Videos"
$destBase = "F:\Videos\Archive"

if (-not (Test-Path $destBase)) {
    New-Item -ItemType Directory -Path $destBase -Force | Out-Null
    Write-Host "Created destination folder: $destBase" -ForegroundColor Cyan
}

# Folders to archive (excluding active 2026 Volleyball, active LUTs, and active OBS)
$archiveFolders = @(
    'Patagonia', 'Agility', 'Appa', 'Climbing', 'Thanksgiving Weekend',
    'Drone', 'Cy', 'Lifting', 'Vince3.21.20', 'Freyja', 'Phlippines',
    'Marathon', 'Agne Shoot', 'RachelRemodel', 'Stowe', 'AppaFreyha',
    'Nanay Videos', 'CribTour', 'Random', 'Training w Vincent', 'Roadtrip',
    'Athan Bday', 'John Gets Pounded', 'Music'
)

Write-Host "================================================================================"
Write-Host "             ARCHIVING INACTIVE VIDEO FOLDERS (J: -> F:)"
Write-Host "================================================================================"
Write-Host "Source:      $sourceBase"
Write-Host "Destination: $destBase"
Write-Host "Folders to archive: $($archiveFolders.Count) folders (~538 GB)"
Write-Host "Active folders preserved on J: (Volleyball 2026, LUTs, OBS)"
Write-Host "================================================================================"
Write-Host ""

$completedFolders = @()

foreach ($folderName in $archiveFolders) {
    $src = Join-Path $sourceBase $folderName
    $dst = Join-Path $destBase $folderName

    if (-not (Test-Path $src)) {
        Write-Host "Skipping '$folderName' (does not exist on source)" -ForegroundColor Yellow
        continue
    }

    Write-Host "`n>>> Processing: $folderName ..." -ForegroundColor Green
    
    # Use Robocopy for high-speed multi-threaded transfer with resume capability
    & robocopy $src $dst /E /R:2 /W:2 /MT:8 /NP

    # Verify folder size match
    $srcSize = (Get-ChildItem -Path $src -Recurse -File -Force -ErrorAction SilentlyContinue | Measure-Object -Property Length -Sum).Sum
    $dstSize = (Get-ChildItem -Path $dst -Recurse -File -Force -ErrorAction SilentlyContinue | Measure-Object -Property Length -Sum).Sum

    $srcGB = [math]::Round($srcSize / 1GB, 2)
    $dstGB = [math]::Round($dstSize / 1GB, 2)

    if ($srcSize -eq $dstSize -and $srcSize -gt 0) {
        Write-Host "  [VERIFIED] Size match: Source=$srcGB GB, Destination=$dstGB GB" -ForegroundColor Green
        $completedFolders += $folderName
    } else {
        Write-Host "  [WARNING] Size mismatch: Source=$srcGB GB, Destination=$dstGB GB" -ForegroundColor Red
    }
}

Write-Host "`n================================================================================"
Write-Host "Transfer completed for $($completedFolders.Count) of $($archiveFolders.Count) folders."
Write-Host "================================================================================"

Write-Host "`nWould you like to delete the source folders from J:\Videos to reclaim disk space? (y/n)"
$answer = Read-Host

if ($answer -eq 'y' -or $answer -eq 'Y') {
    foreach ($folderName in $completedFolders) {
        $src = Join-Path $sourceBase $folderName
        Write-Host "Removing from J:\: $folderName ..." -ForegroundColor Yellow
        Remove-Item -Path $src -Recurse -Force -ErrorAction SilentlyContinue
    }
    Write-Host "Source folders deleted. Space on J:\ successfully reclaimed!" -ForegroundColor Green
} else {
    Write-Host "Source folders preserved on J:\. You can manually delete them after inspecting F:\." -ForegroundColor Cyan
}
