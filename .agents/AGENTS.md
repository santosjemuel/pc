# Project Memory & Agent Rules: `pc`

## Project Goal
Migrate Windows OS from the current 500GB SATA SSD (`C:\`) to the 2TB NVMe PCIe 3.0 SSD (`I:\`), and evaluate hardware expansion on the ASUS ROG STRIX Z790-A motherboard.

## System Configuration
- **Current OS Drive (`C:\`)**: Samsung SSD 970 EVO Plus 2TB (NVMe PCIe 3.0 x4) — **Active Boot & System**
- **Legacy OS Drive (`E:\`)**: Samsung SSD 850 EVO 500GB (SATA III) — Former C: drive, retained as backup
- **Motherboard**: ASUS ROG STRIX Z790-A GAMING WIFI II
- **CPU**: Intel Core i9-14900K

## Completed Milestones
- [x] Hardware component scan completed ([scan-report.txt](file:///output/scan-report.txt)).
- [x] Motherboard M.2 expansion capability verified (5x PCIe 4.0 slots available).
- [x] Comparative performance analysis conducted (850 EVO vs 970 EVO Plus vs 990 PRO).
- [x] Dedicated repository memory system configured at `.agents/`.
- [x] Samsung Data Migration executed and verified.
- [x] BIOS boot sequence successfully configured to Samsung 970 EVO Plus.
- [x] NVMe Boot & Volume Validation: 970 EVO Plus confirmed as active `C:\` (IsBoot: True, IsSystem: True, 1.49 TB free space). Old 850 EVO reassigned to `E:\`.
- [x] Fixed Maven portable path in `mvnw.cmd` and refreshed diagnostic reports.

## Current Active Task / Next Immediate Step
- [ ] Monitor system stability for 24-48 hours before formatting or reallocating legacy OS drive (`E:\`).
- [ ] (Optional) Enable Intel XMP / ASUS AEMP in BIOS via `reboot-to-bios.cmd` to increase DDR5 speed from 4800 MHz to full rated speed.

## Key Decisions & Notes
- Windows successfully booted off the 970 EVO Plus NVMe; Windows automatically mounted it as the primary system `C:\` partition.
- Legacy 850 EVO partition was automatically mounted to `E:\`, keeping all previous files safe as a fallback clone.
