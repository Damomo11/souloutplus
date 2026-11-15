package com.souloutplus.render;

import com.souloutplus.interaction.InteractionGuard;
import net.minecraft.network.chat.Component;
public final class HudCache {
    public static final Component SOUL_BADGE =
            Component.translatable("souloutplus.hud.soul_badge");
    public static InteractionGuard.ReachColor lastReachColor;

    private HudCache() {
    }
}