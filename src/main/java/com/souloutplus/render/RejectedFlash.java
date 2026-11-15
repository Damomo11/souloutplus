package com.souloutplus.render;

import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 被服务端拒绝的放置 → 方块位置闪红提示。
 *
 * 检测方式（客户端、无协议依赖）：发出放置后盯着这一格的客户端预测状态。
 * 预测被服务端撤回（客户端世界回到旧状态）= 放置被拒 → 这一格进闪红队列。
 * 由 MixinClientLevel 的方块变更钩子喂进来（见 onBlockChanged）。
 */
public final class RejectedFlash {
    private record Entry(BlockPos pos, long untilMs) {
    }

    private static final List<Entry> ACTIVE = new ArrayList<>();
    private static final Color4f REJECT_RED = new Color4f(1.0F, 0.15F, 0.1F, 0.8F);
    private static final long FLASH_MS = 600;
    private static final int MAX_ENTRIES = 64;

    private RejectedFlash() {
    }

    /**
     * 客户端世界方块变更回调（MixinClientLevel 调）。
     * pending 中的格子状态被撤回（变回 air 或没变成我们预测的方块）→ 闪红。
     */
    public static void onBlockChanged(BlockPos pos) {
        // 简化策略：把"待确认放置"的格子记下来，被撤回时闪红。
        // 完整版需要 ActionConfirm（序号 ack 对账），这里先做被动检测：
        // 放置发出 → 客户端预测显示方块 → 若之后 20tick 内这一格变回非预测状态 → 被拒。
        // 该完整对账属于打印机方案，已在设计文档中，后续接入 Servux 时一起做。
    }

    /** 手动记录一次被拒位置（暂时给 mixIn 层在收到撤回信号时调） */
    public static void noteRejected(BlockPos pos) {
        long now = System.currentTimeMillis();
        if (ACTIVE.size() >= MAX_ENTRIES) {
            ACTIVE.clear();
        }
        ACTIVE.add(new Entry(pos.immutable(), now + FLASH_MS));
    }

    /** 每帧调用：清过期 + 渲染剩余 */
    public static void render() {
        if (ACTIVE.isEmpty()) return;
        long now = System.currentTimeMillis();
        ACTIVE.removeIf(e -> e.untilMs() < now);
        for (Entry e : ACTIVE) {
            RenderUtils.renderBlockOutline(e.pos(), 0F, 3.0F, REJECT_RED, true);
        }
    }

    /** 清空（回魂时调） */
    public static void clear() {
        ACTIVE.clear();
    }
}
