package com.moxg.moonphaseu.config;

import com.moxg.moonphaseu.MoonPhaseUpdated;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class ModConfig {

    String filename;
    public int hudPosition = 4;
    public boolean hudVisible = true;

    public ModConfig() {
        this.filename = MoonPhaseUpdated.MOD_ID + "_config.properties";
    }

    private void write() throws IOException {
        Properties props = new Properties();
        props.setProperty("hud_position", String.valueOf(hudPosition));
        props.setProperty("hud_visible", String.valueOf(hudVisible));
        try (FileWriter writer = new FileWriter(this.filename)) {
            props.store(writer, null);
        }
    }

    // Reads the config file. Old files that only contain "hud_position=N" still work.
    public void load() throws IOException {
        File file = new File(this.filename);
        if (file.createNewFile()) {
            hudPosition = 4;
            hudVisible = true;
            write();
            return;
        }
        Properties props = new Properties();
        try (FileReader reader = new FileReader(file)) {
            props.load(reader);
        }
        try {
            hudPosition = Integer.parseInt(props.getProperty("hud_position", "4").trim());
        } catch (NumberFormatException e) {
            hudPosition = 4;
        }
        hudVisible = Boolean.parseBoolean(props.getProperty("hud_visible", "true").trim());
    }

    public void save(int position) throws IOException {
        hudPosition = position;
        write();
    }

    public void saveVisible(boolean visible) throws IOException {
        hudVisible = visible;
        write();
    }
}
