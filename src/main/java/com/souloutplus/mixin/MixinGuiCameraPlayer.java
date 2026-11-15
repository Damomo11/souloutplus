package com.souloutplus.mixin;

import com.souloutplus.camera.CameraState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 出窍时 HUD（物品栏/血条/经验条）用**真本体**渲染（从 Tweakeroo MixinInGameHud 移植）。
 *
 * 原版 Gui.getCameraPlayer() = getCameraEntity() as Player —— 出窍时拿到的是我们的 CameraEntity：
 *  - 它的 inventory 是构造时的全新空背包 → 物品栏渲染出来全是空气格（看起来"没有物品栏"）
 *  - 它的 health/food/xp 全是初始值 → 血条/饥饿/经验条全部归零
 * 修复：出窍时重定向回 mc.player，HUD 显示本体的真实状态。
 */
@Mixin(Gui.class)
public abstract class MixinGuiCameraPlayer {

    @Inject(method = "getCameraPlayer", at = @At("HEAD"), cancellable = true, require = 0)
    private void souloutplus$useBodyPlayerForHud(CallbackInfoReturnable<Player> cir) {
        if (CameraState.isSoulOut() && Minecraft.getInstance().player != null) {
            cir.setReturnValue(Minecraft.getInstance().player);
        }
    }
}
