package com.souloutplus.mixin;

import com.souloutplus.camera.CameraState;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(Player.class)
public abstract class MixinPlayer {

    @Inject(method = "isSpectator", at = @At("HEAD"), cancellable = true)
    private void souloutplus$spoofSpectator(CallbackInfoReturnable<Boolean> cir) {
        if (CameraState.isSoulOut()) {
            cir.setReturnValue(true);
        }
    }
}