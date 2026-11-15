package com.souloutplus.mixin;

import com.souloutplus.camera.CameraState;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(net.minecraft.client.multiplayer.ClientLevel.class)
public abstract class MixinClientLevel {

    @Unique
    private static final int GIVE_UP_TICKS = 20;
    @Inject(method = "destroyBlock", at = @At("HEAD"))
    private void souloutplus$onDestroyBlock(BlockPos pos, boolean dropBlock, int serverParticleId, CallbackInfo ci) {
        if (CameraState.isSoulOut()) {
            com.souloutplus.render.RejectedFlash.onBlockChanged(pos);
        }
    }
}