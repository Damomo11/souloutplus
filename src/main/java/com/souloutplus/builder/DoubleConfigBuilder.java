package com.souloutplus.builder;

import com.souloutplus.I18n;
import fi.dy.masa.malilib.config.options.ConfigDouble;

public class DoubleConfigBuilder extends BaseConfigBuilder<ConfigDouble, DoubleConfigBuilder> {
    private double defaultValue = 0;
    private double min = 0;
    private double max = Double.MAX_VALUE;

    public DoubleConfigBuilder(I18n i18n) {
        super(i18n);
    }

    public DoubleConfigBuilder defaultValue(double defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public DoubleConfigBuilder range(double min, double max) {
        this.min = min;
        this.max = max;
        return this;
    }

    @Override
    public ConfigDouble build() {
        ConfigDouble config = new ConfigDouble(i18n.getConfigNameKey(), defaultValue, min, max, i18n.getConfigDescKey());
        return buildExtension(config);
    }
}
