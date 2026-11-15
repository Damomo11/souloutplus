package com.souloutplus;

import fi.dy.masa.malilib.event.InitializationHandler;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoulOutPlus implements ClientModInitializer {
    public static final String MOD_ID = "souloutplus";
    public static final String MOD_NAME = "Soul Out Plus";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onInitializeClient() {
        // 对齐 printer-autyism 的时序：初始化注册走 malilib 的 InitializationHandler
        // （entrypoint 阶段太早，Configs.init 里的 keybind 注册可能赶不上 onGameInitDone 的 updateUsedKeys）
        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
    }
}
