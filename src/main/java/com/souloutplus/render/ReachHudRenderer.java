package com.souloutplus.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;
import com.souloutplus.camera.CameraState;
import com.souloutplus.config.Configs;
import com.souloutplus.enums.PreviewStyle;
import com.souloutplus.interaction.InteractionGuard;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

public class ReachHudRenderer implements IRenderer {

    private static final Color4f PREVIEW_GREEN = new Color4f(0.2F, 0.9F, 0.5F, 0.4F);
    private static final Color4f ARROW_ORANGE = new Color4f(1.0F, 0.5F, 0.15F, 0.9F);

    @Override
    public void onRenderWorldLast(RenderTarget renderTarget, Matrix4fc frustumMatrix, CameraRenderState cameraRenderState,
                                  Frustum frustum, RenderBuffers renderBuffers, GpuBufferSlice bufferSlice,
                                  Vector4f clipVector, ProfilerFiller profiler) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !CameraState.isSoulOut()) return;

        if (Configs.Render.REACH_HUD.getBooleanValue()) {
            HitResult hit = mc.hitResult;
            if (hit != null && hit.getType() != HitResult.Type.MISS) {
                HudCache.lastReachColor = InteractionGuard.colorFor(hit);
            } else {
                HudCache.lastReachColor = null;
            }
        }

        if (Configs.Render.GHOST_PREVIEW.getBooleanValue()) {
            renderGhostPreview(mc);
        }

        if (Configs.Placement.REJECT_FLASH.getBooleanValue()) {
            RejectedFlash.render();
        }
    }

    @Override
    public void onExtractGuiOverlayPost(GuiContext context, float partialTicks, ProfilerFiller profiler) {
        if (!CameraState.isSoulOut()) return;
        Minecraft mc = context.mc();
        if (mc.player == null) return;

        Font font = context.fontRenderer();
        context.drawString(font, HudCache.SOUL_BADGE, 8, 8, 0xFFFFFF);

        var color = HudCache.lastReachColor;
        if (color != null) {
            Window window = mc.getWindow();
            int x = window.getGuiScaledWidth() / 2 + 8;
            int y = window.getGuiScaledHeight() / 2 + 8;
            int argb = switch (color) {
                case GREEN -> 0xFF00CC44;
                case YELLOW -> 0xFFDDCC00;
                case RED -> 0xFFCC2200;
            };
            context.fill(x, y, x + 4, y + 4, argb);
        }
    }

    private void renderGhostPreview(Minecraft mc) {
        HitResult hit = mc.hitResult;
        if (!(hit instanceof BlockHitResult blockHit) || blockHit.getType() != HitResult.Type.BLOCK) return;

        ItemStack held = mc.player.getMainHandItem();
        if (!(held.getItem() instanceof BlockItem)) return;  // 手里不是方块：没有预览

        BlockPos placePos = blockHit.getBlockPos().relative(blockHit.getDirection());
        Direction facing = Direction.fromYRot(CameraState.getCamera().getYRot());

        float lineWidth = 2.0F;
        switch ((PreviewStyle) Configs.Render.PREVIEW_STYLE.getOptionListValue()) {
            case WIREFRAME ->
                    RenderUtils.renderBlockOutline(placePos, 0F, lineWidth, PREVIEW_GREEN, false);
            case TRANSLUCENT -> {
                RenderUtils.renderBlockOutline(placePos, 0F, lineWidth, PREVIEW_GREEN, true);
            }
            case ARROW -> {
                RenderUtils.renderBlockOutline(placePos, 0F, lineWidth, PREVIEW_GREEN, true);
                drawFacingArrow(placePos, facing);
            }
        }
    }
    private void drawFacingArrow(BlockPos pos, Direction facing) {
        BlockPos center = pos;
        BlockPos tip = pos.relative(facing);
        RenderUtils.renderBlockOutline(
                tip, 0F, 1.5F, ARROW_ORANGE, false);
    }
}
