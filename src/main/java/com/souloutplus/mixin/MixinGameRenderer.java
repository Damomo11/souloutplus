package com.souloutplus.mixin;

import com.souloutplus.camera.CameraState;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 出窍时不再渲染第一人称手臂（从 Tweakeroo MixinGameRenderer 移植）。
 */
@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true, require = 0)
    private void souloutplus$removeHandRender(CallbackInfo ci) {
        if (CameraState.isSoulOut()) {
            ci.cancel();
        }
    }
}
