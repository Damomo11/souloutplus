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
public final class InteractionGuard {
    private static final Minecraft MC = Minecraft.getInstance();

    private InteractionGuard() {
    }

    public static CrosshairSource source() {
        return (CrosshairSource) Configs.Generic.CROSSHAIR_SOURCE.getOptionListValue();
    }
    public static boolean shouldBlockClicks() {
        return CameraState.isSoulOut() && source() == CrosshairSource.OFF;
    }
    public static boolean shouldRayFromBody() {
        return CameraState.isSoulOut() && source() == CrosshairSource.BODY;
    }
    public static double cameraBlockRange() {
        if (!CameraState.isSoulOut() || !inSoulMode()) {
            return -1;
        }
        double configured = Configs.Reach.PLACE_RANGE.getDoubleValue();
        return configured > 0 ? configured : MC.player != null ? MC.player.blockInteractionRange() : 4.5;
    }
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