package com.souloutplus.mixin;

import com.souloutplus.camera.CameraState;
import com.souloutplus.config.Configs;
import com.souloutplus.interaction.InteractionGuard;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 点击守卫（1.21.11 已实测的方法签名）：
 *  - OFF 模式：屏蔽全部攻击/使用/挖掘（= Tweakeroo 默认行为）
 *  - SOUL 模式：放行（原版循环照常工作，距离门在 MixinCameraRange）
 *  - 实体交互/攻击：ENTITY_INTERACT 开关控制
 *
 * startAttack 的调用链（已反编译核对）：
 *   Minecraft.startAttack → GameMode.attack(player, crosshairPickEntity)      ← 实体攻击
 *                        → GameMode.startDestroyBlock(pos, face)            ← 方块挖掘
 *   Minecraft.continueAttack → GameMode.continueDestroyBlock(pos, face)      ← 持续挖掘
 *   Minecraft.startUseItem → GameMode.useItemOn(player, hand, blockHit)      ← 放置/交互
 *                         → GameMode.interact/interactAt                    ← 实体交互
 * 我们全部在 GameMode 这一层拦 —— 深度刚好：比拦 Minecraft 浅（少一层重定向），比拦包深（保留了客户端预测路径）。
 */
@Mixin(net.minecraft.client.multiplayer.MultiPlayerGameMode.class)
public abstract class MixinMultiPlayerGameMode {

    // ---------------- OFF 模式屏蔽 + 守卫 ----------------

    @Inject(method = "startDestroyBlock", at = @At("HEAD"), cancellable = true)
    private void souloutplus$guardStartDestroy(net.minecraft.core.BlockPos pos, net.minecraft.core.Direction face,
                                                CallbackInfoReturnable<Boolean> cir) {
        if (InteractionGuard.shouldBlockClicks()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "continueDestroyBlock", at = @At("HEAD"), cancellable = true)
    private void souloutplus$guardContinueDestroy(net.minecraft.core.BlockPos pos, net.minecraft.core.Direction face,
                                                  CallbackInfoReturnable<Boolean> cir) {
        if (InteractionGuard.shouldBlockClicks()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void souloutplus$guardUseItemOn(net.minecraft.client.player.LocalPlayer player, InteractionHand hand,
                                            BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (InteractionGuard.shouldBlockClicks()) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    // ---------------- 实体交互/攻击门 ----------------

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void souloutplus$guardAttack(Player player, Entity target, CallbackInfo ci) {
        if (CameraState.isSoulOut() && !Configs.Generic.ENTITY_INTERACT.getBooleanValue()) {
            ci.cancel();
        }
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void souloutplus$guardInteract(Player player, Entity target, InteractionHand hand,
                                           CallbackInfoReturnable<InteractionResult> cir) {
        if (CameraState.isSoulOut() && !Configs.Generic.ENTITY_INTERACT.getBooleanValue()) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    @Inject(method = "interactAt", at = @At("HEAD"), cancellable = true)
    private void souloutplus$guardInteractAt(Player player, Entity target, EntityHitResult hit, InteractionHand hand,
                                             CallbackInfoReturnable<InteractionResult> cir) {
        if (CameraState.isSoulOut() && !Configs.Generic.ENTITY_INTERACT.getBooleanValue()) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
