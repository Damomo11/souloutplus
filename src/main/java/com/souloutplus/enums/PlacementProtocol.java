package com.souloutplus.enums;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;
public enum PlacementProtocol implements IConfigOptionListEntry {
    AUTO("auto"),
    V3("v3"),
    V2("v2"),
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