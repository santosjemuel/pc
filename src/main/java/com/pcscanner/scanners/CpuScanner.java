package com.pcscanner.scanners;

import com.pcscanner.utils.FormatUtils;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

public class CpuScanner {

    public static void scan(SystemInfo si) {
        FormatUtils.printHeader("CPU (PROCESSOR) INFORMATION");

        CentralProcessor cpu = si.getHardware().getProcessor();
        CentralProcessor.ProcessorIdentifier id = cpu.getProcessorIdentifier();

        FormatUtils.printRow("Processor Name", id.getName());
        FormatUtils.printRow("Vendor", id.getVendor());
        FormatUtils.printRow("Architecture / Microarch", id.getMicroarchitecture());
        FormatUtils.printRow("Physical Cores (Sockets)", cpu.getPhysicalPackageCount() + " Package(s)");
        FormatUtils.printRow("Physical Cores", String.valueOf(cpu.getPhysicalProcessorCount()));
        FormatUtils.printRow("Logical Processors", String.valueOf(cpu.getLogicalProcessorCount()));
        FormatUtils.printRow("64-bit Compatible", id.isCpu64bit() ? "Yes" : "No");

        long maxFreq = cpu.getMaxFreq();
        if (maxFreq > 0) {
            FormatUtils.printRow("Max Frequency", FormatUtils.formatHertz(maxFreq));
        }

        long vendorFreq = id.getVendorFreq();
        if (vendorFreq > 0) {
            FormatUtils.printRow("Vendor Frequency", FormatUtils.formatHertz(vendorFreq));
        }
    }
}
