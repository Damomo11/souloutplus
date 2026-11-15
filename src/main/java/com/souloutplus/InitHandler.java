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

/**
 * malilib 初始化钩子（对齐 printer-autyism 的正确时序）。
 *
 * 注册成 IInitializationHandler 之后，registerModHandlers() 由 malilib 在
 * InitializationHandler.onGameInitDone 时机回调 —— 那之后 malilib 自己会调
 * updateUsedKeys() 把 provider 的 keybind 重建进 hotkeyMap。
 * 直接在 fabric entrypoint 里 init 的时序不可靠（entrypoint 跑在 Minecraft 构造前，
 * 而 onGameInitDone 只在构造完成时调一次），keybind 可能永远进不了 hotkeyMap → 按键无反应。
 */
public class InitHandler implements IInitializationHandler {
    private static boolean ticked = false;

    @Override
    public void registerModHandlers() {
        Configs.init();
        // 每帧的世界上层渲染（可达 HUD 数据计算 + 幽灵预览）
        RenderEventHandler.getInstance().registerWorldLastRenderer(new ReachHudRenderer());
        // 每 client tick：单人属性调距 + 世界卸载强制回魂 + 兜底 updateUsedKeys
        TickHandler.getInstance().registerClientTickHandler(new IClientTickHandler() {
            @Override
            public void onClientTick(Minecraft mc) {
                // 兜底：malilib 的 onGameInitDone → updateUsedKeys 只调一次；
                // 若我们的注册晚于它（entrypoint 顺序问题），keybind 不在 hotkeyMap 里 → 按键无反应。
                // 首次 tick 再重建一次，保证进世界后热键一定可用。
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
