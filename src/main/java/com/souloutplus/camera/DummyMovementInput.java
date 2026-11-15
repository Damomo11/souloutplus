package com.souloutplus.camera;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.ClientInput;
import net.minecraft.world.entity.player.Input;
public class DummyMovementInput extends ClientInput {
    private final Minecraft mc = Minecraft.getInstance();

    @Override
    public void tick() {
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
    @Override
    public void makeJump() {
    }
}