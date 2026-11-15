package com.souloutplus;

import com.souloutplus.camera.CameraState;
import com.souloutplus.config.Configs;
import com.souloutplus.interaction.ReachHelper;
import com.souloutplus.render.ReachHudRenderer;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.event.TickHandler;
import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import net.minecraft.client.Minecraft;
public class InitHandler implements IInitializationHandler {
    private static boolean ticked = false;

    @Override
    public void registerModHandlers() {
        Configs.init();
        RenderEventHandler.getInstance().registerWorldLastRenderer(new ReachHudRenderer());
        TickHandler.getInstance().registerClientTickHandler(new IClientTickHandler() {
            @Override
            public void onClientTick(Minecraft mc) {
                if (!ticked) {
                    ticked = true;
                    InputEventHandler.getKeybindManager().updateUsedKeys();
                }
                ReachHelper.tick();
                if (mc.player == null || mc.level == null) {
                    CameraState.forceRemove();
                }
            }
        });
    }
}