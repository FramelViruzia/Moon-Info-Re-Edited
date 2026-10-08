package com.moxg.moonphaseu.config;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ModConfig {

    // Defaults (also used by the Reset buttons in the config screen)
    public static final int DEFAULT_POSITION = 4;
    public static final boolean DEFAULT_HUD_VISIBLE = true;
    public static final IndicatorMode DEFAULT_INDICATOR = IndicatorMode.BOTH;
    public static final boolean DEFAULT_SHOW_PERCENTAGE = true;
    public static final boolean DEFAULT_SHOW_TIME = true;
    public static final boolean DEFAULT_SHOW_ICON = true;

    // <game folder>/config/Moon-Info-Re-Edited.properties
    private final Path path;
    public int hudPosition = DEFAULT_POSITION;
    public boolean hudVisible = DEFAULT_HUD_VISIBLE;
    public IndicatorMode indicatorMode = DEFAULT_INDICATOR;
    public boolean showPercentage = DEFAULT_SHOW_PERCENTAGE;
    public boolean showTime = DEFAULT_SHOW_TIME;
    public boolean showIcon = DEFAULT_SHOW_ICON;

    public ModConfig() {
        this.path = FabricLoader.getInstance().getConfigDir().resolve("Moon-Info-Re-Edited.properties");
    }

    // Writes every setting to disk.
    public void save() throws IOException {
        Properties props = new Properties();
        props.setProperty("hud_position", String.valueOf(hudPosition));
        props.setProperty("hud_visible", String.valueOf(hudVisible));
        props.setProperty("indicator_mode", indicatorMode.name());
        props.setProperty("show_percentage", String.valueOf(showPercentage));
        props.setProperty("show_time", String.valueOf(showTime));
        props.setProperty("show_icon", String.valueOf(showIcon));
        Files.createDirectories(path.getParent());
        try (FileWriter writer = new FileWriter(path.toFile())) {
            props.store(writer, null);
        }
    }

    // Reads the config file. Old files that only contain "hud_position=N" still work;
    // missing keys fall back to their defaults.
    public void load() throws IOException {
        Files.createDirectories(path.getParent());
        File file = path.toFile();
        if (file.createNewFile()) {
            save();
            return;
        }
        Properties props = new Properties();
        try (FileReader reader = new FileReader(file)) {
            props.load(reader);
        }
        try {
            hudPosition = Integer.parseInt(props.getProperty("hud_position", String.valueOf(DEFAULT_POSITION)).trim());
        } catch (NumberFormatException e) {
            hudPosition = DEFAULT_POSITION;
        }
        hudVisible = parseBool(props, "hud_visible", DEFAULT_HUD_VISIBLE);
        indicatorMode = IndicatorMode.fromString(props.getProperty("indicator_mode"), DEFAULT_INDICATOR);
        showPercentage = parseBool(props, "show_percentage", DEFAULT_SHOW_PERCENTAGE);
        showTime = parseBool(props, "show_time", DEFAULT_SHOW_TIME);
        showIcon = parseBool(props, "show_icon", DEFAULT_SHOW_ICON);
    }

    private static boolean parseBool(Properties props, String key, boolean fallback) {
        String v = props.getProperty(key);
        return v == null ? fallback : Boolean.parseBoolean(v.trim());
    }

    public void save(int position) throws IOException {
        hudPosition = position;
        save();
    }

    public void saveVisible(boolean visible) throws IOException {
        hudVisible = visible;
        save();
    }
}
