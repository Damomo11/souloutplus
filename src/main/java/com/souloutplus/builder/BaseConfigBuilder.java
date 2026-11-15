package com.souloutplus.builder;

import com.souloutplus.I18n;
import com.souloutplus.extension.ConfigExtension;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBase;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

/**
 * 配置 builder 基类（从打印机 mod 移植的简化版）。
 */
@SuppressWarnings("unchecked")
public abstract class BaseConfigBuilder<T extends ConfigBase<?>, B extends BaseConfigBuilder<T, B>> {
    protected final I18n i18n;
    protected String nameKey;
    protected String descKey;
    protected @Nullable BooleanSupplier visible;

    public BaseConfigBuilder(I18n i18n) {
        this.i18n = i18n;
        this.nameKey = i18n.getConfigNameKey();
        this.descKey = i18n.getConfigDescKey();
        this.visible = null;
    }

    public BaseConfigBuilder(String translateKey) {
        this(I18n.of(translateKey));
    }

    public B setVisible(@Nullable BooleanSupplier visible) {
        this.visible = visible;
        return (B) this;
    }

    protected T buildExtension(T config) {
        ConfigExtension extension = (ConfigExtension) config;
        extension.souloutplus$setTranslateNameKey(this.nameKey);
        extension.souloutplus$setTranslateCommentKey(this.descKey);
        extension.souloutplus$setVisible(this.visible);
        return config;
    }

    public abstract T build();
}
