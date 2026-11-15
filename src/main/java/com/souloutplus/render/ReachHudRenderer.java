package com.souloutplus.render;

import com.souloutplus.camera.CameraState;
import com.souloutplus.config.Configs;
import com.souloutplus.interaction.InteractionGuard;
import fi.dy.masa.malilib.interfaces.IRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.HitResult;

/**
 * 世界上层渲染（malilib IRenderer，每帧 renderWorldLast 调用）：
 *  - 可达性三色点（准星旁）
 *  - 被拒方块红闪（占位，后续 ActionConfirm 接入后填充）
 *
 * 出窍时的幽灵方块预览也挂这里（GHOST_PREVIEW 开着时渲染准星处待放置方块的线框+朝向）。
 */
public class ReachHudRenderer implements IRenderer {
    @Override
    public void onRenderWorldLast(net.minecraft.client.renderer.LevelRenderer levelRenderer,
                                   float partialTicks, boolean renderBlockOutline) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !CameraState.isSoulOut()) return;

        // 三色点：画在准星右下 12px 处（screen-space，用矩阵栈透传太复杂，用 debug hud 的简化路径）
        if (Configs.Render.REACH_HUD.getBooleanValue()) {
            HitResult hit = mc.hitResult;
            if (hit != null && hit.getType() != HitResult.Type.MISS) {
                InteractionGuard.ReachColor color = InteractionGuard.colorFor(hit);
                // 颜色缓存到静态字段，Gui.render 的 mixin 读取画点
                HudCache.lastReachColor = color;
            } else {
                HudCache.lastReachColor = null;
            }
        }
    }

    @Override
    public void onRenderTooltipLast(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // no-op
    }
}
