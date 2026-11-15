package com.souloutplus;

import com.souloutplus.camera.CameraState;
import com.souloutplus.config.Configs;
import com.souloutplus.interaction.ReachHelper;
import com.souloutplus.render.ReachHudRenderer;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.event.TickHandler;
import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import net.minecraft.client.Minecraft;

public class InitHandler {
    public static void init() {
        Configs.init();
        // 每帧的世界上层渲染（可达 HUD 数据计算 + 后续幽灵预览）
        RenderEventHandler.getInstance().registerWorldLastRenderer(new ReachHudRenderer());
        // 每 client tick：单人属性调距 + 世界卸载强制回魂
        TickHandler.getInstance().registerClientTickHandler(new IClientTickHandler() {
            @Override
            public void onClientTick(Minecraft mc) {
                ReachHelper.tick();
                if (mc.player == null || mc.level == null) {
                    CameraState.forceRemove();
                }
            }
        });
    }
}
