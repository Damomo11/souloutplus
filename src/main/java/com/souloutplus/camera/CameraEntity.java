package com.souloutplus.camera;

import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.stats.StatsCounter;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;

public class CameraEntity extends LocalPlayer {
    public CameraEntity(Minecraft minecraft, ClientLevel level, ClientPacketListener connection,
                        StatsCounter stats, ClientRecipeBook recipeBook,
                        net.minecraft.world.entity.player.Input input) {
        super(minecraft, level, connection, stats, recipeBook, input, false);
        this.noPhysics = true;
    }

    @Override
    public boolean isSpectator() {
        return true;
    }

    public void setCameraRotations(float yaw, float pitch) {
        this.setYRot(yaw);
        this.setXRot(pitch);
        this.yRotO = yaw;
        this.xRotO = pitch;
        this.setYHeadRot(yaw);
        this.yHeadRotO = yaw;
        this.yBodyRot = yaw;
        this.yBodyRotO = yaw;
    }

    public void updateCameraRotations(float yawChange, float pitchChange) {
        float yaw = this.getYRot() + yawChange * 0.15F;
        float pitch = Mth.clamp(this.getXRot() + pitchChange * 0.15F, -90F, 90F);
        this.setCameraRotations(yaw, pitch);
    }
    public void applyMotion(double forward, double up, double strafe, double scale) {
        float yaw = this.getYRot();
        double xFactor = Math.sin(yaw * Math.PI / 180.0);
        double zFactor = Math.cos(yaw * Math.PI / 180.0);

        double x = (strafe * zFactor - forward * xFactor) * scale;
        double y = up * scale;
        double z = (forward * zFactor + strafe * xFactor) * scale;

        this.setDeltaMovement(new Vec3(x, y, z));
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    public void updateLastTickPosition() {
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();

        this.xOld = this.getX();
        this.yOld = this.getY();
        this.zOld = this.getZ();
        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
        this.yHeadRotO = this.yHeadRot;
        this.yBodyRotO = this.yBodyRot;
    }
    public void snapTo(double x, double y, double z, float yaw, float pitch) {
        this.absSnapTo(x, y, z);
        this.setCameraRotations(yaw, pitch);
    }
}