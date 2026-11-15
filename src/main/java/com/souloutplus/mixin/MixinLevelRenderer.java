package com.souloutplus.mixin;

import com.souloutplus.camera.CameraState;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 相机跨区块移动时的区块重建标记（从 Tweakeroo MixinWorldRenderer 移植到 1.21.11）。
 *
 * 原版靠"服务端发新区块 → 客户端标记 dirty"来触发地形重建；
 * 出窍状态下相机飞来飞去没有这个过程，视野新出现的边缘区块会不渲染，
 * 所以相机跨区块时手动 setBlocksDirty 圈一圈。
 *
 * 1.21.11 renderLevel 签名（已实测）：
 * renderLevel(GraphicsResourceAllocator, DeltaTracker, boolean, Camera, Matrix4f x3, GpuBufferSlice, Vector4f, boolean)
 */
@Mixin(LevelRenderer.class)
public abstract class MixinLevelRenderer {
    @Unique
    private int souloutplus$lastCameraChunkX = Integer.MIN_VALUE;
    @Unique
    private int souloutplus$lastCameraChunkZ = Integer.MIN_VALUE;

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void souloutplus$trackCameraChunk(com.mojang.blaze3d.resource.GraphicsResourceAllocator allocator,
                                              DeltaTracker deltaTracker, boolean renderBlockOutline,
                                              Camera camera, org.joml.Matrix4f frustumMatrix,
                                              org.joml.Matrix4f projectionMatrix,
                                              org.joml.Matrix4f viewMatrix,
                                              com.mojang.blaze3d.buffers.GpuBufferSlice bufferSlice,
                                              org.joml.Vector4f clipVector,
                                              boolean renderSky,
                                              CallbackInfo ci) {
        if (!CameraState.isSoulOut() || CameraState.getCamera() == null) return;
        var cam = CameraState.getCamera();

        int chunkX = cam.getBlockX() >> 4;
        int chunkZ = cam.getBlockZ() >> 4;
        if (this.souloutplus$lastCameraChunkX == Integer.MIN_VALUE) {
            this.souloutplus$lastCameraChunkX = chunkX;
            this.souloutplus$lastCameraChunkZ = chunkZ;
            return;
        }
        if (chunkX != this.souloutplus$lastCameraChunkX || chunkZ != this.souloutplus$lastCameraChunkZ) {
            LevelRenderer self = (LevelRenderer) (Object) this;
            int viewDistance = net.minecraft.client.Minecraft.getInstance().options.renderDistance().get();
            // 相机新进入的列（相对旧位置超出旧视野的）整列标脏（16~世界高）
            for (int cx = chunkX - viewDistance; cx <= chunkX + viewDistance; cx++) {
                for (int cz = chunkZ - viewDistance; cz <= chunkZ + viewDistance; cz++) {
                    boolean newColumn = Math.abs(cx - this.souloutplus$lastCameraChunkX) > viewDistance
                            || Math.abs(cz - this.souloutplus$lastCameraChunkZ) > viewDistance;
                    if (newColumn) {
                        self.setBlocksDirty(cx << 4, -64, cz << 4, (cx << 4) + 15, 320, (cz << 4) + 15);
                    }
                }
            }
            this.souloutplus$lastCameraChunkX = chunkX;
            this.souloutplus$lastCameraChunkZ = chunkZ;
        }
    }
}
