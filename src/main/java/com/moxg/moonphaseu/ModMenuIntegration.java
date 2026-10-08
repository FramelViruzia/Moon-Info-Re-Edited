package com.moxg.moonphaseu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // The config screen needs Cloth Config. Without it, don't touch any Cloth classes.
        if (!FabricLoader.getInstance().isModLoaded("cloth-config")) {
            return ModMenuApi.super.getModConfigScreenFactory();
        }
        return MoonPhaseConfigScreen::create;
    }
}
