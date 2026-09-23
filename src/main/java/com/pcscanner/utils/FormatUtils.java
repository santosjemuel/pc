package com.pcscanner.utils;

public class FormatUtils {

    public static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.2f %cB", bytes / Math.pow(1024, exp), pre);
    }

    public static String formatHertz(long hertz) {
        if (hertz >= 1_000_000_000L) {
            return String.format("%.2f GHz", hertz / 1_000_000_000.0);
        } else if (hertz >= 1_000_000L) {
            return String.format("%.2f MHz", hertz / 1_000_000.0);
        } else if (hertz >= 1_000L) {
            return String.format("%.2f kHz", hertz / 1_000.0);
        } else {
            return hertz + " Hz";
        }
    }

    public static String formatPercentage(double value) {
        return String.format("%.1f%%", value * 100);
    }

    public static void printHeader(String title) {
        String border = "================================================================================";
        System.out.println("\n" + border);
        System.out.printf("  %s%n", title);
        System.out.println(border);
    }

    public static void printRow(String label, String value) {
        System.out.printf("  %-25s : %s%n", label, value != null && !value.trim().isEmpty() ? value : "N/A");
    }

    public static void printAdvisory(int index, String severity, String category, String title, String description, String action) {
        System.out.printf("  [%d] [%s] %s%n", index, severity, title);
        System.out.printf("      Category    : %s%n", category);
        System.out.printf("      Details     : %s%n", description);
        System.out.printf("      Action Plan : %s%n%n", action);
    }
}

