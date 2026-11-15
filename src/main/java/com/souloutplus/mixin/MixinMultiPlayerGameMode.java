package com.souloutplus.mixin;

import com.souloutplus.camera.CameraState;
import com.souloutplus.interaction.InteractionGuard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 点击守卫 + OFF 模式屏蔽（对应 Tweakeroo MixinClientPlayerInteractionManager 的"屏蔽"职责，
 * 但只在 OFF 模式 / 超范围时才拦，SOUL/BODY 全放行）。
 */
@Mixin(MultiPlayerGameMode.class)
public abstract class MixinMultiPlayerGameMode {
    @Shadow
    @Final
    Minecraft minecraft;

    /** 左键攻击方块（开始挖掘） */
    @Inject(method = "startDestroyBlock", at = @At("HEAD"), cancellable = true)
    private void souloutplus$guardStartDestroy(CallbackInfoReturnable<Boolean> cir) {
        if (CameraState.isSoulOut() && InteractionGuard.shouldBlockClicks()) {
            cir.setReturnValue(false);
        }
    }

    /** 右键放置 / 交互 */
    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void souloutplus$guardUseItemOn(CallbackInfoReturnable<Object> cir) {
        if (CameraState.isSoulOut() && InteractionGuard.shouldBlockClicks()) {
            cir.setReturnValue(net.minecraft.world.InteractionResult.PASS);
        }
    }

    /** 左键长按持续挖掘 */
    @Inject(method = "continueDestroyBlock", at = @At("HEAD"), cancellable = true)
    private void souloutplus$guardContinueDestroy(CallbackInfoReturnable<Boolean> cir) {
        if (CameraState.isSoulOut() && InteractionGuard.shouldBlockClicks()) {
            cir.setReturnValue(true);
        }
    }
}
