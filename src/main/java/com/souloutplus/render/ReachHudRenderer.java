package com.souloutplus.render;

import com.souloutplus.camera.CameraState;
import com.souloutplus.config.Configs;
import com.souloutplus.enums.PreviewStyle;
import com.souloutplus.interaction.InteractionGuard;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.joml.Matrix4f;
public class ReachHudRenderer implements IRenderer {

    private static final Color4f PREVIEW_GREEN = new Color4f(0.2F, 0.9F, 0.5F, 0.4F);
    private static final Color4f PREVIEW_FILL = new Color4f(0.2F, 0.9F, 0.5F, 0.12F);
    private static final Color4f ARROW_ORANGE = new Color4f(1.0F, 0.5F, 0.15F, 0.9F);

    @Override
    public void onRenderWorldLast(Matrix4f matrixStack, Matrix4f projectionMatrix) {
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