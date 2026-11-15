package com.souloutplus.camera;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.ClientInput;
import net.minecraft.world.entity.player.Input;

/**
 * 出窍时替换本体移动输入的哑输入（从 Tweakeroo DummyMovementInput 移植到 1.21.11 的 ClientInput/Input 体系）。
 *
 * 关键设计（灵魂出窍 Plus 的核心需求）：**保留 shift 的真实按键**。
 * 1.21.11 里客户端输入是 ClientInput.keyPresses: Input（record），
 * 服务端潜行状态由原版 tickMovement 从 keyPresses.shift() 同步。
 * shift+右键可交互方块（音符盒/活版门）= 放置，靠的是服务端看到"玩家潜行中" →
 * useWithoutItem 里 isShiftKeyDown() → PASS → 走到放置。
 * 所以这里每 tick 重置 keyPresses：移动/跳跃/疾跑全清零，shift 原样透传。
 */
public class DummyMovementInput extends ClientInput {
    private final Minecraft mc = Minecraft.getInstance();

    @Override
    public void tick() {
        // 只透传 shift；其余全 false。moveVector 留 (0,0)（父类字段默认零）
        this.keyPresses = new Input(
                false,  // forward
                false,  // backward
                false,  // left
                false,  // right
                false,  // jump
                mc.options.keyShift.isDown(),  // shift ← 真实按键
                false   // sprint
        );
        super.moveVector = net.minecraft.world.phys.Vec2.ZERO;
        super.tick();
    }

    @Override
    public boolean hasForwardImpulse() {
        return false;
    }

    @Override
    public net.minecraft.world.phys.Vec2 getMoveVector() {
        return net.minecraft.world.phys.Vec2.ZERO;
    }
}
