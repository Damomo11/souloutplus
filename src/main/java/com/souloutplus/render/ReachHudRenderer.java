package com.souloutplus.render;

import com.souloutplus.camera.CameraState;
import com.souloutplus.config.Configs;
import com.souloutplus.interaction.InteractionGuard;
import fi.dy.masa.malilib.interfaces.IRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.HitResult;
import org.joml.Matrix4f;

/**
 * 世界上层渲染（malilib IRenderer，每帧 onRenderWorldLast 调用）：
 *  - 可达性三色点数据计算（结果放 HudCache，Gui.render 的 mixin 画）
 *  - 被拒方块红闪（占位，后续 ActionConfirm 接入后填充）
 *  - 幽灵方块预览（GHOST_PREVIEW 开着时渲染准星处待放置方块的线框+朝向，待接入渲染工具后补）
 */
public class ReachHudRenderer implements IRenderer {
    @Override
    public void onRenderWorldLast(Matrix4f matrixStack, Matrix4f projectionMatrix) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !CameraState.isSoulOut()) return;

        // 三色点数据（world pass 算，GUI pass 画）
        if (Configs.Render.REACH_HUD.getBooleanValue()) {
            HitResult hit = mc.hitResult;
            if (hit != null && hit.getType() != HitResult.Type.MISS) {
                HudCache.lastReachColor = InteractionGuard.colorFor(hit);
            } else {
                HudCache.lastReachColor = null;
            }
        }
    }
}
