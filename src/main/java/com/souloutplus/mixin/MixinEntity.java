package com.souloutplus.mixin;

import com.souloutplus.camera.CameraState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 鼠标转向 → 相机（从 Tweakeroo MixinEntity 移植到 1.21.11 的 Entity.turn(double, double)）。
 *
 * 出窍时鼠标增量喂给 CameraEntity；
 * 不 cancel 原版路径 —— 本体朝向照样跟着鼠标转，
 * 于是原版移动包把"身体朝向=鼠标朝向"同步给服务端，
 * 服务端放置朝向敏感方块（侦测器/活塞）时头朝向即鼠标朝向。零包手术。
 */
@Mixin(Entity.class)
public abstract class MixinEntity {

    @Inject(method = "turn", at = @At("HEAD"))
    private void souloutplus$feedCameraRotations(double yawChange, double pitchChange, CallbackInfo ci) {
        if ((Object) this instanceof Player && CameraState.isSoulOut()
                && CameraState.getCamera() != null) {
            CameraState.getCamera().updateCameraRotations((float) yawChange, (float) pitchChange);
        }
        // 注意：不 cancel —— 本体照常转向（见类注释）
    }
}
