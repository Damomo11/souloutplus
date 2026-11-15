package com.souloutplus.config;

import com.google.common.collect.ImmutableList;
import com.souloutplus.SoulOutPlus;
import com.souloutplus.config.Configs.Generic;
import com.souloutplus.config.Configs.Hotkeys;
import com.souloutplus.config.Configs.Placement;
import com.souloutplus.config.Configs.Reach;
import com.souloutplus.config.Configs.Render;
import com.souloutplus.mixin.extension.ConfigExtension;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;

/**
 * 配置界面（malilib GuiConfigsBase，Tweakeroo 同款：5 栏分页、hover 修改、单条重置）。
 */
public class ConfigUi extends GuiConfigsBase {
    private static Tab tab = Tab.GENERIC;

    public ConfigUi(@Nullable Screen parent) {
        super(10, 50, SoulOutPlus.MOD_ID, parent, SoulOutPlus.MOD_NAME + " v" + SouloulVersionSafe());
    }

    public ConfigUi() {
        this(Minecraft.getInstance().screen);
    }

    private static String SouloulVersionSafe() {
        return "0.1.0";
    }

    public static void refresh() {
        if (Minecraft.getInstance().screen instanceof ConfigUi gui) {
            gui.initGui();
        }
    }

    /** 分页按钮占几行（窗口窄时会换行，配置列表跟着往下移） */
    private int tabRows = 1;

    @Override
    public void initGui() {
        int rows = this.layoutTabs(false);
        if (rows != this.tabRows) {
            this.tabRows = rows;
            this.setListPosition(10, 50 + (rows - 1) * 22);
            this.reCreateListWidget();
        }
        super.initGui();
        this.clearOptions();
        this.layoutTabs(true);
    }

    @Override
    protected int getBrowserHeight() {
        return super.getBrowserHeight() - (this.tabRows - 1) * 22;
    }

    /** 排列分页按钮，返回占用的行数；create = false 时只计算不添加 */
    private int layoutTabs(boolean create) {
        int x = 10;
        int y = 26;
        int rows = 1;
        for (Tab tab : Tab.values()) {
            ButtonGeneric button = new ButtonGeneric(x, y, -1, 20, tab.getName(), tab.getComment());
            if (x > 10 && x + button.getWidth() > this.getScreenWidth() - 10) {
                x = 10;
                y += 22;
                rows++;
                button.setPosition(x, y);
            }
            if (create) {
                button.setEnabled(ConfigUi.tab != tab);
                this.addButton(button, new ButtonListener(tab, this));
            }
            x += button.getWidth() + 2;
        }
        return rows;
    }

    public void reset() {
        reCreateListWidget();
        Objects.requireNonNull(getListWidget()).resetScrollbarPosition();
        initGui();
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs() {
        ImmutableList.Builder<ConfigOptionWrapper> builder = ImmutableList.builder();
        for (IConfigBase config : ConfigUi.tab.getConfigs()) {
            if (config instanceof ConfigExtension extension) {
                @Nullable BooleanSupplier visible = extension.souloutplus$getVisible();
                if (visible != null && !visible.getAsBoolean()) {
                    continue;
                }
            }
            builder.add(new ConfigOptionWrapper(config));
        }
        return builder.build();
    }

    public enum Tab {
        GENERIC("generic"),
        REACH("reach"),
        PLACEMENT("placement"),
        RENDER("render"),
        HOTKEYS("hotkeys");

        private final String key;

        Tab(String key) {
            this.key = key;
        }

        public String getName() {
            return fi.dy.masa.malilib.util.StringUtils.translate("souloutplus.config.category." + key);
        }

        public String getComment() {
            return fi.dy.masa.malilib.util.StringUtils.translate("souloutplus.config.category." + key + ".desc");
        }

        public ImmutableList<IConfigBase> getConfigs() {
            return switch (this) {
                case GENERIC -> Generic.OPTIONS;
                case REACH -> Reach.OPTIONS;
                case PLACEMENT -> Placement.OPTIONS;
                case RENDER -> Render.OPTIONS;
                case HOTKEYS -> Hotkeys.OPTIONS;
            };
        }
    }

    public record ButtonListener(Tab tab, ConfigUi parent) implements IButtonActionListener {
        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            ConfigUi.tab = this.tab;
            this.parent.reset();
        }
    }
}
