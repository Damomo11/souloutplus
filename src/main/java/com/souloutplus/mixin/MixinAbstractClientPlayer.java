package com.souloutplus.mixin;

import com.souloutplus.camera.CameraState;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 出窍时把玩家的 isSpectator 伪装成 true（从 Tweakeroo MixinAbstractClientPlayerEntity 移植）。
 * 作用：LevelRenderer 判定 "相机是旁观者 → 本体玩家照常渲染"，
 * 于是出窍状态下能看到自己的身体（相机 noClip 穿到墙外时本体不会被剔除）。
 */
@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer {

    @Inject(method = "isSpectator", at = @At("HEAD"), cancellable = true)
    private void souloutplus$spoofSpectator(CallbackInfoReturnable<Boolean> cir) {
        if (CameraState.isSoulOut()) {
            cir.setReturnValue(true);
        }
    }
}
