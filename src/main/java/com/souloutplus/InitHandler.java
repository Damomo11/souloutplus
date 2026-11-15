package com.souloutplus;

import com.souloutplus.camera.CameraState;
import com.souloutplus.config.Configs;
import com.souloutplus.interaction.ReachHelper;
import com.souloutplus.render.ReachHudRenderer;

import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.event.TickEventHandler;

public class InitHandler {
    public static void init() {
        Configs.init();
        // 每帧的世界上层渲染（可达 HUD + 后续幽灵预览）
        RenderEventHandler.getInstance().registerWorldLastRenderer(new ReachHudRenderer());
        // 每 client tick：单人属性调距 + 世界卸载强制回魂（malilib 的 client tick 钩子，不依赖 fabric-api 事件）
        TickEventHandler.getInstance().registerClientTickHandler((mc) -> {
            ReachHelper.tick();
            if (mc.player == null || mc.level == null) {
                CameraState.forceRemove();
            }
        });
    }
}
