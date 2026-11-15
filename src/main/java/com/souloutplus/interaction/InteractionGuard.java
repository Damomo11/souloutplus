package com.souloutplus.interaction;

import com.souloutplus.camera.CameraState;
import com.souloutplus.config.Configs;
import com.souloutplus.enums.CrosshairSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * 交互距离判定与守卫。
 *
 * 服务端校验的永远是"玩家本体眼睛 → 目标"（block/entity_interaction_range）。
 * mod 侧的职责：
 *  - 客户端先用"相机眼睛 → 目标"距离做 UI 判定（可达性 HUD 的绿/黄/红）
 *  - 点击守卫：超配置范围的点击本地吃掉，不发会被拒绝的包
 *
 * 注意：SOUL 模式下射线从相机打出（原版 updateCrosshairTarget 的默认行为，见设计），
 * 所以这里的"距离"是相机到目标的距离 —— 配置里的 placeRange 等就是"相机的作用半径"。
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

    // ---------------- 距离 ----------------

    /** 相机眼到目标方块的直线距离 */
    public static double distanceToTarget(HitResult hit) {
        if (hit == null) return Double.MAX_VALUE;
        Vec3 eye = effectiveEye();
        Vec3 loc = hit.getLocation();
        return eye.distanceTo(loc);
    }

    /** BODY 模式射线/朝向都用本体眼；SOUL 用相机眼 */
    public static Vec3 effectiveEye() {
        LocalPlayer player = MC.player;
        if (player == null) return Vec3.ZERO;
        if (shouldRayFromBody() || !CameraState.isSoulOut()) {
            return player.getEyePosition();
        }
        return CameraState.getCamera() != null ? CameraState.getCamera().getEyePosition() : player.getEyePosition();
    }

    // ---------------- 可达性三色 ----------------

    public enum ReachColor {
        GREEN, YELLOW, RED
    }

    /**
     * 可达性判定（HUD + 守卫共用）：
     * GREEN  = 相机距离 ≤ 配置距离（本地判定通过；服务端单人可自动调距）
     * YELLOW = 相机距离 > 配置，但仍在"服务器允许的距离"内（会被调距后的服务端接受）
     * RED    = 全部超限，点击会被吃掉
     */
    public static ReachColor colorFor(HitResult hit) {
        if (hit == null) return ReachColor.RED;
        double dist = distanceToTarget(hit);
        double range = rangeFor(hit);
        if (dist <= range) return ReachColor.GREEN;
        double serverCap = ReachHelper.serverAllowance();
        if (dist <= serverCap) return ReachColor.YELLOW;
        return ReachColor.RED;
    }

    /** 点击守卫：true = 放行；false = 本地吃掉（不发注定被拒的包） */
    public static boolean allowClick(HitResult hit) {
        if (hit == null) return true;
        if (shouldBlockClicks()) return false;
        return colorFor(hit) != ReachColor.RED || !Configs.Reach.RESPECT_SERVER_CAP.getBooleanValue();
    }

    private static double rangeFor(HitResult hit) {
        if (hit instanceof EntityHitResult) {
            return Configs.Reach.ENTITY_RANGE.getDoubleValue();
        }
        return Configs.Reach.PLACE_RANGE.getDoubleValue();
    }

    // ---------------- 实体门 ----------------

    /** 出窍时是否允许实体交互/攻击走相机射线（配置开关） */
    public static boolean entityInteractAllowed() {
        return !CameraState.isSoulOut() || Configs.Generic.ENTITY_INTERACT.getBooleanValue();
    }
}
