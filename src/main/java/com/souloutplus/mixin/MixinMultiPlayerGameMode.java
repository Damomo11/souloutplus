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
@Mixin(net.minecraft.client.multiplayer.MultiPlayerGameMode.class)
public abstract class MixinMultiPlayerGameMode {

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