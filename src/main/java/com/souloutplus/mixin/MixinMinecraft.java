package com.souloutplus.mixin;

import com.souloutplus.config.Configs;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.souloutplus.camera.CameraState;
@Mixin(net.minecraft.client.Minecraft.class)
public abstract class MixinMinecraft {

    @Inject(method = "tick", at = @At("RETURN"))
    private void souloutplus$snapAim(CallbackInfo ci) {
        if (!CameraState.isSoulOut() || CameraState.getCamera() == null) return;

        var snapKey = Configs.Hotkeys.SNAP_AIM.getKeybind();
        if (snapKey != null && snapKey.isKeybindHeld()) {
            var camera = CameraState.getCamera();
            float yaw = camera.getYRot();
            float pitch = camera.getXRot();
            float snappedYaw = Mth.floor(yaw / 90F) * 90F;
            snappedYaw = Mth.wrapDegrees(snappedYaw);
            camera.setCameraRotations(snappedYaw, pitch >= 45 ? 90 : pitch <= -45 ? -90 : 0);
        }
    }
}