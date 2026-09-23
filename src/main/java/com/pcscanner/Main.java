package com.pcscanner;

import com.pcscanner.scanners.*;
import oshi.SystemInfo;

public class Main {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("                     PC HARDWARE & SYSTEM COMPONENT SCANNER                     ");
        System.out.println("================================================================================");

        System.out.println("Scanning hardware components... Please wait...\n");

        long startTime = System.currentTimeMillis();
        SystemInfo si = new SystemInfo();

        OsScanner.scan(si);
        MotherboardScanner.scan(si);
        CpuScanner.scan(si);
        MemoryScanner.scan(si);
        GpuScanner.scan(si);
        DiskScanner.scan(si);

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("================================================================================");
        System.out.printf("Hardware scan completed in %d ms.%n", elapsedTime);
        System.out.println("================================================================================");
    }
}
