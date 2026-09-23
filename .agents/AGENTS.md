# Project Memory & Agent Rules: PC Hardware Scanner (`pc`)

This file provides standalone context, rules, and hardware state for the `pc` repository.

---

## 1. Project Overview & Architecture
A lightweight Java utility powered by OSHI to query, diagnose, and report system hardware telemetry, storage health, memory topology, and OS metrics.

- **Build Tool**: Embedded Maven (`mvnw.cmd`)
- **Key Modules**:
  - `src/main/java/com/pcscanner/scanners/`: Specialized hardware query modules (OS, Motherboard, CPU, Memory, GPU, Disk, Volume, Advisor).
  - `src/main/java/com/pcscanner/advisor/`: Diagnostic engine evaluating XMP profiles, M.2 expansion, and disk health wear.
  - `src/main/java/com/pcscanner/reports/`: Dual-output generator (terminal text & interactive dark-theme HTML report).
  - `src/main/java/com/pcscanner/utils/`: Native PowerShell CIM/WMI wrappers for SMART & bus interface detection.
  - `reboot-to-bios.cmd`: Windows utility script to reboot directly into UEFI setup.

## 2. Common Commands
```cmd
# Run scanner with bundled Maven wrapper
mvnw.cmd clean compile exec:java

# Package standalone executable JAR
mvnw.cmd clean package

# Reboot directly to UEFI BIOS
reboot-to-bios.cmd
```

## 3. Host System Configuration & Hardware Baseline
- **Motherboard**: ASUS ROG STRIX Z790-A GAMING WIFI II (5x M.2 PCIe 4.0 slots, 4x SATA III ports)
- **CPU**: Intel Core i9-14900K (24 cores / 32 threads)
- **RAM**: 64 GB DDR5 (2x 32GB Patriot Memory @ JEDEC 4800 MHz baseline)
- **GPU**: NVIDIA GeForce RTX 4070 SUPER (12 GB VRAM)
- **Active OS / System Drive (`C:\`)**: Samsung SSD 970 EVO Plus 2TB (NVMe PCIe 3.0 x4) — *Successfully migrated and active*
- **Legacy OS Drive (`E:\`)**: Samsung SSD 850 EVO 500GB (SATA III) — *Retained as offline fallback clone*
- **Mass Storage**:
  - `J:\` (4TB Samsung 870 EVO SATA SSD)
  - `D:\` (2TB Seagate Barracuda SATA HDD)
  - `F:\` (18TB WD My Book USB 3.0 External)

## 4. Completed Milestones
- [x] Hardware component scan completed ([scan-report.txt](file:///output/scan-report.txt) / [scan-report.html](file:///output/scan-report.html)).
- [x] Motherboard M.2 expansion capability verified (5x PCIe 4.0 slots available).
- [x] Comparative performance analysis conducted (850 EVO vs 970 EVO Plus vs 990 PRO).
- [x] Samsung Data Migration executed and verified.
- [x] BIOS boot sequence successfully configured to Samsung 970 EVO Plus.
- [x] NVMe Boot & Volume Validation: 970 EVO Plus confirmed as active `C:\` (`IsBoot: True`, `IsSystem: True`, 1.49 TB free space). Old 850 EVO safely reassigned to `E:\`.
- [x] Maven wrapper path made relative (`%~dp0`) for portable execution across directories and environments.

## 5. Current Active Task / Next Immediate Steps
- [ ] Monitor system stability for 24-48 hours before formatting or reallocating legacy OS drive (`E:\`).
- [ ] (Optional) Enable Intel XMP / ASUS AEMP in BIOS via `reboot-to-bios.cmd` to increase DDR5 speed from 4800 MHz to full rated speed.

## 6. Agent Behavioral Rules for this Repository
1. **Self-Contained Execution**: Always use relative paths (`%~dp0`, `./`, etc.) so the project functions seamlessly in any folder or clone.
2. **Safe Storage Operations**: Never run destructive partition or volume operations without explicit confirmation from the user.
3. **Report Generation**: When modifying scanners or advisor logic, run `mvnw.cmd clean compile exec:java` to verify compilation and refresh scan reports in `output/`.
