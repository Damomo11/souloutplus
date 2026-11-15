package com.souloutplus.builder;

import com.souloutplus.I18n;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;

public class OptionListConfigBuilder extends BaseConfigBuilder<ConfigOptionList, OptionListConfigBuilder> {
    private IConfigOptionListEntry defaultValue;

    public OptionListConfigBuilder(I18n i18n) {
        super(i18n);
    }

    public OptionListConfigBuilder defaultValue(IConfigOptionListEntry defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    @Override
    public ConfigOptionList build() {
        ConfigOptionList config = new ConfigOptionList(i18n.getConfigNameKey(), defaultValue, i18n.getConfigDescKey());
        return buildExtension(config);
    }
}