package com.pcscanner;

import com.pcscanner.reports.HtmlReportGenerator;
import com.pcscanner.scanners.*;
import oshi.SystemInfo;

import java.io.*;

public class Main {

    public static void main(String[] args) {
        // Prepare output directory
        File outputDir = new File("output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // Setup dual output stream to capture console output for text report
        PrintStream originalOut = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();

        OutputStream dualOutputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                originalOut.write(b);
                capturedOutput.write(b);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                originalOut.write(b, off, len);
                capturedOutput.write(b, off, len);
            }

            @Override
            public void flush() throws IOException {
                originalOut.flush();
                capturedOutput.flush();
            }
        };

        try {
            PrintStream teeStream = new PrintStream(dualOutputStream, true, "UTF-8");
            System.setOut(teeStream);
        } catch (UnsupportedEncodingException ignored) {
        }

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

        // Restore original output stream
        System.setOut(originalOut);

        // Save plain-text report
        File textReportFile = new File(outputDir, "scan-report.txt");
        try (FileOutputStream fos = new FileOutputStream(textReportFile)) {
            fos.write(capturedOutput.toByteArray());
        } catch (IOException e) {
            System.err.println("Failed to write text report: " + e.getMessage());
        }

        // Save HTML report
        File htmlReportFile = new File(outputDir, "scan-report.html");
        try {
            HtmlReportGenerator.generate(si, elapsedTime, htmlReportFile);
        } catch (IOException e) {
            System.err.println("Failed to generate HTML report: " + e.getMessage());
        }

        System.out.println();
        System.out.println("Scan reports generated successfully:");
        System.out.println("  -> [Text Report] " + textReportFile.getAbsolutePath());
        System.out.println("  -> [HTML Report] " + htmlReportFile.getAbsolutePath());
        System.out.println("     (Open output/scan-report.html in your browser)");
    }
}

