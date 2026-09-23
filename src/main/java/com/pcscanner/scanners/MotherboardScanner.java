package com.pcscanner.scanners;

import com.pcscanner.utils.FormatUtils;
import oshi.SystemInfo;
import oshi.hardware.Baseboard;
import oshi.hardware.ComputerSystem;
import oshi.hardware.Firmware;

public class MotherboardScanner {

    public static String resolveMotherboardModel(Baseboard baseboard) {
        String model = baseboard != null ? baseboard.getModel() : null;
        if (model != null && !model.trim().isEmpty() && !model.equalsIgnoreCase("unknown")) {
            return model.trim();
        }

        // On Windows, query Registry HKLM\HARDWARE\DESCRIPTION\System\BIOS\BaseBoardProduct
        try {
            Process process = new ProcessBuilder("reg", "query", "HKLM\\HARDWARE\\DESCRIPTION\\System\\BIOS", "/v", "BaseBoardProduct").start();
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("BaseBoardProduct")) {
                        String[] tokens = line.trim().split("\\s+");
                        if (tokens.length >= 3) {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 2; i < tokens.length; i++) {
                                if (i > 2) sb.append(" ");
                                sb.append(tokens[i]);
                            }
                            String regModel = sb.toString().trim();
                            if (!regModel.isEmpty()) {
                                return regModel;
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return model != null ? model : "unknown";
    }

    public static void scan(SystemInfo si) {
        FormatUtils.printHeader("MOTHERBOARD & SYSTEM INFORMATION");

        ComputerSystem system = si.getHardware().getComputerSystem();
        FormatUtils.printRow("System Model", system.getManufacturer() + " " + system.getModel());
        FormatUtils.printRow("Serial Number", system.getSerialNumber());

        Baseboard baseboard = system.getBaseboard();
        FormatUtils.printRow("Motherboard Manufacturer", baseboard.getManufacturer());
        FormatUtils.printRow("Motherboard Model", resolveMotherboardModel(baseboard));
        FormatUtils.printRow("Motherboard Version", baseboard.getVersion());
        FormatUtils.printRow("Motherboard Serial Number", baseboard.getSerialNumber());

        Firmware firmware = system.getFirmware();
        FormatUtils.printRow("BIOS Vendor", firmware.getManufacturer());
        FormatUtils.printRow("BIOS Version", firmware.getVersion());
        FormatUtils.printRow("BIOS Release Date", firmware.getReleaseDate());
    }
}

