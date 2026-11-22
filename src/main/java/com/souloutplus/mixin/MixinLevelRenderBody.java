package com.souloutplus.mixin;

import com.souloutplus.camera.CameraState;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelExtractor.class)
public abstract class MixinLevelRenderBody {

    @Redirect(method = "extractVisibleEntities",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/Camera;entity()Lnet/minecraft/world/entity/Entity;",
                    ordinal = 3))
    private static Entity souloutplus$showBodyInSoul(Camera camera) {
        if (CameraState.isSoulOut() && Minecraft.getInstance().player != null) {
            return Minecraft.getInstance().player;
        }
        return camera.entity();
    }
}
