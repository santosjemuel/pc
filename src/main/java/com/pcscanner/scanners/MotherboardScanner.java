package com.pcscanner.scanners;

import com.pcscanner.utils.FormatUtils;
import oshi.SystemInfo;
import oshi.hardware.Baseboard;
import oshi.hardware.ComputerSystem;
import oshi.hardware.Firmware;

public class MotherboardScanner {

    public static void scan(SystemInfo si) {
        FormatUtils.printHeader("MOTHERBOARD & SYSTEM INFORMATION");

        ComputerSystem system = si.getHardware().getComputerSystem();
        FormatUtils.printRow("System Model", system.getManufacturer() + " " + system.getModel());
        FormatUtils.printRow("Serial Number", system.getSerialNumber());

        Baseboard baseboard = system.getBaseboard();
        FormatUtils.printRow("Motherboard Manufacturer", baseboard.getManufacturer());
        FormatUtils.printRow("Motherboard Model", baseboard.getModel());
        FormatUtils.printRow("Motherboard Version", baseboard.getVersion());
        FormatUtils.printRow("Motherboard Serial Number", baseboard.getSerialNumber());

        Firmware firmware = system.getFirmware();
        FormatUtils.printRow("BIOS Vendor", firmware.getManufacturer());
        FormatUtils.printRow("BIOS Version", firmware.getVersion());
        FormatUtils.printRow("BIOS Release Date", firmware.getReleaseDate());
    }
}
