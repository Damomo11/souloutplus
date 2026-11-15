package com.souloutplus.enums;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;
public enum PreviewStyle implements IConfigOptionListEntry {
    WIREFRAME("wireframe"),
    TRANSLUCENT("translucent"),
    ARROW("arrow");

    private final String name;

    PreviewStyle(String name) {
        this.name = name;
    }

    @Override
    public String getStringValue() {
        return this.name;
    }

    @Override
    public String getDisplayName() {
        return StringUtils.translate("souloutplus.enum.preview_style." + this.name);
    }

    @Override
    public IConfigOptionListEntry cycle(boolean forward) {
        int id = this.ordinal();
        int max = values().length - 1;
        return values()[forward ? (id < max ? id + 1 : 0) : (id > 0 ? id - 1 : max)];
    }

    @Override
    public IConfigOptionListEntry fromString(String value) {
        for (PreviewStyle entry : values()) {
            if (entry.name.equals(value)) {
                return entry;
            }
        }
        return values()[0];
    }
}