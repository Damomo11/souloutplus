package com.souloutplus.render;

import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.List;
public final class RejectedFlash {
    private record Entry(BlockPos pos, long untilMs) {
    }

    private static final List<Entry> ACTIVE = new ArrayList<>();
    private static final Color4f REJECT_RED = new Color4f(1.0F, 0.15F, 0.1F, 0.8F);
    private static final long FLASH_MS = 600;
    private static final int MAX_ENTRIES = 64;

    private RejectedFlash() {
    }
    public static void onBlockChanged(BlockPos pos) {
    }
    public static void noteRejected(BlockPos pos) {
        long now = System.currentTimeMillis();
        if (ACTIVE.size() >= MAX_ENTRIES) {
            ACTIVE.clear();
        }
        ACTIVE.add(new Entry(pos.immutable(), now + FLASH_MS));
    }
    public static void render() {
        if (ACTIVE.isEmpty()) return;
        long now = System.currentTimeMillis();
        ACTIVE.removeIf(e -> e.untilMs() < now);
        for (Entry e : ACTIVE) {
            RenderUtils.renderBlockOutline(e.pos(), 0F, 3.0F, REJECT_RED, true);
        }
    }
    public static void clear() {
        ACTIVE.clear();
    }
}