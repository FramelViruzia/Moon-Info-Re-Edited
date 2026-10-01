package com.moxg.moonphaseu.config;

public enum IndicatorMode {
    NAME("Name"),
    NUMBER("Number"),
    BOTH("Both"),
    HIDE("Hide");

    private final String label;

    IndicatorMode(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static IndicatorMode fromString(String value, IndicatorMode fallback) {
        if (value == null) return fallback;
        for (IndicatorMode mode : values()) {
            if (mode.name().equalsIgnoreCase(value.trim())) return mode;
        }
        return fallback;
    }
}
