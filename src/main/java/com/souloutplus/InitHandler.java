package com.souloutplus;

import com.souloutplus.camera.CameraState;
import com.souloutplus.config.Configs;
import com.souloutplus.render.ReachHudRenderer;

import fi.dy.masa.malilib.event.RenderEventHandler;

public class InitHandler {
    public static void init() {
        Configs.init();
        CameraState.init();
        RenderEventHandler.getInstance().registerWorldLastRenderer(new ReachHudRenderer());
    }
}
