package com.souloutplus.config;

import com.souloutplus.SoulOutPlus;
import com.souloutplus.camera.CameraState;
import net.minecraft.client.Minecraft;
public class HotkeysCallback {
    private static final Minecraft client = Minecraft.getInstance();

    public static void initCallbacks() {
        Configs.Hotkeys.TOGGLE_SOUL.getKeybind().setCallback((action, keybind) -> {
            SoulOutPlus.LOGGER.info("hotkey: toggleSoulOut pressed (player={}, level={})",
                    client.player != null, client.level != null);
            if (client.player != null && client.level != null) {
                try {
                    CameraState.toggle();
                } catch (Throwable t) {
                    SoulOutPlus.LOGGER.error("toggle soul out failed", t);
                }
            }
            return true;
        });

        Configs.Hotkeys.OPEN_CONFIG.getKeybind().setCallback((action, keybind) -> {
            SoulOutPlus.LOGGER.info("hotkey: openConfigGui pressed");
            if (client.player != null && client.level != null) {
                client.gui.setScreen(new ConfigUi());
            }
            return true;
        });

        Configs.Hotkeys.RETURN_TO_BODY.getKeybind().setCallback((action, keybind) -> {
            if (client.player != null && CameraState.isSoulOut()) {
                CameraState.teleportToBody();
            }
            return true;
        });
    }
}