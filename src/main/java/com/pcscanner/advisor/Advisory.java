package com.pcscanner.advisor;

public class Advisory {

    public enum Severity {
        CRITICAL("CRITICAL ALERT", "#ef4444", "badge-critical"),
        WARNING("WARNING", "#f59e0b", "badge-warning"),
        OPTIMIZATION("OPTIMIZATION", "#38bdf8", "badge-optimization"),
        EXPANSION("EXPANSION", "#34d399", "badge-expansion");

        private final String label;
        private final String color;
        private final String cssClass;

        Severity(String label, String color, String cssClass) {
            this.label = label;
            this.color = color;
            this.cssClass = cssClass;
        }

        public String getLabel() {
            return label;
        }

        public String getColor() {
            return color;
        }

        public String getCssClass() {
            return cssClass;
        }
    }

    public enum Category {
        CPU("Processor & Microcode"),
        MOTHERBOARD("Motherboard & Firmware"),
        MEMORY("Memory (RAM)"),
        STORAGE("Storage & Expansion"),
        SYSTEM("Operating System");

        private final String label;

        Category(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private final Severity severity;
    private final Category category;
    private final String title;
    private final String description;
    private final String action;

    public Advisory(Severity severity, Category category, String title, String description, String action) {
        this.severity = severity;
        this.category = category;
        this.title = title;
        this.description = description;
        this.action = action;
    }

    public Severity getSeverity() {
        return severity;
    }

    public Category getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAction() {
        return action;
    }
}
