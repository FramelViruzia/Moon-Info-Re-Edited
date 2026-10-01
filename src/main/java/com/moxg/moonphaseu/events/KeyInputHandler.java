package com.moxg.moonphaseu.events;

import com.moxg.moonphaseu.MoonPhaseUpdated;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

public class KeyInputHandler {
    public static final String KEY_CATEGORY_MOONPHASE = "Moon Phase Info";
    public static final String KEY_CHANGE_POSITION = "Change HUD position";
    public static final String KEY_TOGGLE_HUD = "Toggle HUD";

    public static KeyBinding changePositionKey;
    public static KeyBinding toggleHudKey;

    public static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleHudKey.wasPressed()) {
                try {
                    MoonPhaseUpdated.config.saveVisible(!MoonPhaseUpdated.config.hudVisible);
                } catch (IOException ei) {
                    // keep the in-memory state even if saving failed
                    MoonPhaseUpdated.config.hudVisible = !MoonPhaseUpdated.config.hudVisible;
                }
            }
            if(changePositionKey.wasPressed()) {
                try {
                    MoonPhaseUpdated.config.load();
                    int currentPosition = MoonPhaseUpdated.config.hudPosition;
                    if (currentPosition + 1 > 4) { currentPosition = 1; }
                    else { currentPosition++; }
                    MoonPhaseUpdated.config.save(currentPosition);
                } catch (IOException ei) {
                    return;
                }
            }
        });
    }

    public static void register() {
        changePositionKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_CHANGE_POSITION,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F6,
                KEY_CATEGORY_MOONPHASE
        ));

        toggleHudKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_TOGGLE_HUD,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                KEY_CATEGORY_MOONPHASE
        ));

        registerKeyInputs();
    }
}