package com.souloutplus;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

/**
 * I18n 键封装（从打印机 mod 移植的简化版）：
 * nameKey     = souloutplus.<key>              展示名
 * descKey     = souloutplus.<key>.desc         描述
 * configName  = souloutplus.config.<key>       配置界面里的名字
 * configDesc  = souloutplus.config.<key>.desc  配置界面里的描述
 */
public class I18n {
    private static final String PREFIX_CONFIG = "config";
    private static final String PREFIX_COMMENT = "desc";

    private final @Nullable String prefix;
    private final String nameKey;
    private final String withPrefixNameKey;
    private final String descKey;
    private final String configNameKey;
    private final String configDescKey;

    private I18n(@Nullable String prefix, String nameKey) {
        this.prefix = prefix;
        this.nameKey = nameKey;
        this.withPrefixNameKey = prefix == null ? nameKey : prefix + "." + nameKey;
        this.descKey = withPrefixNameKey + "." + PREFIX_COMMENT;
        String configNameKey = prefix == null ? PREFIX_CONFIG : prefix + "." + PREFIX_CONFIG;
        this.configNameKey = configNameKey + "." + nameKey;
        this.configDescKey = this.configNameKey + "." + PREFIX_COMMENT;
    }

    public static I18n of(@Nullable String prefix, String key) {
        return new I18n(prefix, key);
    }

    public static I18n of(String key) {
        return new I18n(SoulOutPlus.MOD_ID, key);
    }

    public String getWithPrefixNameKey() {
        return withPrefixNameKey;
    }

    public String getConfigNameKey() {
        return configNameKey;
    }

    public String getConfigDescKey() {
        return configDescKey;
    }

    public MutableComponent getName() {
        return Component.translatable(this.withPrefixNameKey);
    }

    public MutableComponent getName(Object... objects) {
        return Component.translatable(this.withPrefixNameKey, objects);
    }

    public MutableComponent getDesc() {
        return Component.translatable(this.descKey);
    }

    public MutableComponent getConfigName() {
        return Component.translatable(this.configNameKey);
    }

    public MutableComponent getConfigDesc() {
        return Component.translatable(this.configDescKey);
    }
}
