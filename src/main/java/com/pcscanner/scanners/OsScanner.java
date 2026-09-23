package com.pcscanner.scanners;

import com.pcscanner.utils.FormatUtils;
import oshi.SystemInfo;
import oshi.software.os.OperatingSystem;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class OsScanner {

    public static void scan(SystemInfo si) {
        FormatUtils.printHeader("OPERATING SYSTEM INFORMATION");

        OperatingSystem os = si.getOperatingSystem();

        FormatUtils.printRow("OS Name", os.getFamily());
        FormatUtils.printRow("Manufacturer", os.getManufacturer());
        FormatUtils.printRow("Version / Build", os.getVersionInfo().toString());
        FormatUtils.printRow("Architecture", os.getBitness() + "-bit");

        long bootTime = os.getSystemBootTime();
        String formattedBootTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochSecond(bootTime));
        FormatUtils.printRow("System Boot Time", formattedBootTime);

        long uptimeSeconds = os.getSystemUptime();
        long days = uptimeSeconds / 86400;
        long hours = (uptimeSeconds % 86400) / 3600;
        long minutes = (uptimeSeconds % 3600) / 60;
        long seconds = uptimeSeconds % 60;
        FormatUtils.printRow("System Uptime", String.format("%d days, %d hours, %d mins, %d secs", days, hours, minutes, seconds));

        FormatUtils.printRow("Process Count", String.valueOf(os.getProcessCount()));
        FormatUtils.printRow("Thread Count", String.valueOf(os.getThreadCount()));
    }
}
