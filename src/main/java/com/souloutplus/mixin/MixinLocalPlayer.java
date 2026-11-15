package com.souloutplus.mixin;

import com.souloutplus.camera.CameraState;
import com.souloutplus.camera.DummyMovementInput;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer {
    @Unique
    private final DummyMovementInput souloutplus$dummyInput = new DummyMovementInput();
    @Unique
    private ClientInput souloutplus$realInput;

    @Inject(method = "tick", at = @At("HEAD"))
    private void souloutplus$onTickHead(CallbackInfo ci) {
        LocalPlayer self = (LocalPlayer) (Object) this;
        if (CameraState.isSoulOut()) {
            CameraState.movementTick();
            if (this.souloutplus$realInput == null) {
                this.souloutplus$realInput = self.input;
                self.input = this.souloutplus$dummyInput;
            }
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void souloutplus$onTickTail(CallbackInfo ci) {
        LocalPlayer self = (LocalPlayer) (Object) this;
        if (this.souloutplus$realInput != null) {
            self.input = this.souloutplus$realInput;
            this.souloutplus$realInput = null;
        }
    }
}