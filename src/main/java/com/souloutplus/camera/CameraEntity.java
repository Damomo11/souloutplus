package com.souloutplus.camera;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

/**
 * 相机实体（从 Tweakeroo CameraEntity 移植到 mojmap 1.21.11）。
 * 继承 LocalPlayer 只为借用 ClientLevel / connection；永远不加入世界、不发包。
 */
public class CameraEntity extends LocalPlayer {
    public double motionX, motionY, motionZ;

    public CameraEntity(Minecraft minecraft, ClientLevel level, net.minecraft.client.multiplayer.ClientPacketListener connection,
                        net.minecraft.stats.StatsCounter stats, net.minecraft.client.recipebook.RecipeBook recipeBook) {
        super(minecraft, level, connection, stats, recipeBook, false, false, false);
        this.noPhysics = true;
    }

    @Override
    public boolean isSpectator() {
        return true;
    }

    /** 让相机不产生脚步声等副作用 */
    @Override
    public boolean isSilent() {
        return true;
    }

    public void setCameraRotations(float yaw, float pitch) {
        this.setYRot(yaw);
        this.setXRot(pitch);
        this.yRotO = yaw;
        this.xRotO = pitch;
        this.setYHeadRot(yaw);
        this.yBodyRot = yaw;
    }

    public void updateCameraRotations(float yawChange, float pitchChange) {
        float yaw = this.getYRot() + yawChange * 0.15F;
        float pitch = net.minecraft.util.Mth.clamp(this.getXRot() + pitchChange * 0.15F, -90F, 90F);
        this.setCameraRotations(yaw, pitch);
    }

    /** 由 CameraState.movementTick 调用：按自身 yaw/pitch 应用速度向量 */
    public void applyMotion(double forward, double up, double strafe, double scale) {
        float yaw = this.getYRot();
        double xFactor = Math.sin(yaw * Math.PI / 180.0);
        double zFactor = Math.cos(yaw * Math.PI / 180.0);

        double x = (strafe * zFactor - forward * xFactor) * scale;
        double y = up * scale;
        double z = (forward * zFactor + strafe * xFactor) * scale;

        this.setDeltaMovement(new Vec3(x, y, z));
        this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement());
    }

    public void updateLastTickPosition() {
        this.xOld = this.getX();
        this.yOld = this.getY();
        this.zOld = this.getZ();
        this.oBb = this.getBoundingBox();

        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
        this.yRotOHead = this.yHeadRot;
    }
}
