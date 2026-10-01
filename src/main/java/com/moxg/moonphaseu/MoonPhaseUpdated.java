package com.moxg.moonphaseu;

import com.moxg.moonphaseu.config.ModConfig;
import com.moxg.moonphaseu.events.KeyInputHandler;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MoonPhaseUpdated implements ModInitializer {
	public static final String MOD_ID = "moonphaseu";
	public static ModConfig config;
	PhaseIcon icon;

	@Override
	public void onInitialize() {
		config = new ModConfig();
		icon = new PhaseIcon();
		try {
			config.load();
		} catch (IOException ie) {
			return;
		}
		HudRenderCallback.EVENT.register(((drawContext, tickDelta) -> {
			icon.drawPhaseIcon(drawContext, config.hudPosition);
		}));
		KeyInputHandler.register();
	}
}