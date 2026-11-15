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

/**
 * 被拒方块红闪检测（客户端被动式）：
 * 出窍期间发出去的"开始挖掘"目标格子 + 停止时未挖穿 → 视为被拒，进闪红队列。
 *
 * 挖掘路径简单可靠：客户端预测的裂纹进度被服务端 reset 时原版会走 destroyBlock(...)
 * 这里用更朴素的方式：出窍时记录本 tick 挖的格子，若 20 tick 内没变空气 → noteRejected。
 * 准确对账（序号 ack）属于打印机方案，等服务端协议接入时再升。
 */
@Mixin(net.minecraft.client.multiplayer.ClientLevel.class)
public abstract class MixinClientLevel {

    @Unique
    private static final int GIVE_UP_TICKS = 20;

    /** 出窍期间最近一次开始挖掘的格子（来自 MixinMultiPlayerGameMode 的 startDestroyBlock） */
    @Inject(method = "destroyBlock", at = @At("HEAD"))
    private void souloutplus$onDestroyBlock(BlockPos pos, boolean dropBlock, int serverParticleId, CallbackInfo ci) {
        // 客户端 destroyBlock（预测破坏）：出窍时这一格如果很快被服务端"变回来"（同步包重放旧状态），
        // 红闪判定交给 RejectedFlash.onBlockChanged（简化版：不区分是谁放的，只对出窍期间发出动作的格子敏感）
        if (CameraState.isSoulOut()) {
            com.souloutplus.render.RejectedFlash.onBlockChanged(pos);
        }
    }
}
