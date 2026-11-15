package com.souloutplus.config;

import com.souloutplus.camera.CameraState;
import net.minecraft.client.Minecraft;

/**
 * 热键 → 行为 的回调注册。
 */
public class HotkeysCallback {
    private static final Minecraft client = Minecraft.getInstance();

    public static void initCallbacks() {
        // 出窍开关
        Configs.Hotkeys.TOGGLE_SOUL.getKeybind().setCallback((action, keybind) -> {
            if (client.player != null && client.level != null) {
                CameraState.toggle();
            }
            return true;
        });

        // 打开设置界面
        Configs.Hotkeys.OPEN_CONFIG.getKeybind().setCallback((action, keybind) -> {
            if (client.player != null && client.level != null) {
                client.setScreen(new ConfigUi());
            }
            return true;
        });

        // 相机瞬移回本体头顶
        Configs.Hotkeys.RETURN_TO_BODY.getKeybind().setCallback((action, keybind) -> {
            if (client.player != null && CameraState.isSoulOut()) {
                CameraState.teleportToBody();
            }
            return true;
        });
    }
}
