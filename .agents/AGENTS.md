# Project Guidelines & Agent Instructions: PC Hardware Scanner (`pc`)

This document defines architecture, build workflows, coding standards, and agent operational rules for the `pc` repository.

---

## 1. Project Overview & Architecture
A cross-platform Java utility powered by [OSHI (Operating System and Hardware Information)](https://github.com/oshi/oshi) that diagnoses, queries, and reports system hardware specifications, memory topologies, GPU controllers, disk telemetry, and OS diagnostics.

### Core Modules
- **`src/main/java/com/pcscanner/scanners/`**: Specialized modular scanners (OS, Motherboard, CPU, Memory, GPU, Disk, Volume, Advisor).
- **`src/main/java/com/pcscanner/advisor/`**: Rule-based hardware advisory engine evaluating memory XMP profiles, M.2 storage expansion, spinning HDD bottlenecks, and SSD wear.
- **`src/main/java/com/pcscanner/reports/`**: Report generators producing dual outputs (plain terminal ASCII report and responsive, self-contained HTML dashboard).
- **`src/main/java/com/pcscanner/utils/`**: Utilities for system formatting, metric parsing, and Windows PowerShell CIM/WMI metadata resolution (e.g., bus interfaces and SMART health).
- **`tools/`**: Bundled portable Maven installation for environments without system-wide Maven.
- **`reboot-to-bios.cmd`**: Windows batch helper to reboot directly into motherboard UEFI firmware.
- **`optimize-storage.cmd`**: One-click elevated script to clean obsolete pagefile paths and trigger a manual TRIM pass on NVMe C:.
- **`setup-scratch-disk.cmd`**: Utility to reformat legacy 500GB SSD (E:) into a dedicated high-speed scratch/cache drive.
- **`archive-videos-to-f.cmd`**: Multi-threaded Robocopy utility to offload completed video archives from J: to 18TB F: drive with integrity verification.

---

## 2. Common Development & Build Commands
```cmd
# Run hardware scan with bundled Maven wrapper
mvnw.cmd clean compile exec:java

# Compile and package executable JAR to target/
mvnw.cmd clean package

# Run with standard Maven (if installed on system PATH)
mvn clean compile exec:java

# Apply storage tweaks & re-trim NVMe drive
optimize-storage.cmd

# Reboot directly to UEFI BIOS
reboot-to-bios.cmd
```

---

## 3. Local Machine Context
- Machine-specific hardware benchmarks, personal drive layouts, and local migration tasks are maintained locally in `.agents/local.md`.
- `.agents/local.md` is excluded from version control via `.gitignore` to preserve privacy and keep the repository clean and portable.

---

## 4. Coding & Architectural Standards
1. **Portability & Relative Paths**:
   - Never hardcode absolute file paths (e.g., `C:\...`, `J:\...`) in scripts or source code.
   - Always resolve paths relative to the project root (using `%~dp0` in Windows scripts or relative `Paths.get(...)` in Java).
2. **Scanner Modularization**:
   - Each hardware component must reside in its own dedicated scanner under `com.pcscanner.scanners`.
   - Complex diagnostic rules belong in `com.pcscanner.advisor.HardwareAdvisor`.
3. **Dual Report Parity**:
   - Any new metric added to the console or text output (`scan-report.txt`) should be reflected in the HTML dashboard (`HtmlReportGenerator.java`) with matching visual hierarchy and styling.
4. **Non-Destructive Operations**:
   - Diagnostic and scanning tools must be strictly read-only.
   - Never introduce write or formatting commands targeting host storage or partitions.
