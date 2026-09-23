package com.pcscanner.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiskMetadataResolver {

    public static class DiskInfo {
        private final String busType;
        private final String healthStatus;

        public DiskInfo(String busType, String healthStatus) {
            this.busType = busType;
            this.healthStatus = healthStatus;
        }

        public String getBusType() {
            return busType;
        }

        public String getHealthStatus() {
            return healthStatus;
        }
    }

    private static Map<String, DiskInfo> cachedInfo = null;

    public static synchronized Map<String, DiskInfo> getPhysicalDiskMap() {
        if (cachedInfo != null) {
            return cachedInfo;
        }

        cachedInfo = new HashMap<>();
        try {
            Process process = new ProcessBuilder(
                    "powershell", "-NoProfile", "-Command",
                    "Get-PhysicalDisk | Select-Object FriendlyName, BusType, HealthStatus | ConvertTo-Json -Compress"
            ).start();

            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            String json = sb.toString().trim();
            Pattern pattern = Pattern.compile("\"FriendlyName\":\"([^\"]+)\",\"BusType\":\"([^\"]+)\",\"HealthStatus\":\"([^\"]+)\"");
            Matcher matcher = pattern.matcher(json);

            while (matcher.find()) {
                String friendlyName = matcher.group(1).trim();
                String busType = matcher.group(2).trim();
                String healthStatus = matcher.group(3).trim();
                cachedInfo.put(friendlyName.toLowerCase(), new DiskInfo(busType, healthStatus));
            }
        } catch (Exception ignored) {
        }

        return cachedInfo;
    }

    public static DiskInfo resolve(String modelName) {
        if (modelName == null || modelName.trim().isEmpty()) {
            return new DiskInfo("Unknown", "OK");
        }

        Map<String, DiskInfo> map = getPhysicalDiskMap();
        String lowerModel = modelName.toLowerCase();

        // Exact or fuzzy match
        for (Map.Entry<String, DiskInfo> entry : map.entrySet()) {
            String key = entry.getKey();
            if (lowerModel.contains(key) || key.contains(lowerModel)) {
                return entry.getValue();
            }
        }

        // Heuristics fallback
        String fallbackBus = "SATA";
        if (lowerModel.contains("nvme") || lowerModel.contains("970 evo")) {
            fallbackBus = "NVMe";
        } else if (lowerModel.contains("usb") || lowerModel.contains("my book") || lowerModel.contains("rw520")) {
            fallbackBus = "USB";
        }

        return new DiskInfo(fallbackBus, "Healthy");
    }
}
