package com.souloutplus.mixin;

import com.souloutplus.camera.CameraEntity;
import com.souloutplus.interaction.InteractionGuard;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 距离门：出窍 SOUL 模式下，相机实体的交互范围 = 配置距离（不是本体属性）。
 *
 * 原版射线链（GameRenderer.pick → cameraEntity.raycastHitResult → blockInteractionRange/entityInteractionRange）
 * 从 cameraEntity 上读距离——出窍时 cameraEntity 是我们的 CameraEntity，
 * 覆盖这两个 getter 就让"准星能指多远"直接变成配置值，原版交互循环零改动。
 *
 * 本体（LocalPlayer）的属性不受影响：服务端校验、单人调距仍按本体属性走。
 */
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
