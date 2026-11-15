package com.souloutplus.mixin;

import com.souloutplus.camera.CameraEntity;
import com.souloutplus.interaction.InteractionGuard;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(LocalPlayer.class)
public abstract class MixinCameraRange {

    @Inject(method = "blockInteractionRange", at = @At("HEAD"), cancellable = true)
    private void souloutplus$cameraBlockRange(CallbackInfoReturnable<Double> cir) {
        if ((Object) this instanceof CameraEntity) {
            double range = InteractionGuard.cameraBlockRange();
            if (range >= 0) {
                cir.setReturnValue(range);
            }
        }
    }

    @Inject(method = "entityInteractionRange", at = @At("HEAD"), cancellable = true)
    private void souloutplus$cameraEntityRange(CallbackInfoReturnable<Double> cir) {
        if ((Object) this instanceof CameraEntity) {
            double range = InteractionGuard.cameraEntityRange();
            if (range >= 0) {
                cir.setReturnValue(range);
            }
        }
    }
}