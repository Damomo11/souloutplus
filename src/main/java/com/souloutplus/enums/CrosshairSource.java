package com.souloutplus.enums;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;
public enum CrosshairSource implements IConfigOptionListEntry {
    SOUL("soul"),
    BODY("body"),
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
    public IConfigOptionListEntry fromString(String value) {
        for (CrosshairSource entry : values()) {
            if (entry.name.equals(value)) {
                return entry;
            }
        }
        return values()[0];
    }
}