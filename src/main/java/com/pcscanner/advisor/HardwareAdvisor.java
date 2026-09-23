package com.pcscanner.advisor;

import com.pcscanner.scanners.MotherboardScanner;
import com.pcscanner.utils.DiskMetadataResolver;
import com.pcscanner.utils.FormatUtils;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import java.util.ArrayList;
import java.util.List;

public class HardwareAdvisor {

    public static final String ASUS_Z790A_BIOS_URL =
            "https://rog.asus.com/motherboards/rog-strix/rog-strix-z790-a-gaming-wifi-ii/helpdesk_bios/";

    public static List<Advisory> evaluate(SystemInfo si) {
        List<Advisory> advisories = new ArrayList<>();

        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();
        CentralProcessor cpu = hal.getProcessor();
        GlobalMemory memory = hal.getMemory();
        List<HWDiskStore> disks = hal.getDiskStores();
        ComputerSystem computerSystem = hal.getComputerSystem();
        Baseboard baseboard = computerSystem.getBaseboard();
        Firmware firmware = computerSystem.getFirmware();

        String mbModel = MotherboardScanner.resolveMotherboardModel(baseboard);

        // 1. CPU & BIOS Stability: Intel 13th/14th Gen Vmin Shift / Voltage Degradation Check
        checkIntel1314GenStability(cpu, firmware, mbModel, advisories);

        // 2. Memory: DDR5 Speed & XMP Profile Check
        checkMemoryXmpStatus(cpu, memory, mbModel, advisories);

        // 3. Storage Free Space & Volume Capacity Check (e.g. C: Drive low space)
        checkVolumeSpace(os, advisories);

        // 4. Storage Expansion: Available Motherboard M.2 and SATA Slots
        checkStorageExpansion(mbModel, disks, advisories);

        // 5. Storage Bottlenecks: Mechanical HDD Warning
        checkMechanicalHddBottlenecks(disks, advisories);

        // 6. Solid State Drive Health: Samsung SSD Firmware & SMART Advisory
        checkSamsungSsdHealth(disks, advisories);

        // 7. SATA Port Saturation & Drive Consolidation
        checkSataPortSaturation(mbModel, disks, advisories);

        // 8. Operating System & System Uptime
        checkSystemUptime(os, advisories);

        return advisories;
    }

    private static void checkIntel1314GenStability(CentralProcessor cpu, Firmware firmware, String mbModel, List<Advisory> advisories) {
        String cpuName = cpu.getProcessorIdentifier().getName();
        String microarch = cpu.getProcessorIdentifier().getMicroarchitecture();
        String biosDate = firmware.getReleaseDate(); // e.g. "2024-06-24"

        boolean isIntel13or14GenDesktop = (cpuName.contains("14900") || cpuName.contains("14700") || cpuName.contains("14600")
                || cpuName.contains("13900") || cpuName.contains("13700") || cpuName.contains("13600")
                || (microarch != null && (microarch.contains("Raptor Lake"))));

        if (isIntel13or14GenDesktop) {
            boolean isOldBios = false;
            if (biosDate != null && !biosDate.trim().isEmpty()) {
                // Critical Intel 0x129 microcode was released August 2024 (2024-08)
                // 0x12B microcode was released September/October 2024 (2024-09/2024-10)
                if (biosDate.compareTo("2024-08-01") < 0) {
                    isOldBios = true;
                }
            }

            if (isOldBios) {
                String actionPlan = String.format(
                        "1. Download the latest BIOS (version 2503 or newer with 0x12B microcode) from the official ASUS Support page: %s%n" +
                        "2. Extract the .CAP file to a FAT32 USB drive or local disk.%n" +
                        "3. Use the included 'reboot-to-bios.cmd' shortcut to boot directly into ASUS EZ Flash 3 to apply the update.%n" +
                        "4. Ensure 'Intel Default Settings' (Baseline profile) is active in BIOS to prevent over-voltage damage.",
                        ASUS_Z790A_BIOS_URL
                );

                advisories.add(new Advisory(
                        Advisory.Severity.CRITICAL,
                        Advisory.Category.CPU,
                        "Critical Intel 13th/14th Gen Vmin Shift Voltage Degradation Advisory",
                        String.format("Your system is running an %s on BIOS release date %s. Intel confirmed a critical 'Vmin Shift Instability' defect affecting 13th/14th Gen desktop processors where elevated operating voltages permanently degrade CPU cores. Your current BIOS predates Intel's essential microcode mitigations (0x129 released August 2024 and 0x12B released late 2024).",
                                cpuName, biosDate != null ? biosDate : "Unknown"),
                        actionPlan
                ));
            }
        }
    }

    private static void checkMemoryXmpStatus(CentralProcessor cpu, GlobalMemory memory, String mbModel, List<Advisory> advisories) {
        List<PhysicalMemory> pmList = memory.getPhysicalMemory();
        if (pmList.isEmpty()) return;

        boolean hasDdr5 = false;
        long maxClockHz = 0;
        for (PhysicalMemory pm : pmList) {
            if ("DDR5".equalsIgnoreCase(pm.getMemoryType())) {
                hasDdr5 = true;
            }
            if (pm.getClockSpeed() > maxClockHz) {
                maxClockHz = pm.getClockSpeed();
            }
        }

        // 4.80 GHz / 4800 MHz is the standard fallback JEDEC baseline for DDR5
        if (hasDdr5 && maxClockHz <= 4_800_000_000L && maxClockHz > 0) {
            advisories.add(new Advisory(
                    Advisory.Severity.OPTIMIZATION,
                    Advisory.Category.MEMORY,
                    "DDR5 Memory Operating at JEDEC 4800 MHz (Check Intel XMP / AEMP Status)",
                    String.format("Your %s DDR5 RAM (%d physical sticks) is running at baseline JEDEC 4800 MHz (%s). Enthusiast DDR5 kits on Z790 motherboards are typically rated for 5600 MHz to 6400+ MHz, but will default to 4800 MHz until the Intel XMP (Extreme Memory Profile) or ASUS AEMP profile is explicitly enabled in BIOS.",
                            FormatUtils.formatBytes(memory.getTotal()), pmList.size(), FormatUtils.formatHertz(maxClockHz)),
                    "Run 'reboot-to-bios.cmd' to boot straight into your UEFI setup screen without pressing any keys, then enable 'XMP I' or 'XMP II' (or ASUS AEMP). This yields up to 15%–25% higher memory bandwidth for gaming and content creation."
            ));
        }
    }

    private static void checkVolumeSpace(OperatingSystem os, List<Advisory> advisories) {
        List<OSFileStore> stores = os.getFileSystem().getFileStores();
        for (OSFileStore store : stores) {
            long total = store.getTotalSpace();
            long usable = store.getUsableSpace();
            if (total <= 0) continue;

            double freeRatio = (double) usable / total;
            String mount = store.getMount();

            // Check if C: or primary system partition is low on space (< 15% free)
            if (mount != null && mount.toUpperCase().startsWith("C:") && freeRatio < 0.15) {
                advisories.add(new Advisory(
                        Advisory.Severity.WARNING,
                        Advisory.Category.STORAGE,
                        String.format("Low Free Space on Windows OS Drive (%s - %s Free)", mount, FormatUtils.formatPercentage(freeRatio)),
                        String.format("Your primary operating system drive (%s) only has %s free space remaining out of %s (%s utilized). SSD performance, background TRIM wear leveling, and Windows swap files degrade when free space falls below 10%%–15%%.",
                                mount, FormatUtils.formatBytes(usable), FormatUtils.formatBytes(total), FormatUtils.formatPercentage(1.0 - freeRatio)),
                        "Relocate large files, media folders, or game libraries from C: onto your 2TB NVMe SSD (I: drive, ~1.34 TB free) or 4TB SSD (J: drive, ~1.00 TB free) to keep C: below 80% capacity and maintain peak SSD write endurance."
                ));
            }
        }
    }

    private static void checkStorageExpansion(String mbModel, List<HWDiskStore> disks, List<Advisory> advisories) {
        boolean isZ790A = mbModel != null && mbModel.toUpperCase().contains("Z790-A");

        if (isZ790A) {
            int nvmeCount = 0;
            int sataCount = 0;

            for (HWDiskStore disk : disks) {
                DiskMetadataResolver.DiskInfo info = DiskMetadataResolver.resolve(disk.getModel());
                if ("NVMe".equalsIgnoreCase(info.getBusType())) {
                    nvmeCount++;
                } else if ("SATA".equalsIgnoreCase(info.getBusType())) {
                    sataCount++;
                }
            }

            int freeM2 = Math.max(0, 5 - nvmeCount);
            int freeSata = Math.max(0, 4 - sataCount);

            advisories.add(new Advisory(
                    Advisory.Severity.EXPANSION,
                    Advisory.Category.STORAGE,
                    String.format("Significant NVMe Expansion Available (%d Free M.2 PCIe 4.0 Slots)", freeM2),
                    String.format("Your %s motherboard features 5 dedicated M.2 PCIe 4.0 x4 slots with pre-installed heatsinks and tool-free M.2 Q-Latches. You currently have %d NVMe drive installed, leaving %d M.2 slots open. You also have %d of 4 SATA ports available.",
                            mbModel, nvmeCount, freeM2, freeSata),
                    String.format("When adding fast storage, purchase M.2 PCIe 4.0 NVMe SSDs (e.g., Samsung 990 PRO, Crucial T500, WD Black SN850X). Adding 4x 4TB NVMe SSDs will provide +16 TB of ~7,000 MB/s storage without any SATA cables.")
            ));
        }
    }

    private static void checkMechanicalHddBottlenecks(List<HWDiskStore> disks, List<Advisory> advisories) {
        for (HWDiskStore disk : disks) {
            String model = disk.getModel() != null ? disk.getModel() : "";
            // Seagate ST2000DM006 is a known 3.5" 7200 RPM mechanical HDD
            if (model.contains("ST2000DM") || model.contains("ST1000") || model.contains("Barracuda") || model.contains("WD Blue") || model.contains("WD Red")) {
                advisories.add(new Advisory(
                        Advisory.Severity.WARNING,
                        Advisory.Category.STORAGE,
                        String.format("Mechanical Spinning Hard Drive Detected (%s)", model),
                        String.format("The drive '%s' (%s) is a mechanical spinning hard disk drive. Mechanical HDDs have high access latency (10–15 ms seek time) and throughput capped around ~150–200 MB/s, compared to 3,500–7,000 MB/s on your solid-state drives.",
                                model, FormatUtils.formatBytes(disk.getSize())),
                        "Designate this mechanical drive strictly for secondary cold backups, archives, or media files. Avoid installing the Windows OS, competitive games, or frequently launched apps on this drive."
                ));
                break;
            }
        }
    }

    private static void checkSamsungSsdHealth(List<HWDiskStore> disks, List<Advisory> advisories) {
        List<String> samsungDrives = new ArrayList<>();
        for (HWDiskStore disk : disks) {
            String model = disk.getModel() != null ? disk.getModel() : "";
            if (model.toUpperCase().contains("SAMSUNG")) {
                samsungDrives.add(model);
            }
        }

        if (!samsungDrives.isEmpty()) {
            advisories.add(new Advisory(
                    Advisory.Severity.OPTIMIZATION,
                    Advisory.Category.STORAGE,
                    "Samsung SSD Controller Firmware & Health Check Advisory",
                    String.format("Detected %d Samsung Solid-State Drive(s): %s. Certain revisions of Samsung EVO SSDs (notably early 870 EVO batches and select 970/980/990 firmware releases) had documented wear issues that were resolved through official firmware patches.",
                            samsungDrives.size(), String.join(", ", samsungDrives)),
                    "Install and run Samsung Magician to inspect the SMART Health status, verify Total Bytes Written (TBW), and confirm each drive is updated to the latest Samsung controller firmware."
            ));
        }
    }

    private static void checkSataPortSaturation(String mbModel, List<HWDiskStore> disks, List<Advisory> advisories) {
        boolean has850Evo = false;
        for (HWDiskStore disk : disks) {
            String model = disk.getModel() != null ? disk.getModel() : "";
            if (model.contains("850 EVO")) {
                has850Evo = true;
                break;
            }
        }

        if (has850Evo) {
            advisories.add(new Advisory(
                    Advisory.Severity.OPTIMIZATION,
                    Advisory.Category.STORAGE,
                    "Legacy SATA Port Consolidation Opportunity (Samsung 850 EVO 500GB)",
                    "You have a 500GB Samsung 850 EVO SATA SSD occupying one of your four motherboard SATA ports. As smaller SATA SSDs age, consolidating them onto a high-capacity NVMe drive frees up physical SATA headers and eliminates SATA data/power cabling inside your case.",
                    "Consider migrating older small-capacity SATA drives into a single partition on your 2TB/4TB SSDs to free up SATA ports for ultra-high-capacity mass storage HDDs."
            ));
        }
    }

    private static void checkSystemUptime(OperatingSystem os, List<Advisory> advisories) {
        long uptimeDays = os.getSystemUptime() / 86400;
        if (uptimeDays > 14) {
            advisories.add(new Advisory(
                    Advisory.Severity.OPTIMIZATION,
                    Advisory.Category.SYSTEM,
                    String.format("Extended System Uptime Detected (%d Days)", uptimeDays),
                    String.format("Your computer has been running continuously for %d days without a full reboot. Over extended periods, background worker threads and OS memory caches can fragment memory and degrade performance.", uptimeDays),
                    "Perform a system restart to clear cached memory, reset pending Windows update stages, and ensure peak performance."
            ));
        }
    }
}
