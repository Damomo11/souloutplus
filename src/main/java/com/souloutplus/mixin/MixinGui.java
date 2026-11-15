package com.souloutplus.mixin;

import com.souloutplus.camera.CameraState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 出窍状态指示（HUD 层，后续可达性三色点也挂这里）。
 */
@Mixin(Gui.class)
public abstract class MixinGui {

    @Inject(method = "render", at = @At("TAIL"))
    private void souloutplus$renderHud(net.minecraft.client.gui.GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        if (!CameraState.isSoulOut()) return;
        var font = Minecraft.getInstance().font;

        // 出窍徽标（左上角）
        guiGraphics.drawString(font, com.souloutplus.render.HudCache.SOUL_BADGE, 8, 8, 0xFFFFFF);

        // 可达性三色点（准星右下）：ReachHudRenderer 在 world pass 算好颜色
        var color = com.souloutplus.render.HudCache.lastReachColor;
        if (color != null) {
            int x = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 + 8;
            int y = Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2 + 8;
            int argb = switch (color) {
                case GREEN -> 0xFF00CC44;
                case YELLOW -> 0xFFDDCC00;
                case RED -> 0xFFCC2200;
            };
            guiGraphics.fill(x, y, x + 4, y + 4, argb);
        }
    }
}
