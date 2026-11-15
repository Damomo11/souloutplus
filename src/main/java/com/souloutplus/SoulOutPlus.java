package com.souloutplus;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoulOutPlus implements ClientModInitializer {
    public static final String MOD_ID = "souloutplus";
    public static final String MOD_NAME = "Soul Out Plus";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onInitializeClient() {
        InitHandler.init();
    }
}
