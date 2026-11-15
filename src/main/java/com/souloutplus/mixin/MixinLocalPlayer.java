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

/**
 * 出窍时：
 *  - tick HEAD：相机每 tick 移动（CameraState.movementTick）+ 本体输入换 DummyMovementInput（保留 shift）
 *  - tick TAIL：换回真实输入
 * （从 Tweakeroo MixinClientPlayerEntity 移植；shift 透传是本 mod 的改动点。
 *  1.21.11 的输入类型是 ClientInput，不是旧版的 Input。）
 */
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
