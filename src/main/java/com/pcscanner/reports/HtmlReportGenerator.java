package com.pcscanner.reports;

import com.pcscanner.utils.FormatUtils;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OperatingSystem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HtmlReportGenerator {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    public static void generate(SystemInfo si, long elapsedTimeMs, File outputFile) throws IOException {
        OperatingSystem os = si.getOperatingSystem();
        HardwareAbstractionLayer hal = si.getHardware();
        ComputerSystem computerSystem = hal.getComputerSystem();
        CentralProcessor cpu = hal.getProcessor();
        GlobalMemory memory = hal.getMemory();
        List<GraphicsCard> gpus = hal.getGraphicsCards();
        List<HWDiskStore> disks = hal.getDiskStores();

        long uptimeSeconds = os.getSystemUptime();
        long days = uptimeSeconds / 86400;
        long hours = (uptimeSeconds % 86400) / 3600;
        long minutes = (uptimeSeconds % 3600) / 60;
        long seconds = uptimeSeconds % 60;
        String formattedUptime = String.format("%d d, %d h, %d m, %d s", days, hours, minutes, seconds);

        long totalMem = memory.getTotal();
        long availableMem = memory.getAvailable();
        long usedMem = totalMem - availableMem;
        double memUsageRatio = totalMem > 0 ? ((double) usedMem / totalMem) : 0;
        double memUsagePercent = memUsageRatio * 100.0;

        long totalDiskStorage = 0;
        for (HWDiskStore disk : disks) {
            totalDiskStorage += disk.getSize();
        }

        StringBuilder html = new StringBuilder(16384);
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"en\">\n<head>\n");
        html.append("  <meta charset=\"UTF-8\">\n");
        html.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("  <title>PC Hardware Scan Report</title>\n");
        html.append("  <style>\n");
        html.append("    :root {\n");
        html.append("      --bg: #0b0f19;\n");
        html.append("      --surface: #131b2e;\n");
        html.append("      --surface-border: #1e293b;\n");
        html.append("      --surface-hover: #1e2942;\n");
        html.append("      --text-main: #f8fafc;\n");
        html.append("      --text-muted: #94a3b8;\n");
        html.append("      --accent-blue: #38bdf8;\n");
        html.append("      --accent-indigo: #818cf8;\n");
        html.append("      --accent-emerald: #34d399;\n");
        html.append("      --accent-purple: #c084fc;\n");
        html.append("      --accent-amber: #fbbf24;\n");
        html.append("    }\n");
        html.append("    * { box-sizing: border-box; margin: 0; padding: 0; }\n");
        html.append("    body {\n");
        html.append("      background: var(--bg);\n");
        html.append("      color: var(--text-main);\n");
        html.append("      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;\n");
        html.append("      line-height: 1.5;\n");
        html.append("      padding: 32px 24px;\n");
        html.append("    }\n");
        html.append("    .container { max-width: 1200px; margin: 0 auto; }\n");
        html.append("    header {\n");
        html.append("      display: flex;\n");
        html.append("      flex-wrap: wrap;\n");
        html.append("      justify-content: space-between;\n");
        html.append("      align-items: center;\n");
        html.append("      gap: 16px;\n");
        html.append("      padding-bottom: 24px;\n");
        html.append("      margin-bottom: 24px;\n");
        html.append("      border-bottom: 1px solid var(--surface-border);\n");
        html.append("    }\n");
        html.append("    .header-title h1 {\n");
        html.append("      font-size: 1.85rem;\n");
        html.append("      font-weight: 700;\n");
        html.append("      background: linear-gradient(135deg, #38bdf8, #818cf8);\n");
        html.append("      -webkit-background-clip: text;\n");
        html.append("      -webkit-text-fill-color: transparent;\n");
        html.append("    }\n");
        html.append("    .header-title p { color: var(--text-muted); font-size: 0.92rem; margin-top: 4px; }\n");
        html.append("    .badges { display: flex; gap: 8px; flex-wrap: wrap; }\n");
        html.append("    .badge {\n");
        html.append("      background: #1e293b;\n");
        html.append("      border: 1px solid #334155;\n");
        html.append("      color: var(--text-main);\n");
        html.append("      font-size: 0.8rem;\n");
        html.append("      font-weight: 600;\n");
        html.append("      padding: 4px 10px;\n");
        html.append("      border-radius: 9999px;\n");
        html.append("    }\n");
        html.append("    .badge-accent { border-color: var(--accent-blue); color: var(--accent-blue); }\n");
        html.append("    .quick-stats {\n");
        html.append("      display: grid;\n");
        html.append("      grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));\n");
        html.append("      gap: 16px;\n");
        html.append("      margin-bottom: 32px;\n");
        html.append("    }\n");
        html.append("    .stat-card {\n");
        html.append("      background: var(--surface);\n");
        html.append("      border: 1px solid var(--surface-border);\n");
        html.append("      border-radius: 12px;\n");
        html.append("      padding: 18px 20px;\n");
        html.append("      box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.2);\n");
        html.append("    }\n");
        html.append("    .stat-card .label {\n");
        html.append("      font-size: 0.78rem;\n");
        html.append("      text-transform: uppercase;\n");
        html.append("      letter-spacing: 0.05em;\n");
        html.append("      color: var(--text-muted);\n");
        html.append("      margin-bottom: 6px;\n");
        html.append("    }\n");
        html.append("    .stat-card .value {\n");
        html.append("      font-size: 1.15rem;\n");
        html.append("      font-weight: 600;\n");
        html.append("      white-space: nowrap;\n");
        html.append("      overflow: hidden;\n");
        html.append("      text-overflow: ellipsis;\n");
        html.append("    }\n");
        html.append("    .stat-card .sub {\n");
        html.append("      font-size: 0.8rem;\n");
        html.append("      color: var(--text-muted);\n");
        html.append("      margin-top: 4px;\n");
        html.append("    }\n");
        html.append("    .grid-2 {\n");
        html.append("      display: grid;\n");
        html.append("      grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));\n");
        html.append("      gap: 24px;\n");
        html.append("      margin-bottom: 24px;\n");
        html.append("    }\n");
        html.append("    @media (max-width: 600px) {\n");
        html.append("      .grid-2 { grid-template-columns: 1fr; }\n");
        html.append("    }\n");
        html.append("    .panel {\n");
        html.append("      background: var(--surface);\n");
        html.append("      border: 1px solid var(--surface-border);\n");
        html.append("      border-radius: 12px;\n");
        html.append("      padding: 24px;\n");
        html.append("      box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.2);\n");
        html.append("    }\n");
        html.append("    .panel-header {\n");
        html.append("      display: flex;\n");
        html.append("      align-items: center;\n");
        html.append("      gap: 10px;\n");
        html.append("      margin-bottom: 18px;\n");
        html.append("      padding-bottom: 12px;\n");
        html.append("      border-bottom: 1px solid var(--surface-border);\n");
        html.append("    }\n");
        html.append("    .panel-header h2 {\n");
        html.append("      font-size: 1.15rem;\n");
        html.append("      font-weight: 600;\n");
        html.append("    }\n");
        html.append("    .panel-icon {\n");
        html.append("      width: 28px;\n");
        html.append("      height: 28px;\n");
        html.append("      display: flex;\n");
        html.append("      align-items: center;\n");
        html.append("      justify-content: center;\n");
        html.append("      border-radius: 6px;\n");
        html.append("      background: #1e293b;\n");
        html.append("      font-size: 1rem;\n");
        html.append("    }\n");
        html.append("    table.data-table {\n");
        html.append("      width: 100%;\n");
        html.append("      border-collapse: collapse;\n");
        html.append("      font-size: 0.9rem;\n");
        html.append("    }\n");
        html.append("    table.data-table th, table.data-table td {\n");
        html.append("      padding: 10px 12px;\n");
        html.append("      text-align: left;\n");
        html.append("      border-bottom: 1px solid #1e293b;\n");
        html.append("    }\n");
        html.append("    table.data-table th {\n");
        html.append("      color: var(--text-muted);\n");
        html.append("      font-weight: 500;\n");
        html.append("      font-size: 0.8rem;\n");
        html.append("      text-transform: uppercase;\n");
        html.append("      letter-spacing: 0.05em;\n");
        html.append("    }\n");
        html.append("    table.data-table td.label-cell {\n");
        html.append("      color: var(--text-muted);\n");
        html.append("      width: 40%;\n");
        html.append("    }\n");
        html.append("    table.data-table td.value-cell {\n");
        html.append("      color: var(--text-main);\n");
        html.append("      font-weight: 500;\n");
        html.append("    }\n");
        html.append("    .progress-bar-container {\n");
        html.append("      background: #1e293b;\n");
        html.append("      border-radius: 9999px;\n");
        html.append("      height: 10px;\n");
        html.append("      overflow: hidden;\n");
        html.append("      margin: 12px 0 16px 0;\n");
        html.append("    }\n");
        html.append("    .progress-bar-fill {\n");
        html.append("      height: 100%;\n");
        html.append("      background: linear-gradient(90deg, #38bdf8, #818cf8);\n");
        html.append("      border-radius: 9999px;\n");
        html.append("    }\n");
        html.append("    .ram-sticks {\n");
        html.append("      margin-top: 16px;\n");
        html.append("      padding-top: 14px;\n");
        html.append("      border-top: 1px solid var(--surface-border);\n");
        html.append("    }\n");
        html.append("    .ram-sticks h3 {\n");
        html.append("      font-size: 0.85rem;\n");
        html.append("      color: var(--text-muted);\n");
        html.append("      text-transform: uppercase;\n");
        html.append("      margin-bottom: 10px;\n");
        html.append("    }\n");
        html.append("    .item-card {\n");
        html.append("      background: #0d1322;\n");
        html.append("      border: 1px solid #1e293b;\n");
        html.append("      border-radius: 8px;\n");
        html.append("      padding: 12px 14px;\n");
        html.append("      margin-bottom: 10px;\n");
        html.append("    }\n");
        html.append("    .item-card:last-child { margin-bottom: 0; }\n");
        html.append("    .item-card-title {\n");
        html.append("      font-weight: 600;\n");
        html.append("      color: var(--accent-blue);\n");
        html.append("      margin-bottom: 6px;\n");
        html.append("    }\n");
        html.append("    footer {\n");
        html.append("      margin-top: 36px;\n");
        html.append("      padding-top: 20px;\n");
        html.append("      border-top: 1px solid var(--surface-border);\n");
        html.append("      display: flex;\n");
        html.append("      justify-content: space-between;\n");
        html.append("      align-items: center;\n");
        html.append("      font-size: 0.82rem;\n");
        html.append("      color: var(--text-muted);\n");
        html.append("      flex-wrap: wrap;\n");
        html.append("      gap: 12px;\n");
        html.append("    }\n");
        html.append("  </style>\n</head>\n<body>\n");
        html.append("<div class=\"container\">\n");

        // Header
        html.append("  <header>\n");
        html.append("    <div class=\"header-title\">\n");
        html.append("      <h1>PC Hardware & System Report</h1>\n");
        html.append("      <p>Generated on ").append(DATE_TIME_FORMATTER.format(Instant.now())).append("</p>\n");
        html.append("    </div>\n");
        html.append("    <div class=\"badges\">\n");
        html.append("      <span class=\"badge badge-accent\">").append(escape(os.getFamily())).append(" ")
            .append(escape(os.getBitness() + "-bit")).append("</span>\n");
        html.append("      <span class=\"badge\">Scan: ").append(elapsedTimeMs).append(" ms</span>\n");
        html.append("      <span class=\"badge\">OSHI 6.6.5</span>\n");
        html.append("    </div>\n");
        html.append("  </header>\n");

        // Quick Stats row
        html.append("  <section class=\"quick-stats\">\n");

        // Stat 1: CPU
        html.append("    <div class=\"stat-card\">\n");
        html.append("      <div class=\"label\">Processor</div>\n");
        html.append("      <div class=\"value\" title=\"").append(escape(cpu.getProcessorIdentifier().getName())).append("\">")
            .append(escape(cpu.getProcessorIdentifier().getName())).append("</div>\n");
        html.append("      <div class=\"sub\">").append(cpu.getPhysicalProcessorCount()).append(" Physical Cores / ")
            .append(cpu.getLogicalProcessorCount()).append(" Threads</div>\n");
        html.append("    </div>\n");

        // Stat 2: RAM
        html.append("    <div class=\"stat-card\">\n");
        html.append("      <div class=\"label\">Total Memory</div>\n");
        html.append("      <div class=\"value\">").append(FormatUtils.formatBytes(totalMem)).append("</div>\n");
        html.append("      <div class=\"sub\">").append(FormatUtils.formatPercentage(memUsageRatio)).append(" Used (")
            .append(FormatUtils.formatBytes(usedMem)).append(")</div>\n");
        html.append("    </div>\n");

        // Stat 3: Primary GPU
        String primaryGpuName = gpus.isEmpty() ? "None Detected" : gpus.get(0).getName();
        String primaryGpuVram = (!gpus.isEmpty() && gpus.get(0).getVRam() > 0)
                ? FormatUtils.formatBytes(gpus.get(0).getVRam()) + " VRAM"
                : (gpus.isEmpty() ? "" : gpus.get(0).getVendor());
        html.append("    <div class=\"stat-card\">\n");
        html.append("      <div class=\"label\">Graphics</div>\n");
        html.append("      <div class=\"value\" title=\"").append(escape(primaryGpuName)).append("\">")
            .append(escape(primaryGpuName)).append("</div>\n");
        html.append("      <div class=\"sub\">").append(escape(primaryGpuVram)).append("</div>\n");
        html.append("    </div>\n");

        // Stat 4: Storage
        html.append("    <div class=\"stat-card\">\n");
        html.append("      <div class=\"label\">Storage Pool</div>\n");
        html.append("      <div class=\"value\">").append(FormatUtils.formatBytes(totalDiskStorage)).append("</div>\n");
        html.append("      <div class=\"sub\">").append(disks.size()).append(" Physical Drive(s)</div>\n");
        html.append("    </div>\n");

        html.append("  </section>\n");

        // Grid Row 1: OS and Motherboard
        html.append("  <div class=\"grid-2\">\n");

        // Panel: OS
        html.append("    <div class=\"panel\">\n");
        html.append("      <div class=\"panel-header\">\n");
        html.append("        <div class=\"panel-icon\">🖥️</div>\n");
        html.append("        <h2>Operating System</h2>\n");
        html.append("      </div>\n");
        html.append("      <table class=\"data-table\">\n");
        appendRow(html, "OS Family", os.getFamily());
        appendRow(html, "Manufacturer", os.getManufacturer());
        appendRow(html, "Version / Build", os.getVersionInfo().toString());
        appendRow(html, "Architecture", os.getBitness() + "-bit");
        appendRow(html, "System Boot Time", DATE_TIME_FORMATTER.format(Instant.ofEpochSecond(os.getSystemBootTime())));
        appendRow(html, "Uptime", formattedUptime);
        appendRow(html, "Process Count", String.valueOf(os.getProcessCount()));
        appendRow(html, "Thread Count", String.valueOf(os.getThreadCount()));
        html.append("      </table>\n");
        html.append("    </div>\n");

        // Panel: Motherboard & System
        Baseboard baseboard = computerSystem.getBaseboard();
        Firmware firmware = computerSystem.getFirmware();
        html.append("    <div class=\"panel\">\n");
        html.append("      <div class=\"panel-header\">\n");
        html.append("        <div class=\"panel-icon\">🖲️</div>\n");
        html.append("        <h2>Motherboard & BIOS</h2>\n");
        html.append("      </div>\n");
        html.append("      <table class=\"data-table\">\n");
        appendRow(html, "System Model", computerSystem.getManufacturer() + " " + computerSystem.getModel());
        appendRow(html, "System Serial", computerSystem.getSerialNumber());
        appendRow(html, "Board Manufacturer", baseboard.getManufacturer());
        appendRow(html, "Board Model", baseboard.getModel());
        appendRow(html, "Board Version", baseboard.getVersion());
        appendRow(html, "Board Serial", baseboard.getSerialNumber());
        appendRow(html, "BIOS Vendor", firmware.getManufacturer());
        appendRow(html, "BIOS Version", firmware.getVersion());
        appendRow(html, "BIOS Release Date", firmware.getReleaseDate());
        html.append("      </table>\n");
        html.append("    </div>\n");

        html.append("  </div>\n");

        // Grid Row 2: CPU and RAM
        html.append("  <div class=\"grid-2\">\n");

        // Panel: CPU
        CentralProcessor.ProcessorIdentifier id = cpu.getProcessorIdentifier();
        html.append("    <div class=\"panel\">\n");
        html.append("      <div class=\"panel-header\">\n");
        html.append("        <div class=\"panel-icon\">⚡</div>\n");
        html.append("        <h2>Processor (CPU)</h2>\n");
        html.append("      </div>\n");
        html.append("      <table class=\"data-table\">\n");
        appendRow(html, "Processor Name", id.getName());
        appendRow(html, "Vendor", id.getVendor());
        appendRow(html, "Microarchitecture", id.getMicroarchitecture());
        appendRow(html, "Physical Packages", cpu.getPhysicalPackageCount() + " Package(s)");
        appendRow(html, "Physical Cores", String.valueOf(cpu.getPhysicalProcessorCount()));
        appendRow(html, "Logical Processors", String.valueOf(cpu.getLogicalProcessorCount()));
        appendRow(html, "64-bit Capable", id.isCpu64bit() ? "Yes" : "No");
        if (cpu.getMaxFreq() > 0) {
            appendRow(html, "Max Frequency", FormatUtils.formatHertz(cpu.getMaxFreq()));
        }
        if (id.getVendorFreq() > 0) {
            appendRow(html, "Vendor Frequency", FormatUtils.formatHertz(id.getVendorFreq()));
        }
        html.append("      </table>\n");
        html.append("    </div>\n");

        // Panel: Memory
        VirtualMemory vm = memory.getVirtualMemory();
        List<PhysicalMemory> pmList = memory.getPhysicalMemory();
        html.append("    <div class=\"panel\">\n");
        html.append("      <div class=\"panel-header\">\n");
        html.append("        <div class=\"panel-icon\">🧠</div>\n");
        html.append("        <h2>Memory (RAM)</h2>\n");
        html.append("      </div>\n");
        html.append("      <div style=\"display:flex; justify-content:space-between; font-size:0.85rem; color:var(--text-muted);\">\n");
        html.append("        <span>Used: ").append(FormatUtils.formatBytes(usedMem)).append("</span>\n");
        html.append("        <span>").append(String.format("%.1f%%", memUsagePercent)).append("</span>\n");
        html.append("        <span>Total: ").append(FormatUtils.formatBytes(totalMem)).append("</span>\n");
        html.append("      </div>\n");
        html.append("      <div class=\"progress-bar-container\">\n");
        html.append("        <div class=\"progress-bar-fill\" style=\"width: ")
            .append(String.format("%.2f", Math.min(100.0, memUsagePercent))).append("%;\"></div>\n");
        html.append("      </div>\n");
        html.append("      <table class=\"data-table\">\n");
        appendRow(html, "Available Memory", FormatUtils.formatBytes(availableMem));
        appendRow(html, "Virtual Memory Max", FormatUtils.formatBytes(vm.getVirtualMax()));
        appendRow(html, "Virtual Memory In Use", FormatUtils.formatBytes(vm.getVirtualInUse()));
        html.append("      </table>\n");

        if (!pmList.isEmpty()) {
            html.append("      <div class=\"ram-sticks\">\n");
            html.append("        <h3>Installed Physical DIMM Sticks (").append(pmList.size()).append(")</h3>\n");
            int slot = 1;
            for (PhysicalMemory pm : pmList) {
                html.append("        <div class=\"item-card\">\n");
                html.append("          <div class=\"item-card-title\">Slot #").append(slot++).append(" - ")
                    .append(escape(pm.getBankLabel())).append("</div>\n");
                html.append("          <table class=\"data-table\" style=\"font-size:0.85rem;\">\n");
                appendRow(html, "Manufacturer", pm.getManufacturer());
                appendRow(html, "Type & Capacity", pm.getMemoryType() + " - " + FormatUtils.formatBytes(pm.getCapacity()));
                appendRow(html, "Speed", FormatUtils.formatHertz(pm.getClockSpeed()));
                html.append("          </table>\n");
                html.append("        </div>\n");
            }
            html.append("      </div>\n");
        }

        html.append("    </div>\n");
        html.append("  </div>\n");

        // Section: Graphics
        html.append("  <div class=\"panel\" style=\"margin-bottom: 24px;\">\n");
        html.append("    <div class=\"panel-header\">\n");
        html.append("      <div class=\"panel-icon\">🎮</div>\n");
        html.append("      <h2>Graphics Adapters (GPU)</h2>\n");
        html.append("    </div>\n");

        if (gpus.isEmpty()) {
            html.append("    <p style=\"color: var(--text-muted); font-size: 0.9rem;\">No dedicated graphics cards detected.</p>\n");
        } else {
            int gpuIndex = 1;
            for (GraphicsCard gpu : gpus) {
                html.append("    <div class=\"item-card\" style=\"padding: 16px;\">\n");
                html.append("      <div class=\"item-card-title\" style=\"font-size: 1rem;\">[GPU #").append(gpuIndex++)
                    .append("] ").append(escape(gpu.getName())).append("</div>\n");
                html.append("      <table class=\"data-table\">\n");
                appendRow(html, "Vendor", gpu.getVendor());
                appendRow(html, "Driver Version", gpu.getVersionInfo());
                if (gpu.getVRam() > 0) {
                    appendRow(html, "Dedicated VRAM", FormatUtils.formatBytes(gpu.getVRam()));
                }
                appendRow(html, "Device ID", gpu.getDeviceId());
                html.append("      </table>\n");
                html.append("    </div>\n");
            }
        }
        html.append("  </div>\n");

        // Section: Storage
        html.append("  <div class=\"panel\" style=\"margin-bottom: 24px;\">\n");
        html.append("    <div class=\"panel-header\">\n");
        html.append("      <div class=\"panel-icon\">💾</div>\n");
        html.append("      <h2>Storage Drives & I/O Telemetry</h2>\n");
        html.append("    </div>\n");

        if (disks.isEmpty()) {
            html.append("    <p style=\"color: var(--text-muted); font-size: 0.9rem;\">No physical disk drives detected.</p>\n");
        } else {
            html.append("    <table class=\"data-table\">\n");
            html.append("      <thead>\n");
            html.append("        <tr>\n");
            html.append("          <th>Drive</th>\n");
            html.append("          <th>Serial Number</th>\n");
            html.append("          <th>Capacity</th>\n");
            html.append("          <th>Reads</th>\n");
            html.append("          <th>Writes</th>\n");
            html.append("        </tr>\n");
            html.append("      </thead>\n");
            html.append("      <tbody>\n");
            int driveIdx = 1;
            for (HWDiskStore disk : disks) {
                html.append("        <tr>\n");
                html.append("          <td><strong>#").append(driveIdx++).append("</strong> ")
                    .append(escape(disk.getModel())).append("</td>\n");
                html.append("          <td style=\"font-family: monospace; font-size:0.85rem;\">")
                    .append(escape(disk.getSerial())).append("</td>\n");
                html.append("          <td><strong>").append(FormatUtils.formatBytes(disk.getSize())).append("</strong></td>\n");
                html.append("          <td>").append(disk.getReads()).append("<br><span style=\"color:var(--text-muted); font-size:0.8rem;\">(")
                    .append(FormatUtils.formatBytes(disk.getReadBytes())).append(")</span></td>\n");
                html.append("          <td>").append(disk.getWrites()).append("<br><span style=\"color:var(--text-muted); font-size:0.8rem;\">(")
                    .append(FormatUtils.formatBytes(disk.getWriteBytes())).append(")</span></td>\n");
                html.append("        </tr>\n");
            }
            html.append("      </tbody>\n");
            html.append("    </table>\n");
        }
        html.append("  </div>\n");

        // Footer
        html.append("  <footer>\n");
        html.append("    <div>Completed in ").append(elapsedTimeMs).append(" ms using OSHI System Information.</div>\n");
        html.append("    <div>PC Hardware Scanner &copy; ").append(Instant.now().atZone(ZoneId.systemDefault()).getYear()).append("</div>\n");
        html.append("  </footer>\n");

        html.append("</div>\n</body>\n</html>\n");

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
            writer.write(html.toString());
        }
    }

    private static void appendRow(StringBuilder sb, String label, String value) {
        sb.append("        <tr>\n");
        sb.append("          <td class=\"label-cell\">").append(escape(label)).append("</td>\n");
        sb.append("          <td class=\"value-cell\">").append(escape(value)).append("</td>\n");
        sb.append("        </tr>\n");
    }

    private static String escape(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "N/A";
        }
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;");
    }
}
