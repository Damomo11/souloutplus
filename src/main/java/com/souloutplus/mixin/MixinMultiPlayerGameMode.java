package com.souloutplus.mixin;

import com.souloutplus.camera.CameraState;
import com.souloutplus.interaction.InteractionGuard;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 点击守卫 + OFF 模式屏蔽（对应 Tweakeroo MixinClientPlayerInteractionManager 的"屏蔽"职责，
 * 但只在 OFF 模式时拦，SOUL/BODY 全放行）。
 *
 * 1.21.11 签名（已实测）：
 *  - boolean startDestroyBlock(BlockPos, Direction)
 *  - boolean continueDestroyBlock(BlockPos, Direction)
 *  - InteractionResult useItemOn(LocalPlayer, InteractionHand, BlockHitResult)
 */
@Mixin(MultiPlayerGameMode.class)
public abstract class MixinMultiPlayerGameMode {

    @Inject(method = "startDestroyBlock", at = @At("HEAD"), cancellable = true)
    private void souloutplus$guardStartDestroy(net.minecraft.core.BlockPos pos, net.minecraft.core.Direction face,
                                               CallbackInfoReturnable<Boolean> cir) {
        if (CameraState.isSoulOut() && InteractionGuard.shouldBlockClicks()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "continueDestroyBlock", at = @At("HEAD"), cancellable = true)
    private void souloutplus$guardContinueDestroy(net.minecraft.core.BlockPos pos, net.minecraft.core.Direction face,
                                                   CallbackInfoReturnable<Boolean> cir) {
        if (CameraState.isSoulOut() && InteractionGuard.shouldBlockClicks()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void souloutplus$guardUseItemOn(LocalPlayer player, InteractionHand hand,
                                            BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (CameraState.isSoulOut() && InteractionGuard.shouldBlockClicks()) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
