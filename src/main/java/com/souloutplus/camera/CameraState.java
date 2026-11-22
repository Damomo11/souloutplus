package com.souloutplus.camera;

import com.souloutplus.config.Configs;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
public final class CameraState {
    private static final Minecraft MC = Minecraft.getInstance();

    private static CameraEntity camera;
    private static Entity originalCameraEntity;
    private static boolean originalCameraWasPlayer;
    private static boolean cullChunksOriginal;
    private static Vec3 rememberedPos;
    private static float rememberedYaw, rememberedPitch;

    private static boolean soulOut;

    private CameraState() {
    }

    public static boolean isSoulOut() {
        return soulOut;
    }

    public static CameraEntity getCamera() {
        return camera;
    }

    public static void toggle() {
        if (isSoulOut()) {
            removeCamera();
        } else {
            createCamera();
        }
    }

    private static void createCamera() {
        LocalPlayer player = MC.player;
        if (player == null || MC.level == null) {
            com.souloutplus.SoulOutPlus.LOGGER.warn("createCamera skipped: player/level null");
            return;
        }

        if (MC.getCameraEntity() != player) {
            com.souloutplus.SoulOutPlus.LOGGER.warn("createCamera blocked: camera entity = {}",
                    MC.getCameraEntity());
            if (Configs.Generic.CONFLICT_GUARD.getBooleanValue()) {
                player.sendSystemMessage(
                        Component.translatable("souloutplus.message.conflict_guard"));
            }
            return;
        }

        StatsCounter stats = player.getStats();
        ClientRecipeBook recipeBook = player.getRecipeBook();
        ClientLevel level = MC.level;

        camera = new CameraEntity(MC, level, MC.getConnection(), stats, recipeBook,
                net.minecraft.world.entity.player.Input.EMPTY, player.chatAbilities());
        originalCameraEntity = MC.getCameraEntity();
        originalCameraWasPlayer = originalCameraEntity == player;
        cullChunksOriginal = MC.smartCull;

        float yaw = player.getYRot();
        float pitch = player.getXRot();
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        if (Configs.Generic.REMEMBER_CAMERA_POS.getBooleanValue() && rememberedPos != null) {
            x = rememberedPos.x; y = rememberedPos.y; z = rememberedPos.z;
            yaw = rememberedYaw; pitch = rememberedPitch;
        }
        camera.snapTo(x, y, z, yaw, pitch);

        MC.setCameraEntity(camera);
        MC.smartCull = false;
        soulOut = true;

        com.souloutplus.SoulOutPlus.LOGGER.info("soul out: camera created at [{}, {}, {}] yaw={} pitch={}",
                x, y, z, yaw, pitch);
        player.sendOverlayMessage(Component.translatable("souloutplus.message.soul_out"));
    }

    private static void removeCamera() {
        if (MC.level != null && camera != null) {
            MC.setCameraEntity(originalCameraWasPlayer ? MC.player : originalCameraEntity);
            MC.smartCull = cullChunksOriginal;
            rememberedPos = new Vec3(camera.getX(), camera.getY(), camera.getZ());
            rememberedYaw = camera.getYRot();
            rememberedPitch = camera.getXRot();

            LocalPlayer player = MC.player;
            if (player != null) {
                player.setNoGravity(false);
                player.noPhysics = false;
                player.setOnGround(true);
                player.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
                player.fallDistance = 0;
            }
        }
        originalCameraEntity = null;
        camera = null;
        soulOut = false;
        if (MC.player != null) {
            MC.player.sendOverlayMessage(Component.translatable("souloutplus.message.soul_in"));
        }
    }
    public static void forceRemove() {
        if (isSoulOut()) {
            removeCamera();
        }
        rememberedPos = null;
    }
    public static void movementTick() {
        if (!isSoulOut() || camera == null || MC.player == null) return;

        var options = MC.options;
        double speed = Configs.Generic.CAMERA_SPEED.getDoubleValue() * 0.7;

        double forward = 0, strafe = 0, up = 0;
        if (options.keyUp.isDown()) forward += 1;
        if (options.keyDown.isDown()) forward -= 1;
        if (options.keyLeft.isDown()) strafe += 1;
        if (options.keyRight.isDown()) strafe -= 1;
        if (options.keyJump.isDown()) up += 1;
        if (options.keyShift.isDown()) up -= 1;
        boolean sprinting = options.keySprint.isDown();

        camera.updateLastTickPosition();
        double scaled = sprinting ? speed * 3 : speed;
        if (!Configs.Generic.CAMERA_INERTIA.getBooleanValue()) {
            camera.applyMotion(forward, up, strafe, scaled);
        } else {
            camera.applyMotion(forward * 0.6, up * 0.6, strafe * 0.6, scaled);
        }
    }
    public static void teleportToBody() {
        if (camera == null || MC.player == null) return;
        float yaw = MC.player.getYRot();
        float pitch = MC.player.getXRot();
        camera.snapTo(MC.player.getX(), MC.player.getY(), MC.player.getZ(), yaw, pitch);
    }
}