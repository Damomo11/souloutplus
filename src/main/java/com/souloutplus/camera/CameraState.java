package com.souloutplus.camera;

import com.souloutplus.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

/**
 * 相机生命周期管理（出窍/回魂）——从 Tweakeroo CameraEntity/CameraUtils 的状态管理移植，
 * mojmap 1.21.11。相机实体本身见 {@link CameraEntity}；
 * 每帧的移动/转向喂入见 mixin（MixinEntity / MixinLocalPlayer）。
 */
public final class CameraState {
    private static final Minecraft MC = Minecraft.getInstance();

    private static CameraEntity camera;
    private static net.minecraft.world.entity.Entity originalCameraEntity;
    private static boolean originalCameraWasPlayer;
    private static boolean cullChunksOriginal;

    /** 记住的出窍位置（跨出窍会话），仅 REMEMBER_CAMERA_POS 开着时用 */
    private static Vec3 rememberedPos;
    private static float rememberedYaw, rememberedPitch;

    /** 单人自动调距的 helper tick（ReachHelper 自己每帧调） */
    private static boolean soulOut;

    private CameraState() {
    }

    // ---------------- 生命周期 ----------------

    public static void init() {
        // 注册每 tick 的相机移动逻辑：挂在 LocalPlayer.tick 的 mixin 里调用 movementTick
    }

    public static boolean isSoulOut() {
        return soulOut;
    }

    public static CameraEntity getCamera() {
        return camera;
    }

    /** 出窍前本体是否就是相机（用于回魂时恢复） */
    public static boolean originalCameraWasPlayer() {
        return originalCameraWasPlayer;
    }

    public static void toggle() {
        if (isSoulOut()) {
            removeCamera();
        } else {
            createCamera();
        }
    }

    private static void createCamera() {
        if (MC.player == null || MC.level == null) return;

        // 冲突守卫：其它 mod 已经占着相机实体时拒绝出窍（零干预，只提示）
        if (MC.getCameraEntity() != MC.player) {
            if (Configs.Generic.CONFLICT_GUARD.getBooleanValue()) {
                MC.player.displayClientMessage(
                        Component.translatable("souloutplus.message.conflict_guard"), false);
            }
            return;
        }

        camera = new CameraEntity(MC, MC.level, MC.getConnection(),
                MC.player.getStats(), MC.player.getRecipeBook());
        originalCameraEntity = MC.getCameraEntity();
        originalCameraWasPlayer = originalCameraEntity == MC.player;
        cullChunksOriginal = MC.smartCull;

        // 出窍位置：记住的位置优先，否则从本体头顶出发
        float yaw = MC.player.getYRot();
        float pitch = MC.player.getXRot();
        double x = MC.player.getX();
        double y = MC.player.getEyeY();
        double z = MC.player.getZ();
        if (Configs.Generic.REMEMBER_CAMERA_POS.getBooleanValue() && rememberedPos != null) {
            x = rememberedPos.x; y = rememberedPos.y; z = rememberedPos.z;
            yaw = rememberedYaw; pitch = rememberedPitch;
        }
        camera.snapTo(x, y, z, yaw, pitch);
        camera.setCameraRotations(yaw, pitch);

        MC.setCameraEntity(camera);
        MC.smartCull = false;
        soulOut = true;
    }

    private static void removeCamera() {
        if (MC.level != null && camera != null) {
            // 回魂：恢复原相机实体（通常是玩家），恢复区块剔除
            MC.setCameraEntity(originalCameraWasPlayer ? MC.player : originalCameraEntity);
            MC.smartCull = cullChunksOriginal;
            rememberedPos = new Vec3(camera.getX(), camera.getY(), camera.getZ());
            rememberedYaw = camera.getYRot();
            rememberedPitch = camera.getXRot();
        }
        originalCameraEntity = null;
        camera = null;
        soulOut = false;
    }

    /** 世界卸载时强制回魂（WorldLoadListener 场景） */
    public static void forceRemove() {
        if (isSoulOut()) {
            removeCamera();
        }
        rememberedPos = null;
    }

    // ---------------- 每 tick ----------------

    /**
     * 每 client tick 调（由 MixinLocalPlayer 在 LocalPlayer.tick HEAD 注入）：
     * 出窍时按键盘状态移动相机。本体输入由 MixinLocalPlayer 换成 DummyMovementInput（保留 shift 真实按键）。
     */
    public static void movementTick() {
        if (!isSoulOut() || camera == null || MC.player == null) return;

        var options = MC.options;
        double speed = Configs.Generic.CAMERA_SPEED.getDoubleValue() * 10.0;

        // 惯性衰减（从 Tweakeroo calculatePlayerMotionWithDeceleration 移植）
        double decelFactor = Configs.Generic.CAMERA_INERTIA.getBooleanValue() ? 0.4 : 1.0;

        double forward = 0, strafe = 0, up = 0;
        if (options.keyUp.isDown()) forward += 1;
        if (options.keyDown.isDown()) forward -= 1;
        if (options.keyLeft.isDown()) strafe -= 1;
        if (options.keyRight.isDown()) strafe += 1;
        if (options.keyJump.isDown()) up += 1;
        if (options.keyShift.isDown()) up -= 1;
        boolean sprinting = options.keySprint.isDown();

        camera.updateLastTickPosition();
        double scaled = sprinting ? speed * 3 : speed;
        camera.applyMotion(forward * decelFactor, up * decelFactor, strafe * decelFactor, scaled);
    }

    /** 相机瞬移回本体头顶（热键） */
    public static void teleportToBody() {
        if (camera == null || MC.player == null) return;
        float yaw = MC.player.getYRot();
        float pitch = MC.player.getXRot();
        camera.snapTo(MC.player.getX(), MC.player.getEyeY(), MC.player.getZ(), yaw, pitch);
        camera.setCameraRotations(yaw, pitch);
    }
}
