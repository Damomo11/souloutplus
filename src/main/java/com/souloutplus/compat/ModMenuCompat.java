package com.souloutplus.compat;

import com.souloutplus.config.ConfigUi;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * ModMenu 集成：模组菜单里打开本 mod 的配置界面。
 * ModMenu 是编译期可选依赖（modCompileOnly），运行时没装也能正常进游戏。
 */
public class ModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigUi::new;
    }
}
