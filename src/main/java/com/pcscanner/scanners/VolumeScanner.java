package com.pcscanner.scanners;

import com.pcscanner.utils.FormatUtils;
import oshi.SystemInfo;
import oshi.software.os.OSFileStore;

import java.util.List;

public class VolumeScanner {

    public static void scan(SystemInfo si) {
        FormatUtils.printHeader("LOGICAL VOLUMES & DRIVE PARTITIONS");

        List<OSFileStore> fileStores = si.getOperatingSystem().getFileSystem().getFileStores();
        if (fileStores.isEmpty()) {
            System.out.println("  No logical volumes detected.");
            return;
        }

        int count = 1;
        for (OSFileStore fs : fileStores) {
            long total = fs.getTotalSpace();
            if (total <= 0) continue; // Skip unmounted or 0-byte media slots

            long usable = fs.getUsableSpace();
            long used = total - usable;
            double usedRatio = total > 0 ? ((double) used / total) : 0;

            String label = fs.getLabel() != null && !fs.getLabel().trim().isEmpty() ? fs.getLabel() : "Local Disk";
            System.out.printf("  [Volume #%d] %s (%s) [%s]%n", count++, fs.getMount(), label, fs.getType());
            FormatUtils.printRow("  Total Capacity", FormatUtils.formatBytes(total));
            FormatUtils.printRow("  Free Space", FormatUtils.formatBytes(usable));
            FormatUtils.printRow("  Used Space", FormatUtils.formatBytes(used) + " (" + FormatUtils.formatPercentage(usedRatio) + ")");
            System.out.println();
        }
    }
}
