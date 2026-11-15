package com.souloutplus.enums;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

/**
 * 精确放置协议（只在检测到 Servux / 单人内置服务端时生效）。
 */
public enum PlacementProtocol implements IConfigOptionListEntry {
    /** 自动：检测到 Servux 才用 V3，否则转头放置 */
    AUTO("auto"),
    /** 强制 V3（Servux 精确放置协议，状态编码进点击坐标） */
    V3("v3"),
    /** 强制 V2（Carpet accurateBlockPlacement 协议） */
    V2("v2"),
    /** 关闭协议，全部走转头放置 */
    OFF("off");

    private final String name;

    PlacementProtocol(String name) {
        this.name = name;
    }

    @Override
    public String getStringValue() {
        return this.name;
    }

    @Override
    public String getDisplayName() {
        return StringUtils.translate("souloutplus.enum.placement_protocol." + this.name);
    }

    @Override
    public IConfigOptionListEntry cycle(boolean forward) {
        int id = this.ordinal();
        int max = values().length - 1;
        return values()[forward ? (id < max ? id + 1 : 0) : (id > 0 ? id - 1 : max)];
    }

    @Override
    public IConfigOptionListEntry fromString(String value) {
        for (PlacementProtocol entry : values()) {
            if (entry.name.equals(value)) {
                return entry;
            }
        }
        return values()[0];
    }
}
