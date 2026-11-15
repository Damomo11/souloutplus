package com.souloutplus.mixin;

import com.souloutplus.camera.CameraState;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true, require = 0)
    private void souloutplus$removeHandRender(float partialTicks, boolean renderBlockOutline,
                                              org.joml.Matrix4f matrix4f, CallbackInfo ci) {
        if (CameraState.isSoulOut()) {
            ci.cancel();
        }
    }
}