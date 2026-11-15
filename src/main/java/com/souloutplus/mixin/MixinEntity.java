package com.souloutplus.mixin;

import com.souloutplus.camera.CameraState;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 鼠标转向 → 相机（从 Tweakeroo MixinEntity 移植）。
 *
 * 出窍（SOUL/BODY/OFF 任一非默认状态）时鼠标增量喂给 CameraEntity；
 * 不 cancel 原版路径 —— 本体朝向照样跟着鼠标转，
 * 于是原版移动包把"身体朝向=鼠标朝向"同步给服务端，
 * 服务端放置朝向敏感方块（侦测器/活塞）时头朝向即鼠标朝向。零包手术。
 */
@Mixin(Entity.class)
public abstract class MixinEntity {

    @Inject(method = "turn", at = @At("HEAD"))
    private void souloutplus$feedCameraRotations(double yawChange, double pitchChange, CallbackInfo ci) {
        if ((Object) this instanceof LocalPlayer && CameraState.isSoulOut()) {
            CameraState.getCamera().updateCameraRotations((float) yawChange, (float) pitchChange);
        }
        // 注意：不 cancel —— 本体照常转向（见类注释）
    }
}
