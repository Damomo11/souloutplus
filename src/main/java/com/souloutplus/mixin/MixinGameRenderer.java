package com.souloutplus.mixin;

import com.souloutplus.camera.CameraState;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true, require = 0)
    private void souloutplus$removeHandRender(CameraRenderState cameraRenderState, float partialTicks,
                                              org.joml.Matrix4fc matrix4f, CallbackInfo ci) {
        if (CameraState.isSoulOut()) {
            ci.cancel();
        }
    }
}
