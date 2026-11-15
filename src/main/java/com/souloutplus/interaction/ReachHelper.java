package com.souloutplus.interaction;

import com.souloutplus.SoulOutPlus;
import com.souloutplus.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
public final class ReachHelper {
    private static final Minecraft MC = Minecraft.getInstance();
    public static final int VANILLA_ATTRIBUTE_MAX = 64;
    private static int requestedValue = -1;
    private static int requestedEntityValue = -1;
    private static int requestedTick;
    private static LocalPlayer requestedFor;

    private ReachHelper() {
    }
    public static void tick() {
        LocalPlayer player = MC.player;
        if (player == null || !MC.hasSingleplayerServer()) {
            reset();
            return;
        }
        if (!Configs.Reach.AUTO_RAISE_REACH.getBooleanValue()) {
            reset();
            return;
        }
        double wantedBlock = Configs.Reach.PLACE_RANGE.getDoubleValue();
        double wantedEntity = Configs.Reach.ENTITY_RANGE.getDoubleValue();
        if (player != requestedFor) {
            requestedFor = player;
            requestedValue = -1;
            requestedEntityValue = -1;
        }
        requestRaise(Attributes.BLOCK_INTERACTION_RANGE, (int) Math.ceil(wantedBlock), true);
        requestRaise(Attributes.ENTITY_INTERACTION_RANGE, (int) Math.ceil(wantedEntity), false);
    }

    private static void requestRaise(net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute,
                                     int target, boolean isBlockRange) {
        LocalPlayer player = MC.player;
        if (player == null) return;
        target = (int) Math.min(VANILLA_ATTRIBUTE_MAX, Math.max(target, 1));
        AttributeInstance attr = player.getAttribute(attribute);
        if (attr == null || attr.getBaseValue() >= target) return;
        int lastRequested = isBlockRange ? requestedValue : requestedEntityValue;
        if (lastRequested == target) return;
        if (player.connection.getCommands().getRoot().getChild("attribute") == null) return;
        if (isBlockRange) requestedValue = target; else requestedEntityValue = target;
        requestedTick = player.tickCount;
        String id = isBlockRange ? "minecraft:block_interaction_range" : "minecraft:entity_interaction_range";
        player.connection.sendCommand("attribute @s " + id + " base set " + target);
    }

    private static void reset() {
        requestedValue = -1;
        requestedEntityValue = -1;
        requestedFor = null;
    }
    public static double serverAllowance() {
        if (!Configs.Reach.RESPECT_SERVER_CAP.getBooleanValue()) {
            return VANILLA_ATTRIBUTE_MAX;
        }
        LocalPlayer player = MC.player;
        if (player != null && MC.hasSingleplayerServer()) {
            return player.blockInteractionRange();
        }
        return 4.5;
    }
}