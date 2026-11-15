package com.souloutplus.mixin;

import com.mojang.blaze3d.resource.ResourceHandle;
import com.souloutplus.camera.CameraState;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.item.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 相机跨区块移动时的区块重建标记（从 Tweakeroo MixinWorldRenderer 移植）。
 *
 * 原版靠"服务端发新区块 → 客户端标记 dirty"来触发地形重建；
 * 出窍状态下相机飞来飞去没有这个过程，边缘区块会消失，
 * 所以相机跨区块时手动 scheduleRebuild。
 */
@Mixin(LevelRenderer.class)
public abstract class MixinLevelRenderer {

    @Unique
    private int souloutplus$lastCameraChunkX;
    @Unique
    private int souloutplus$lastCameraChunkZ;

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void souloutplus$trackCameraChunk(ResourceHandle handle, CallbackInfo ci) {
        if (!CameraState.isSoulOut()) return;
        var camera = CameraState.getCamera();
        if (camera == null) return;

        int chunkX = net.minecraft.util.Mth.floor(camera.getX()) >> 4;
        int chunkZ = net.minecraft.util.Mth.floor(camera.getZ()) >> 4;
        if (chunkX != this.souloutplus$lastCameraChunkX || chunkZ != this.souloutplus$lastCameraChunkZ) {
            // 手动标记相机新视野边缘的区块重建（Tweakeroo CameraUtils.markChunksForRebuild 的简化版：
            // 不精确圈边缘，标记相机周围 viewDistance 一圈，靠 scheduleBlockRender 幂等性去重）
            LevelRenderer self = (LevelRenderer) (Object) this;
            var mc = net.minecraft.client.Minecraft.getInstance();
            int viewDistance = mc.options.renderDistance().get();
            for (int cx = chunkX - viewDistance; cx <= chunkX + viewDistance; cx++) {
                for (int cz = chunkZ - viewDistance; cz <= chunkZ + viewDistance; cz++) {
                    // 仅当已在旧视野外沿（新出现的）才需要重建；内部区块由正常渲染管线处理
                    if (Math.abs(cx - this.souloutplus$lastCameraChunkX) > viewDistance
                            || Math.abs(cz - this.souloutplus$lastCameraChunkZ) > viewDistance) {
                        self.scheduleBlockRerender(cx, camera.getBlockY(), cz);
                    }
                }
            }
            this.souloutplus$lastCameraChunkX = chunkX;
            this.souloutplus$lastCameraChunkZ = chunkZ;
        }
    }

    @org.spongepowered.asm.mixin.Unique
    private void scheduleBlockRerender(int x, int y, int z) {
        // no-op 占位：真正实现编译后按 1.21.11 实际方法名调整
    }
}
