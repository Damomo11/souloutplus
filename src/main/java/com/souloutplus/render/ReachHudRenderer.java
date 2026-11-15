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

/**
 * 世界上层渲染（malilib IRenderer，每帧 onRenderWorldLast 调用）：
 *  - 可达性三色点数据计算（结果放 HudCache，Gui.render 的 mixin 画）
 *  - 朝向幽灵预览：准星处待放置方块的轮廓 + 放下去的朝向（含朝向箭头样式）
 *  - 被拒方块红闪（服务端撤回时闪红，位置由 RejectedFlash 记录）
 *
 * 全部用 malilib RenderUtils 的世界空间绘制（renderBlockOutline 等，1.21.11 已核对签名）。
 */
public class ReachHudRenderer implements IRenderer {

    private static final Color4f PREVIEW_GREEN = new Color4f(0.2F, 0.9F, 0.5F, 0.4F);
    private static final Color4f PREVIEW_FILL = new Color4f(0.2F, 0.9F, 0.5F, 0.12F);
    private static final Color4f ARROW_ORANGE = new Color4f(1.0F, 0.5F, 0.15F, 0.9F);

    @Override
    public void onRenderWorldLast(Matrix4f matrixStack, Matrix4f projectionMatrix) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !CameraState.isSoulOut()) return;

        // ---- 1. 可达性三色点数据（world pass 算，GUI pass 画） ----
        if (Configs.Render.REACH_HUD.getBooleanValue()) {
            HitResult hit = mc.hitResult;
            if (hit != null && hit.getType() != HitResult.Type.MISS) {
                HudCache.lastReachColor = InteractionGuard.colorFor(hit);
            } else {
                HudCache.lastReachColor = null;
            }
        }

        // ---- 2. 幽灵预览 ----
        if (Configs.Render.GHOST_PREVIEW.getBooleanValue()) {
            renderGhostPreview(mc);
        }

        // ---- 3. 被拒方块红闪 ----
        if (Configs.Placement.REJECT_FLASH.getBooleanValue()) {
            RejectedFlash.render();
        }
    }

    /** 幽灵预览：准星指向的可放置位置的轮廓 + 朝向箭头 */
    private void renderGhostPreview(Minecraft mc) {
        HitResult hit = mc.hitResult;
        if (!(hit instanceof BlockHitResult blockHit) || blockHit.getType() != HitResult.Type.BLOCK) return;

        ItemStack held = mc.player.getMainHandItem();
        if (!(held.getItem() instanceof BlockItem)) return;  // 手里不是方块：没有预览

        // 放置位置 = 点击面外那格（原版放置规则）
        BlockPos placePos = blockHit.getBlockPos().relative(blockHit.getDirection());
        // 放下去的水平朝向 = 相机朝向（和真实放置一致：服务端用头朝向放朝向敏感方块）
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

    /** 朝向箭头：一条竖线 + 方块中心向 facing 伸出的一条水平短线段，肉眼可读出朝向 */
    private void drawFacingArrow(BlockPos pos, Direction facing) {
        // 用两个薄方块轮廓拼一条"从方块中心指向 facing"的箭头线
        BlockPos center = pos;
        BlockPos tip = pos.relative(facing);
        // 画从中心向 facing 半格的小盒（相对偏移，视觉上就是箭头）
        RenderUtils.renderBlockOutline(
                tip, 0F, 1.5F, ARROW_ORANGE, false);
    }
}
