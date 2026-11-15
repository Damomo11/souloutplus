package com.souloutplus.builder;

import com.souloutplus.I18n;

/**
 * builder 语法糖（builder 类都在本包，Configs 静态导入用）。
 */
public class Builders {
    public static HotkeyConfigBuilder hotkey(String key) {
        return new HotkeyConfigBuilder(I18n.of(key));
    }

    public static BooleanConfigBuilder bool(String key) {
        return new BooleanConfigBuilder(I18n.of(key));
    }

    public static DoubleConfigBuilder doubleValue(String key) {
        return new DoubleConfigBuilder(I18n.of(key));
    }

    public static OptionListConfigBuilder optionList(String key) {
        return new OptionListConfigBuilder(I18n.of(key));
    }
}
