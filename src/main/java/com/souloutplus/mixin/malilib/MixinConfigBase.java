package com.souloutplus.mixin.malilib;

import com.souloutplus.extension.ConfigExtension;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigNotifiable;
import fi.dy.masa.malilib.config.IConfigResettable;
import fi.dy.masa.malilib.config.options.ConfigBase;
import fi.dy.masa.malilib.util.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BooleanSupplier;

/**
 * 对 malilib 配置基类扩展：I18n 翻译键 + 可见性（从打印机 mod 移植，仅保留本 mod 用到的部分）。
 */
@Mixin(value = ConfigBase.class, remap = false)
public abstract class MixinConfigBase<T extends IConfigBase> implements IConfigBase, IConfigResettable, IConfigNotifiable<T>, ConfigExtension {
    @Shadow
    protected String prettyName;

    @Shadow
    protected String comment;

    @Unique
    private String souloutplus$translateNameKey;

    @Unique
    private String souloutplus$translateCommentKey;

    @Unique
    private BooleanSupplier souloutplus$visible = () -> true;

    @Inject(method = "getPrettyName", at = @At("HEAD"), cancellable = true)
    public void souloutplus$getPrettyName(CallbackInfoReturnable<String> cir) {
        if (souloutplus$translateNameKey != null && !souloutplus$translateNameKey.isEmpty()) {
            cir.setReturnValue(StringUtils.getTranslatedOrFallback(souloutplus$translateNameKey, prettyName));
        }
    }

    @Inject(method = "getComment", at = @At("HEAD"), cancellable = true)
    public void souloutplus$getComment(CallbackInfoReturnable<String> cir) {
        @Nullable String comment = null;
        if (souloutplus$translateCommentKey != null && !souloutplus$translateCommentKey.isEmpty()) {
            comment = StringUtils.getTranslatedOrFallback(souloutplus$translateCommentKey, null);
        }
        if (comment == null && souloutplus$translateNameKey != null && !souloutplus$translateNameKey.isEmpty()) {
            comment = StringUtils.getTranslatedOrFallback(souloutplus$translateNameKey, null);
        }
        if (comment == null) {
            comment = this.comment;
        }
        if (comment != null) {
            cir.setReturnValue(comment);
        }
    }

    @Override
    public @Nullable String souloutplus$getTranslateNameKey() {
        return this.souloutplus$translateNameKey;
    }

    @Override
    public void souloutplus$setTranslateNameKey(String translateKey) {
        this.souloutplus$translateNameKey = translateKey;
    }

    @Override
    public @Nullable String souloutplus$getTranslateCommentKey() {
        return this.souloutplus$translateCommentKey;
    }

    @Override
    public void souloutplus$setTranslateCommentKey(String translateKey) {
        this.souloutplus$translateCommentKey = translateKey;
    }

    @Override
    public @Nullable BooleanSupplier souloutplus$getVisible() {
        return souloutplus$visible;
    }

    @Override
    public void souloutplus$setVisible(@Nullable BooleanSupplier visible) {
        this.souloutplus$visible = visible;
    }
}
