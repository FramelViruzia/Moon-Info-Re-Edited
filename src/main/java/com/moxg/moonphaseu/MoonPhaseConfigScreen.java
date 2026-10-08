package com.moxg.moonphaseu;

import com.moxg.moonphaseu.config.IndicatorMode;
import com.moxg.moonphaseu.config.ModConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.io.IOException;

/**
 * Cloth Config screen opened from Mod Menu.
 * Only load this class when Cloth Config is installed (see ModMenuIntegration).
 */
public class MoonPhaseConfigScreen {

    public static Screen create(Screen parent) {
        ModConfig cfg = MoonPhaseUpdated.config;

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("Moon-Info-Re-Edited"))
                .setSavingRunnable(() -> {
                    try {
                        cfg.save();
                    } catch (IOException e) {
                        MoonPhaseUpdated.LOGGER.error("Could not save Moon Phase config", e);
                    }
                });

        ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
        ConfigEntryBuilder eb = builder.entryBuilder();

        // 1. Moon Info HUD (same value as the "Toggle HUD" key bind)
        general.addEntry(eb.startBooleanToggle(Text.literal("Moon Info HUD"), cfg.hudVisible)
                .setDefaultValue(ModConfig.DEFAULT_HUD_VISIBLE)
                .setYesNoTextSupplier(v -> Text.literal(v ? "Enable" : "Disable"))
                .setSaveConsumer(v -> cfg.hudVisible = v)
                .build());

        // 2. Moon Phase Indicator
        general.addEntry(eb.startEnumSelector(Text.literal("Moon Phase Indicator"), IndicatorMode.class, cfg.indicatorMode)
                .setDefaultValue(ModConfig.DEFAULT_INDICATOR)
                .setEnumNameProvider(e -> Text.literal(((IndicatorMode) e).getLabel()))
                .setSaveConsumer(v -> cfg.indicatorMode = v)
                .build());

        // 3. Percentage
        general.addEntry(eb.startBooleanToggle(Text.literal("Percentage"), cfg.showPercentage)
                .setDefaultValue(ModConfig.DEFAULT_SHOW_PERCENTAGE)
                .setYesNoTextSupplier(v -> Text.literal(v ? "Show" : "Hide"))
                .setSaveConsumer(v -> cfg.showPercentage = v)
                .build());

        // 4. Time Until Next Phase
        general.addEntry(eb.startBooleanToggle(Text.literal("Time Until Next Phase"), cfg.showTime)
                .setDefaultValue(ModConfig.DEFAULT_SHOW_TIME)
                .setYesNoTextSupplier(v -> Text.literal(v ? "Show" : "Hide"))
                .setSaveConsumer(v -> cfg.showTime = v)
                .build());

        // 5. Moon Icon
        general.addEntry(eb.startBooleanToggle(Text.literal("Moon Icon"), cfg.showIcon)
                .setDefaultValue(ModConfig.DEFAULT_SHOW_ICON)
                .setYesNoTextSupplier(v -> Text.literal(v ? "Show" : "Hide"))
                .setSaveConsumer(v -> cfg.showIcon = v)
                .build());

        return builder.build();
    }
}
