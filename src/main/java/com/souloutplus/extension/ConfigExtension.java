package com.souloutplus.extension;

import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;
public interface ConfigExtension {
    @Nullable String souloutplus$getTranslateNameKey();

    void souloutplus$setTranslateNameKey(@Nullable String translateKey);

    @Nullable String souloutplus$getTranslateCommentKey();

    void souloutplus$setTranslateCommentKey(@Nullable String translateKey);

    @Nullable BooleanSupplier souloutplus$getVisible();

    void souloutplus$setVisible(@Nullable BooleanSupplier visible);
}