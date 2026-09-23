package com.pcscanner.scanners;

import com.pcscanner.utils.FormatUtils;
import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;

import java.util.List;

public class DiskScanner {

    public static void scan(SystemInfo si) {
        FormatUtils.printHeader("STORAGE (DISKS & DRIVES) INFORMATION");

        List<HWDiskStore> disks = si.getHardware().getDiskStores();

        if (disks.isEmpty()) {
            System.out.println("  No physical disk drives detected.");
            return;
        }

        int count = 1;
        for (HWDiskStore disk : disks) {
            com.pcscanner.utils.DiskMetadataResolver.DiskInfo info = com.pcscanner.utils.DiskMetadataResolver.resolve(disk.getModel());
            System.out.printf("  [Drive #%d] %s (%s)%n", count++, disk.getModel(), info.getBusType());
            FormatUtils.printRow("  Bus Interface", info.getBusType());
            FormatUtils.printRow("  Health Status", info.getHealthStatus());
            FormatUtils.printRow("  Serial Number", disk.getSerial());
            FormatUtils.printRow("  Capacity", FormatUtils.formatBytes(disk.getSize()));
            FormatUtils.printRow("  Read Operations", disk.getReads() + " (" + FormatUtils.formatBytes(disk.getReadBytes()) + ")");
            FormatUtils.printRow("  Write Operations", disk.getWrites() + " (" + FormatUtils.formatBytes(disk.getWriteBytes()) + ")");
            System.out.println();
        }
    }
}
