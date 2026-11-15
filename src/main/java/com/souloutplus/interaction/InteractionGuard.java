package com.souloutplus.interaction;

import com.souloutplus.camera.CameraState;
import com.souloutplus.config.Configs;
import com.souloutplus.enums.CrosshairSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * 交互距离判定与守卫。
 *
 * 服务端校验的永远是"玩家本体眼睛 → 目标"（block/entity_interaction_range 属性）。
 * mod 侧的职责：
 *  - 客户端先用"有效眼睛 → 目标"距离做 UI 判定（可达性 HUD 的绿/黄/红）
 *  - 距离门：出窍时把 CameraEntity 的交互范围覆盖成配置值，让原版射线长度就是配置距离
 *
 * SOUL 模式下射线从相机打出（原版 GameRenderer.pick 的默认行为——getCameraEntity()），
 * 配置里的 placeRange/breakRange/entityRange 就是"相机的作用半径"。
 * 服务端是否接受取决于本体到目标的属性距离（单人可自动调，见 ReachHelper）。
 */
public final class InteractionGuard {
    private static final Minecraft MC = Minecraft.getInstance();

    private InteractionGuard() {
    }

    // ---------------- 模式 ----------------

    public static CrosshairSource source() {
        return (CrosshairSource) Configs.Generic.CROSSHAIR_SOURCE.getOptionListValue();
    }

    /** 出窍时左右键是否应该被屏蔽（OFF 模式） */
    public static boolean shouldBlockClicks() {
        return CameraState.isSoulOut() && source() == CrosshairSource.OFF;
    }

    /** BODY 模式：射线需要拉回本体（Tweakeroo freeCameraPlayerInputs 的效果） */
    public static boolean shouldRayFromBody() {
        return CameraState.isSoulOut() && source() == CrosshairSource.BODY;
    }

    // ---------------- 距离门（CameraEntity 的 range 覆盖） ----------------

    /** 相机在 SOUL 模式下的方块交互范围（原版 LocalPlayer.blockInteractionRange 的覆盖点，见 MixinCameraRange） */
    public static double cameraBlockRange() {
        if (!CameraState.isSoulOut() || !inSoulMode()) {
            // 非出窍/非SOUL：返回 -1 表示"不覆盖，走原版值"
            return -1;
        }
        double configured = Configs.Reach.PLACE_RANGE.getDoubleValue();
        return configured > 0 ? configured : MC.player != null ? MC.player.blockInteractionRange() : 4.5;
    }

    /** 相机在 SOUL 模式下的实体交互范围 */
    public static double cameraEntityRange() {
        if (!CameraState.isSoulOut() || !inSoulMode()) {
            return -1;
        }
        double configured = Configs.Reach.ENTITY_RANGE.getDoubleValue();
        return configured > 0 ? configured : MC.player != null ? MC.player.entityInteractionRange() : 3.0;
    }

    private static boolean inSoulMode() {
        return source() == CrosshairSource.SOUL;
    }

    // ---------------- 可达性三色 ----------------

    /** 有效眼睛：SOUL=相机眼，BODY/非出窍=本体眼 */
    public static Vec3 effectiveEye() {
        LocalPlayer player = MC.player;
        if (player == null) return Vec3.ZERO;
        if (shouldRayFromBody() || !CameraState.isSoulOut() || CameraState.getCamera() == null) {
            return player.getEyePosition();
        }
        return CameraState.getCamera().getEyePosition();
    }

    public enum ReachColor {
        GREEN, YELLOW, RED
    }

    /**
     * 可达性判定（HUD 用）：
     * GREEN  = 有效距离 ≤ 配置距离（本地判定通过）
     * YELLOW = 超配置，但单人调距后服务端仍会接受（或 respectServerCap 关闭）
     * RED    = 连服务器上限都超了，操作必被拒
     */
    public static ReachColor colorFor(HitResult hit) {
        if (hit == null) return ReachColor.RED;
        Vec3 eye = effectiveEye();
        double dist = eye.distanceTo(hit.getLocation());
        double range = rangeFor(hit);
        if (dist <= range) return ReachColor.GREEN;
        double serverCap = ReachHelper.serverAllowance();
        if (dist <= serverCap) return ReachColor.YELLOW;
        return ReachColor.RED;
    }

    private static double rangeFor(HitResult hit) {
        if (hit instanceof EntityHitResult) {
            double configured = Configs.Reach.ENTITY_RANGE.getDoubleValue();
            return configured > 0 ? configured : 3.0;
        }
        double configured = Configs.Reach.PLACE_RANGE.getDoubleValue();
        return configured > 0 ? configured : 4.5;
    }
}
