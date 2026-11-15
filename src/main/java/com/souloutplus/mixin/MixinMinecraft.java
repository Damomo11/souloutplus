package com.souloutplus.mixin;

import com.souloutplus.config.Configs;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.souloutplus.camera.CameraState;

/**
 * 相机吸附六向（按住热键时把相机 yaw/pitch 吸附到最近的水平/垂直朝向）。
 * 从 Tweakeroo SnapAim 移植的简化版：吸附只作用于 CameraEntity，本体朝向不吸附
 * （本体朝向=服务端放置朝向，出窍期间希望它保持和相机一致，由 MixinEntity 的自然同步流程走）。
 *
 * 放在 Minecraft.tick 之后跑（每 tick 检查热键状态）。
 */
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
            // 吸附：yaw 到最近的 22.5°*8（水平六向 + 45°对角），pitch 到 ±90 或 0
            float snappedYaw = Mth.floor(yaw / 90F) * 90F;
            snappedYaw = Mth.wrapDegrees(snappedYaw);
            camera.setCameraRotations(snappedYaw, pitch >= 45 ? 90 : pitch <= -45 ? -90 : 0);
        }
    }
}
