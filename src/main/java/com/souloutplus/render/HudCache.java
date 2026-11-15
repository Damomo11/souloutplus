package com.souloutplus.render;

import com.souloutplus.interaction.InteractionGuard;
import net.minecraft.network.chat.Component;

/**
 * HUD 缓存的常量 Component + 帧间共享状态（避免每帧 I18n 查找）。
 */
public final class HudCache {
    public static final Component SOUL_BADGE =
            Component.translatable("souloutplus.hud.soul_badge");

    /** 可达性三色点：ReachHudRenderer(world pass) 写，MixinGui(GUI pass) 读 */
    public static InteractionGuard.ReachColor lastReachColor;

    private HudCache() {
    }
}
