package com.souloutplus.mixin;

import com.souloutplus.camera.CameraState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(Gui.class)
public abstract class MixinGuiCameraPlayer {

    @Inject(method = "getCameraPlayer", at = @At("HEAD"), cancellable = true, require = 0)
    private void souloutplus$useBodyPlayerForHud(CallbackInfoReturnable<Player> cir) {
        if (CameraState.isSoulOut() && Minecraft.getInstance().player != null) {
            cir.setReturnValue(Minecraft.getInstance().player);
        }
    }
}