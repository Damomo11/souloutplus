package com.souloutplus.mixin;

import com.souloutplus.camera.CameraState;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 出窍时把玩家的 isSpectator 伪装成 true（从 Tweakeroo MixinAbstractClientPlayerEntity 移植；
 * 1.21.11 mojmap 里 isSpectator 的实现位置在 Player，不在 AbstractClientPlayer）。
 *
 * 作用：渲染器判定"相机是旁观者 → 本体玩家照常渲染"，
 * 于是出窍状态下能看到自己的身体（相机 noPhysics 穿到墙外时本体不会被面别剔除）。
 * 注意伪装范围：LevelRenderer 的渲染判定用 isSpectator —— 但这个方法也被一些游戏逻辑读
 * （例如旁观者不触发方块碰撞、不吃东西）。出窍是短时状态，交互路径上原版
 * 逻辑都会先走 gameMode/abilities 判断，实测影响面可控（Tweakeroo 同样做法多年）。
 */
@Mixin(Player.class)
public abstract class MixinPlayer {

    @Inject(method = "isSpectator", at = @At("HEAD"), cancellable = true)
    private void souloutplus$spoofSpectator(CallbackInfoReturnable<Boolean> cir) {
        if (CameraState.isSoulOut()) {
            cir.setReturnValue(true);
        }
    }
}
