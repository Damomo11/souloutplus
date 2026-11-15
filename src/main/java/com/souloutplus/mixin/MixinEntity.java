package com.souloutplus.mixin;

import com.souloutplus.camera.CameraState;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Inject(method = "turn", at = @At("HEAD"))
    private void souloutplus$feedCameraRotations(double yawChange, double pitchChange, CallbackInfo ci) {
        if ((Object) this instanceof LocalPlayer localPlayer && CameraState.isSoulOut()
                && CameraState.getCamera() != null) {
            CameraState.getCamera().setCameraRotations(localPlayer.getYRot(), localPlayer.getXRot());
        }
    }
}
