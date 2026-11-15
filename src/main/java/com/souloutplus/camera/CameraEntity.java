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

/**
 * 相机实体（从 Tweakeroo CameraEntity 移植到 mojmap 1.21.11）。
 * 继承 LocalPlayer 只为借用 ClientLevel / connection；永远不加入世界、不发任何包。
 *
 * 1.21.11 LocalPlayer 构造签名（已实测）：
 * LocalPlayer(Minecraft, ClientLevel, ClientPacketListener, StatsCounter, ClientRecipeBook, Input, boolean)
 */
public class CameraEntity extends LocalPlayer {
    public CameraEntity(Minecraft minecraft, ClientLevel level, ClientPacketListener connection,
                        StatsCounter stats, ClientRecipeBook recipeBook,
                        net.minecraft.world.entity.player.Input input) {
        // 1.21.11: (mc, level, connection, stats, recipeBook, input, printChat?) —— 布尔具体含义不重要，给 false
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
        // prev 系同步：鼠标转向是逐帧事件（不随 tick），
        // 渲染用 rotLerp(yRotO→yRot) 插值 —— 若只改 yRot，yRotO 每 tick 才追上，
        // 两帧间插值起点旧/终点新 → 转视角时锯齿。Tweakeroo 的做法就是两边一起设。
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

    /** 按 yaw/pitch 应用速度向量（由 CameraState.movementTick 调用） */
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
        // 1.21.11 渲染插值的"上tick值"三件套:
        // 位置 = lerp(xo→x)（Camera.setup → Entity.getPosition(partial)），
        // 朝向 = rotLerp(yRotO→yRot)。
        // 原版 xo/yo/zo 由 ClientLevel.tickNonPassenger 开头的 setOldPosAndRot() 维护，
        // 但我们的相机不进世界、没人 tick —— 必须自己每 tick 开头同步，
        // 否则移动时 xo 停留在创建瞬间、x 每tick变 → 渲染插值斜率突变 → 视角一颤一颤。
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

    /**
     * 直接瞬移相机到指定位置+朝向。
     * 注意：1.21.11 mojmap 里 Entity.snapTo(DDD) 内部会调 snapTo(DDDFF)（五参版）！
     * 我们这个五参版如果反过来调 this.snapTo(x,y,z)（三参版）就形成无限递归 → StackOverflow。
     * 所以这里直接调最底层的 absSnapTo + 位置设定，不走三参 snapTo。
     */
    public void snapTo(double x, double y, double z, float yaw, float pitch) {
        // absSnapTo 是真正只做"瞬移不触发位移逻辑"的底层方法；
        // 但它内部同样有 absSnapTo(DDDFF) 重载 —— 我们只调三参版，不会回到五参版
        this.absSnapTo(x, y, z);
        this.setCameraRotations(yaw, pitch);
    }
}
