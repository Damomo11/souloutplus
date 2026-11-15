package com.souloutplus.mixin;

import com.souloutplus.camera.CameraState;
import com.souloutplus.render.HudCache;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 出窍状态 HUD：左上角徽章 + 准星旁可达性三色点。
 * 颜色由 ReachHudRenderer 在 world pass 算好放进 HudCache（GUI pass 只管画）。
 */
@Mixin(Gui.class)
public abstract class MixinGui {

    @Inject(method = "render", at = @At("TAIL"))
    private void souloutplus$renderHud(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!CameraState.isSoulOut()) return;
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        // 出窍徽章（左上角）
        guiGraphics.drawString(font, HudCache.SOUL_BADGE, 8, 8, 0xFFFFFF);

        // 可达性三色点（准星右下）
        var color = HudCache.lastReachColor;
        if (color != null) {
            int x = mc.getWindow().getGuiScaledWidth() / 2 + 8;
            int y = mc.getWindow().getGuiScaledHeight() / 2 + 8;
            int argb = switch (color) {
                case GREEN -> 0xFF00CC44;
                case YELLOW -> 0xFFDDCC00;
                case RED -> 0xFFCC2200;
            };
            guiGraphics.fill(x, y, x + 4, y + 4, argb);
        }
    }
}
