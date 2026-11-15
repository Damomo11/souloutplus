package com.souloutplus.camera;

import net.minecraft.client.player.Input;

/**
 * 出窍时替换本体移动输入的哑输入（从 Tweakeroo DummyMovementInput 移植）。
 *
 * 关键设计（灵魂出窍 Plus 的核心需求）：**保留 shift 的真实按键**。
 * 原版链路里 shift 同时是"潜行"和"交互修饰"：
 *  - tickMovement 里 setShiftKeyDown(sneaking) 决定服务端潜行状态 → 右键音符盒/活板门时
 *    useWithoutItem 检查 isShiftKeyDown() 决定"交互还是放置"；
 *  - 如果这里把 shift 也清零，出窍状态下 shift+右键放不了方块、反而会打开音符盒。
 * 所以：移动/跳跃/疾跑全部清零，唯独 sneaking 原样透传。
 */
public class DummyMovementInput extends Input {
    @Override
    public boolean hasForwardImpulse() {
        return false;
    }

    @Override
    public float getImpulse() {
        return 0;
    }

    @Override
    public boolean hasLeftImpulse() {
        return false;
    }

    @Override
    public boolean hasRightImpulse() {
        return false;
    }

    @Override
    public boolean isJumping() {
        return false;
    }

    @Override
    public boolean isShiftKeyDown() {
        // 真实按键透传：服务端潜行状态必须跟随玩家真实的 shift 键
        return net.minecraft.client.Minecraft.getInstance().options.keyShift.isDown();
    }
}
