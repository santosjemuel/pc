# PC Hardware & System Component Scanner

A lightweight, cross-platform Java utility that detects, queries, and reports hardware specifications, firmware details, and operating system diagnostics from the host machine.

Built with Java and powered by [OSHI (Operating System and Hardware Information)](https://github.com/oshi/oshi), this tool provides a comprehensive snapshot of system components in an easy-to-read terminal report without requiring external native installers or C++ toolchains.

---

## Table of Contents

- [Purpose & Overview](#purpose--overview)
- [Key Features](#key-features)
- [Architecture & Modules](#architecture--modules)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
  - [Run with Bundled Maven Wrapper (Windows)](#run-with-bundled-maven-wrapper-windows)
  - [Run with Standard Maven](#run-with-standard-maven)
  - [Build Standalone JAR](#build-standalone-jar)
- [Sample Output](#sample-output)
- [Dependencies](#dependencies)
- [License](#license)

---

## Purpose & Overview

Diagnosing PC hardware configurations and retrieving detailed hardware telemetry often requires vendor-specific tools, native system calls, or third-party diagnostic software. 

The **PC Hardware & System Component Scanner** solves this by providing a unified, standalone Java application that scans hardware components, RAM topologies, CPU microarchitectures, GPU controllers, drive telemetry, and OS metrics within milliseconds.

---

## Key Features

The scanner queries and presents information across six core areas:

| Module | Description | Key Metrics Detected |
|---|---|---|
| **Operating System** | Host OS and environment diagnostics | OS name/family, build & version, architecture (32/64-bit), boot time, uptime (days, hours, mins, secs), active process & thread count |
| **Motherboard & BIOS** | Hardware chassis and firmware information | System product name & serial, motherboard manufacturer, model, revision, serial, BIOS vendor, version, and release date |
| **CPU (Processor)** | Central processing unit specifications | Processor model string, vendor, microarchitecture family, physical packages/sockets, physical cores, logical threads, 64-bit capability, max & vendor clock frequencies |
| **Memory (RAM)** | Physical & virtual memory utilization + DIMM inventory | Total, used, and available RAM, utilization %, virtual memory capacity & usage, per-stick physical slot breakdown (manufacturer, memory generation e.g. DDR4/DDR5, capacity, clock speed, bank label) |
| **Graphics (GPU)** | Video controller and display adapters | Dedicated/integrated GPU name, vendor, driver version, VRAM capacity, hardware device ID |
| **Storage (Disks)** | Physical storage drives and I/O telemetry | Drive model, serial number, storage capacity, lifetime read/write operations, and read/write byte throughput |

---

## Architecture & Modules

The application is structured into modular scanner components orchestrated by the main runner:

```
pc/
├── mvnw.cmd                                    # Windows Maven execution wrapper
├── pom.xml                                     # Project metadata, dependencies, and plugins
├── tools/
│   └── apache-maven-3.9.9/                     # Embedded Maven installation
└── src/main/java/com/pcscanner/
    ├── Main.java                               # Entrypoint: coordinates scanners, dual capture & reports
    ├── reports/
    │   └── HtmlReportGenerator.java            # Generates self-contained HTML dashboard report
    ├── scanners/
    │   ├── OsScanner.java                      # Operating system, uptime, and process telemetry
    │   ├── MotherboardScanner.java             # Baseboard, system chassis, and BIOS metadata
    │   ├── CpuScanner.java                     # Central processor properties and core topology
    │   ├── MemoryScanner.java                  # RAM utilization and individual DIMM inspection
    │   ├── GpuScanner.java                     # Graphics adapters and video memory inspection
    │   └── DiskScanner.java                    # Physical storage devices and I/O statistics
    └── utils/
        └── FormatUtils.java                    # Formatting helpers (bytes, Hz, percentages, tables)
```

---

## Scan Reports & Output

Every scan automatically exports formatted reports to the `output/` directory (which is excluded from Git via `.gitignore`):

| File | Format | How to Open |
|---|---|---|
| `output/scan-report.html` | Rich, responsive dark-themed dashboard | Double-click or open in any web browser |
| `output/scan-report.txt` | Terminal ASCII text report | Open with any text editor or viewer |

> **Note**: Both files are automatically kept out of version control by `.gitignore`.

---

## Prerequisites

- **Java Development Kit (JDK)**: Java 8 or newer (compatible with Java 8, 11, 17, 21, and latest).
- **Maven**: Version 3.6+ (or use the pre-configured `mvnw.cmd` wrapper and bundled Maven distribution).

---

## Getting Started

### Run with Bundled Maven Wrapper (Windows)

The repository includes a ready-to-run wrapper `mvnw.cmd` configured to locate your local JDK and use the included Maven binaries:

```cmd
mvnw.cmd clean compile exec:java
```

### Opening the Scan Output

Once the scan finishes, open the generated HTML report directly in your default browser:

```powershell
Start-Process output/scan-report.html
```

Or view the text report:

```powershell
notepad output/scan-report.txt
```

### Run with Standard Maven

If you have Maven installed in your system `PATH`:

```bash
# Compile and run in one step
mvn clean compile exec:java
```

### Build Standalone JAR

To package the project into a JAR archive:

```bash
mvn clean package
```

The compiled JAR file will be located at `target/pc-hardware-scanner-1.0.0.jar`.

---


## Sample Output

Running the scanner outputs a clean, formatted report:

```text
================================================================================
                     PC HARDWARE & SYSTEM COMPONENT SCANNER                     
================================================================================
Scanning hardware components... Please wait...


================================================================================
  OPERATING SYSTEM INFORMATION
================================================================================
  OS Name                   : Windows
  Manufacturer              : Microsoft
  Version / Build           : 11 (Home) build 26200
  Architecture              : 64-bit
  System Boot Time          : 2026-09-23 12:17:41
  System Uptime             : 0 days, 0 hours, 4 mins, 0 secs
  Process Count             : 363
  Thread Count              : 9117

================================================================================
  MOTHERBOARD & SYSTEM INFORMATION
================================================================================
  System Model              : ASUS System Product Name
  Serial Number             : System Serial Number
  Motherboard Manufacturer  : ASUSTeK COMPUTER INC.
  Motherboard Model         : unknown
  Motherboard Version       : Rev 1.xx
  Motherboard Serial Number : 240842306500559
  BIOS Vendor               : American Megatrends Inc.
  BIOS Version              : ALASKA - 1072009
  BIOS Release Date         : 2024-06-24

================================================================================
  CPU (PROCESSOR) INFORMATION
================================================================================
  Processor Name            : Intel(R) Core(TM) i9-14900K
  Vendor                    : GenuineIntel
  Architecture / Microarch  : Raptor Lake
  Physical Cores (Sockets)  : 1 Package(s)
  Physical Cores            : 24
  Logical Processors        : 32
  64-bit Compatible         : Yes
  Max Frequency             : 3.19 GHz
  Vendor Frequency          : 3.19 GHz

================================================================================
  MEMORY (RAM) INFORMATION
================================================================================
  Total Physical RAM        : 63.81 GB
  Used Memory               : 14.76 GB
  Available Memory          : 49.05 GB
  Memory Usage              : 23.1%
  Virtual Memory Max        : 87.34 GB
  Virtual Memory Used       : 21.43 GB

  -- Installed Physical RAM Sticks (2) --
  [Stick #1] Patriot Memory (PDP Systems) DDR5 - 32.00 GB (4.80 GHz, BANK 0)
  [Stick #2] Patriot Memory (PDP Systems) DDR5 - 32.00 GB (4.80 GHz, BANK 0)

================================================================================
  GRAPHICS CARD (GPU) INFORMATION
================================================================================
  [GPU #1] NVIDIA GeForce RTX 4070 SUPER
    Vendor                  : NVIDIA
    Driver Version          : 32.0.16.1088
    VRAM Size               : 11.99 GB
    Device ID               : VideoController1


================================================================================
  STORAGE (DISKS & DRIVES) INFORMATION
================================================================================
  [Drive #1] Samsung SSD 850 EVO 500GB (Standard disk drives)
    Serial Number           : S2RANX0J317574L
    Capacity                : 465.76 GB
    Read Operations         : 333128 (12.23 GB)
    Write Operations        : 137173 (5.26 GB)

  [Drive #2] Samsung SSD 970 EVO Plus 2TB (Standard disk drives)
    Serial Number           : 0025_3854_3140_3DEC.
    Capacity                : 1.82 TB
    Read Operations         : 1306 (39.77 MB)
    Write Operations        : 354 (4.63 MB)

================================================================================
Hardware scan completed in 635 ms.
================================================================================
```

---

## Dependencies

- **[OSHI (oshi-core 6.6.5)](https://github.com/oshi/oshi)**: Free, open-source Java operating system and hardware information library for querying hardware and OS telemetry across Windows, Linux, macOS, Unix, and BSD.
- **[SLF4J (slf4j-simple 2.0.16)](https://www.slf4j.org/)**: Simple Logging Facade for Java.
