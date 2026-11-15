package com.souloutplus.builder;

import fi.dy.masa.malilib.config.options.ConfigBoolean;
import com.souloutplus.I18n;

public class BooleanConfigBuilder extends BaseConfigBuilder<ConfigBoolean, BooleanConfigBuilder> {
    private boolean defaultValue = false;

    public BooleanConfigBuilder(I18n i18n) {
        super(i18n);
    }

    public BooleanConfigBuilder defaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    @Override
    public ConfigBoolean build() {
        ConfigBoolean config = new ConfigBoolean(i18n.getConfigNameKey(), defaultValue, i18n.getConfigDescKey());
        return buildExtension(config);
    }
}
