package com.pcscanner.scanners;

import com.pcscanner.utils.FormatUtils;
import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;

import java.util.List;

public class GpuScanner {

    public static void scan(SystemInfo si) {
        FormatUtils.printHeader("GRAPHICS CARD (GPU) INFORMATION");

        List<GraphicsCard> gpus = si.getHardware().getGraphicsCards();

        if (gpus.isEmpty()) {
            System.out.println("  No dedicated graphics cards detected.");
            return;
        }

        int count = 1;
        for (GraphicsCard gpu : gpus) {
            System.out.printf("  [GPU #%d] %s%n", count++, gpu.getName());
            FormatUtils.printRow("  Vendor", gpu.getVendor());
            FormatUtils.printRow("  Driver Version", gpu.getVersionInfo());
            if (gpu.getVRam() > 0) {
                FormatUtils.printRow("  VRAM Size", FormatUtils.formatBytes(gpu.getVRam()));
            }
            FormatUtils.printRow("  Device ID", gpu.getDeviceId());
            System.out.println();
        }
    }
}
