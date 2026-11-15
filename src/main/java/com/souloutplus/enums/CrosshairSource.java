package com.souloutplus.enums;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

/**
 * 交互源三态：出窍状态下准星射线和放置朝向的来源。
 */
public enum CrosshairSource implements IConfigOptionListEntry {
    /** 射线从相机（灵魂）眼睛打出，方块朝向用相机朝向 —— 本 mod 的核心模式 */
    SOUL("soul"),
    /** 射线从本体眼睛打出（等同 Tweakeroo 的 freeCameraPlayerInputs 效果） */
    BODY("body"),
    /** 出窍时屏蔽左右键（等同 Tweakeroo 默认行为） */
    OFF("off");

    private final String name;

    CrosshairSource(String name) {
        this.name = name;
    }

    @Override
    public String getStringValue() {
        return this.name;
    }

    @Override
    public String getDisplayName() {
        return StringUtils.translate("souloutplus.enum.crosshair_source." + this.name);
    }

    @Override
    public IConfigOptionListEntry cycle(boolean forward) {
        int id = this.ordinal();
        int max = values().length - 1;
        return values()[forward ? (id < max ? id + 1 : 0) : (id > 0 ? id - 1 : max)];
    }

    @Override
    public int getListOrdinal() {
        return this.ordinal();
    }
}
