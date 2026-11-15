package com.souloutplus.interaction;

import com.souloutplus.SoulOutPlus;
import com.souloutplus.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * 单人世界的交互距离属性提升（从打印机 ReachHelper 移植）。
 *
 * 服务端校验用 block_interaction_range / entity_interaction_range 属性（1.21+ 默认 4.5 / 3.0）。
 * 单人 + 开作弊时自动 /attribute 调高（上限 64），让出窍远程操作真的够得着。
 * 多人服务器不碰（没权限发指令只会刷聊天报错）。
 */
public final class ReachHelper {
    private static final Minecraft MC = Minecraft.getInstance();
    public static final int VANILLA_ATTRIBUTE_MAX = 64;
    private static int requestedValue = -1;
    private static int requestedEntityValue = -1;
    private static int requestedTick;
    private static LocalPlayer requestedFor;

    private ReachHelper() {
    }

    /** 每帧调（InitHandler 里挂） */
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
        // 只往上调，不往回调（退出出窍后保留调高的属性，避免配置来回抖动）
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
        // 没开作弊（不能用 /attribute）时什么都不做：发出去只会在聊天栏报错
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

    /**
     * 服务器（含单人内置服务端）实际会接受的距离上限。
     * RESPECT_SERVER_CAP 开着时：单人 = 属性当前值（调高后就是调高值）；
     * 多人 = 原版默认（拿不到服务端状态，按保守 4.5 算）。
     */
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
