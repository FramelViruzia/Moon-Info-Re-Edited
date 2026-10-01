package com.moxg.moonphaseu;

import com.mojang.blaze3d.systems.RenderSystem;
import com.moxg.moonphaseu.config.IndicatorMode;
import com.moxg.moonphaseu.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class PhaseIcon {
    private static final Identifier MOONPHASE_OVERWORLD = Identifier.of(MoonPhaseUpdated.MOD_ID, "textures/moonphase_overworld.png");
    private static final Identifier MOONPHASE_NETHER = Identifier.of(MoonPhaseUpdated.MOD_ID, "textures/moonphase_nether.png");
    private static final Identifier MOONPHASE_END = Identifier.of(MoonPhaseUpdated.MOD_ID, "textures/moonphase_end.png");

    private static final Formatting[] FULLNESS_COLOR = new Formatting[]
            {Formatting.RED, Formatting.GOLD, Formatting.YELLOW, Formatting.GREEN, Formatting.DARK_GREEN};

    // Names indexed by Minecraft's internal phase (mc.world.getMoonPhase()):
    // 0 = full moon ... 4 = new moon (the icon textures use this same order)
    private static final String[] PHASE_NAMES = {
            "Full Moon", "Waning Gibbous", "Last Quarter", "Waning Crescent",
            "New Moon", "Waxing Crescent", "First Quarter", "Waxing Gibbous"
    };

    private static final int ICON_SIZE = 24;
    private static final int GAP = 2;
    private static final int MARGIN_LEFT = 5;
    private static final int MARGIN_RIGHT = 4;

    // Number shown to the player: 0 = New Moon, 1 = Waxing Crescent, 2 = First Quarter,
    // 3 = Waxing Gibbous, 4 = Full Moon, 5 = Waning Gibbous, 6 = Last Quarter, 7 = Waning Crescent
    static int displayNumber(int mcPhase) {
        return (mcPhase + 4) % 8;
    }

    void drawPhaseIcon(DrawContext context, int hudPosition) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ModConfig cfg = MoonPhaseUpdated.config;
        if (!cfg.hudVisible || mc.options.hudHidden || mc.getDebugHud().shouldShowDebugHud() || mc.world == null) {
            return;
        }
        if (hudPosition == 0) {
            hudPosition = 4;
        }
        int windowWidth = mc.getWindow().getScaledWidth();
        int windowHeight = mc.getWindow().getScaledHeight();
        int fontHeight = mc.textRenderer.fontHeight;

        int phase = mc.world.getMoonPhase();

        // ---- what is shown ----
        boolean showIcon = cfg.showIcon;
        boolean showPercent = cfg.showPercentage;
        boolean showTime = cfg.showTime;
        boolean hasText = showPercent || showTime;

        String indicator = null;
        switch (cfg.indicatorMode) {
            case NAME -> indicator = PHASE_NAMES[phase];
            case NUMBER -> indicator = "Phase " + displayNumber(phase);
            case BOTH -> indicator = displayNumber(phase) + ". " + PHASE_NAMES[phase];
            case HIDE -> indicator = null;
        }

        if (!showIcon && !hasText && indicator == null) {
            return;
        }

        // ---- layout ----
        boolean atTop = hudPosition == 1 || hudPosition == 3;
        boolean atLeft = hudPosition <= 2;

        // fixed width for the text column so the icon doesn't jitter when the numbers change
        int textColumnWidth = Math.max(mc.textRenderer.getWidth("100%↑"), mc.textRenderer.getWidth("20min"));

        int rowWidth = (showIcon ? ICON_SIZE : 0)
                + (showIcon && hasText ? GAP : 0)
                + (hasText ? textColumnWidth : 0);

        int rowX = atLeft ? MARGIN_LEFT : windowWidth - MARGIN_RIGHT - rowWidth;
        int rowY = atTop ? 2 : windowHeight - ICON_SIZE - 2;

        int iconX = rowX;
        int textX = rowX + (showIcon ? ICON_SIZE + GAP : 0);

        // ---- icon ----
        if (showIcon) {
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            Identifier textureId = MOONPHASE_OVERWORLD;
            if (mc.world.getRegistryKey() == World.OVERWORLD) {
                textureId = MOONPHASE_OVERWORLD;
            } else if (mc.world.getRegistryKey() == World.END) {
                textureId = MOONPHASE_END;
            } else if (mc.world.getRegistryKey() == World.NETHER) {
                textureId = MOONPHASE_NETHER;
            }
            context.drawTexture(textureId, iconX, rowY, phase * ICON_SIZE, 0, ICON_SIZE, ICON_SIZE, 192, 24);
        }

        // ---- percentage + time ----
        if (hasText) {
            int lines = (showPercent ? 1 : 0) + (showTime ? 1 : 0);
            int textY = rowY + (ICON_SIZE - lines * fontHeight) / 2;

            if (showPercent) {
                int sizePercent = (int) (mc.world.getMoonSize() * 100f);
                context.drawTextWithShadow(mc.textRenderer, String.format("%s%d%%%s",
                                FULLNESS_COLOR[Math.min(sizePercent / 25, FULLNESS_COLOR.length - 1)],
                                sizePercent,
                                phase <= 3 ? Formatting.RED + "↓" : Formatting.DARK_GREEN + "↑"),
                        textX, textY, 0xffffff);
                textY += fontHeight;
            }
            if (showTime) {
                long daySecondsLeft = (24000 - mc.world.getTimeOfDay() % 24000) / 20;
                long dayMinutesLeft = daySecondsLeft / 60;
                boolean atLeast60s = daySecondsLeft >= 60;
                context.drawTextWithShadow(mc.textRenderer, atLeast60s ? dayMinutesLeft + "min" : daySecondsLeft + "s",
                        textX, textY, Formatting.DARK_AQUA.getColorValue());
            }
        }

        // ---- phase indicator (name / number): above the row, or below it at the top of the screen ----
        if (indicator != null) {
            int indicatorWidth = mc.textRenderer.getWidth(indicator);
            int indicatorX = Math.max(2, Math.min(rowX, windowWidth - indicatorWidth - MARGIN_RIGHT));
            int indicatorY;
            if (rowWidth == 0) {
                // nothing else is shown: put the text in the corner by itself
                indicatorY = atTop ? 4 : windowHeight - fontHeight - 4;
            } else {
                indicatorY = atTop ? rowY + ICON_SIZE + GAP : rowY - fontHeight - 1;
            }
            context.drawTextWithShadow(mc.textRenderer, indicator, indicatorX, indicatorY, 0xFFFFFF);
        }
    }
}
