package com.souloutplus.builder;

import com.souloutplus.I18n;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;

public class HotkeyConfigBuilder extends BaseConfigBuilder<ConfigHotkey, HotkeyConfigBuilder> {
    private String defaultStorageString = "";
    private KeybindSettings keybindSettings = KeybindSettings.DEFAULT;

    public HotkeyConfigBuilder(I18n i18n) {
        super(i18n);
    }

    public HotkeyConfigBuilder defaultStorageString(String storageString) {
        this.defaultStorageString = storageString;
        return this;
    }

    public HotkeyConfigBuilder keybindSettings(KeybindSettings settings) {
        this.keybindSettings = settings;
        return this;
    }

    @Override
    public ConfigHotkey build() {
        ConfigHotkey config = new ConfigHotkey(i18n.getConfigNameKey(), defaultStorageString, keybindSettings, i18n.getConfigDescKey());
        return buildExtension(config);
    }
}