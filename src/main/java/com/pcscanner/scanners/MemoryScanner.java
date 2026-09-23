package com.pcscanner.scanners;

import com.pcscanner.utils.FormatUtils;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.PhysicalMemory;
import oshi.hardware.VirtualMemory;

import java.util.List;

public class MemoryScanner {

    public static void scan(SystemInfo si) {
        FormatUtils.printHeader("MEMORY (RAM) INFORMATION");

        GlobalMemory memory = si.getHardware().getMemory();

        long total = memory.getTotal();
        long available = memory.getAvailable();
        long used = total - available;

        FormatUtils.printRow("Total Physical RAM", FormatUtils.formatBytes(total));
        FormatUtils.printRow("Used Memory", FormatUtils.formatBytes(used));
        FormatUtils.printRow("Available Memory", FormatUtils.formatBytes(available));
        FormatUtils.printRow("Memory Usage", FormatUtils.formatPercentage((double) used / total));

        VirtualMemory vm = memory.getVirtualMemory();
        FormatUtils.printRow("Virtual Memory Max", FormatUtils.formatBytes(vm.getVirtualMax()));
        FormatUtils.printRow("Virtual Memory Used", FormatUtils.formatBytes(vm.getVirtualInUse()));

        List<PhysicalMemory> pmList = memory.getPhysicalMemory();
        if (!pmList.isEmpty()) {
            System.out.println("\n  -- Installed Physical RAM Sticks (" + pmList.size() + ") --");
            int slot = 1;
            for (PhysicalMemory pm : pmList) {
                System.out.printf("  [Stick #%d] %s %s - %s (%s, %s)%n",
                        slot++,
                        pm.getManufacturer(),
                        pm.getMemoryType(),
                        FormatUtils.formatBytes(pm.getCapacity()),
                        FormatUtils.formatHertz(pm.getClockSpeed()),
                        pm.getBankLabel());
            }
        }
    }
}
