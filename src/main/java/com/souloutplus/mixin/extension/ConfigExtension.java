package com.souloutplus.mixin.extension;

import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

/**
 * 注入到 malilib ConfigBase 的扩展接口（实现见 MixinConfigBase）。
 * 提供：翻译键覆盖（I18n）、可见性回调（配置界面按条件显示）。
 */
public interface ConfigExtension {
    @Nullable String souloutplus$getTranslateNameKey();

    void souloutplus$setTranslateNameKey(@Nullable String translateKey);

    @Nullable String souloutplus$getTranslateCommentKey();

    void souloutplus$setTranslateCommentKey(@Nullable String translateKey);

    @Nullable BooleanSupplier souloutplus$getVisible();

    void souloutplus$setVisible(@Nullable BooleanSupplier visible);
}
