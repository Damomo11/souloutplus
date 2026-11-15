package com.souloutplus.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.souloutplus.I18n;
import com.souloutplus.SoulOutPlus;
import com.souloutplus.builder.BooleanConfigBuilder;
import com.souloutplus.builder.DoubleConfigBuilder;
import com.souloutplus.builder.HotkeyConfigBuilder;
import com.souloutplus.builder.OptionListConfigBuilder;
import com.souloutplus.enums.CrosshairSource;
import com.souloutplus.enums.PlacementProtocol;
import com.souloutplus.enums.PreviewStyle;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static com.souloutplus.builder.Builders.*;

public class Configs implements IConfigHandler {
    private static final Configs INSTANCE = new Configs();

    private static final String FILE_PATH = "./config/souloutplus.json";
    private static final File CONFIG_DIR = new File("./config");

    public static final ImmutableList<IConfigBase> OPTIONS;
    public static final ImmutableList<IHotkey> HOTKEYS;

    // ---------------- 热键 ----------------
    public static class Hotkeys {
        /** 出窍开关（默认无绑定） */
        public static final ConfigHotkey TOGGLE_SOUL = hotkey("toggleSoulOut").defaultStorageString("").build();
        /** 打开配置界面（默认无绑定） */
        public static final ConfigHotkey OPEN_CONFIG = hotkey("openConfigGui").defaultStorageString("").build();
        /** 相机瞬移回本体头顶 */
        public static final ConfigHotkey RETURN_TO_BODY = hotkey("returnToBody").defaultStorageString("").build();
        /** 按住时相机转向吸附到最近的六向 */
        public static final ConfigHotkey SNAP_AIM = hotkey("snapAim")
                .defaultStorageString("")
                .keybindSettings(KeybindSettings.PRESS_ALLOWEXTRA)
                .build();

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                TOGGLE_SOUL, OPEN_CONFIG, RETURN_TO_BODY, SNAP_AIM
        );
    }

    // ---------------- 通用 ----------------
    public static class Generic {
        /** 交互源三态 */
        public static final ConfigOptionList CROSSHAIR_SOURCE = optionList("crosshairSource")
                .defaultValue(CrosshairSource.SOUL).build();
        /** 出窍飞行速度 */
        public static final ConfigDouble CAMERA_SPEED = doubleValue("cameraSpeed")
                .defaultValue(0.7).range(0.05, 10).build();
        /** 移动惯性 */
        public static final ConfigBoolean CAMERA_INERTIA = bool("cameraInertia")
                .defaultValue(true).build();
        /** 跨出窍会话记住相机位置 */
        public static final ConfigBoolean REMEMBER_CAMERA_POS = bool("rememberCameraPos")
                .defaultValue(true).build();
        /** 出窍时实体交互/攻击也走相机射线 */
        public static final ConfigBoolean ENTITY_INTERACT = bool("entityInteractInSoul")
                .defaultValue(true).build();
        /** 冲突守卫提示（有其它 mod 占用相机时拒绝出窍并提示） */
        public static final ConfigBoolean CONFLICT_GUARD = bool("conflictGuardNotice")
                .defaultValue(true).build();

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                CROSSHAIR_SOURCE, CAMERA_SPEED, CAMERA_INERTIA, REMEMBER_CAMERA_POS,
                ENTITY_INTERACT, CONFLICT_GUARD
        );
    }

    // ---------------- 距离 ----------------
    public static class Reach {
        /** 放置/交互距离（客户端判定） */
        public static final ConfigDouble PLACE_RANGE = doubleValue("placeRange")
                .defaultValue(4.5).range(0.5, 64).build();
        /** 挖掘距离（客户端判定，独立于放置） */
        public static final ConfigDouble BREAK_RANGE = doubleValue("breakRange")
                .defaultValue(4.5).range(0.5, 64).build();
        /** 实体攻击距离 */
        public static final ConfigDouble ENTITY_RANGE = doubleValue("entityRange")
                .defaultValue(3.0).range(0.5, 64).build();
        /** 单人+作弊：自动 /attribute 调高 block_interaction_range（和 entity_interaction_range，上限 64） */
        public static final ConfigBoolean AUTO_RAISE_REACH = bool("autoRaiseReachSingleplayer")
                .defaultValue(true).build();
        /** 服务器上限夹紧：无 Servux/地毯时把所有判定夹回原版可达范围 */
        public static final ConfigBoolean RESPECT_SERVER_CAP = bool("respectServerCap")
                .defaultValue(true).build();
        /** 原版频率限制：每 tick 最多 1 次放置 + 1 次挖掘 */
        public static final ConfigBoolean VANILLA_RATE_LIMIT = bool("vanillaRateLimit")
                .defaultValue(true).build();

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                PLACE_RANGE, BREAK_RANGE, ENTITY_RANGE, AUTO_RAISE_REACH,
                RESPECT_SERVER_CAP, VANILLA_RATE_LIMIT
        );
    }

    // ---------------- 放置与挖掘 ----------------
    public static class Placement {
        /** 精确放置协议 */
        public static final ConfigOptionList PLACEMENT_PROTOCOL = optionList("placementProtocol")
                .defaultValue(PlacementProtocol.AUTO).build();
        /** 优先走原版本地预测路径 */
        public static final ConfigBoolean USE_CLIENT_PREDICTION = bool("useClientPrediction")
                .defaultValue(true).build();
        /** 朝向敏感方块等服务端头部确认 */
        public static final ConfigBoolean WAIT_SERVER_HEAD = bool("waitServerHead")
                .defaultValue(true).build();
        /** shift 状态透传（保证服务端潜行状态=玩家真实按键） */
        public static final ConfigBoolean SHIFT_PASSTHROUGH = bool("shiftStatePassthrough")
                .defaultValue(true).build();
        /** 被服务端拒绝的方块红闪提示 */
        public static final ConfigBoolean REJECT_FLASH = bool("rejectedBlockFlash")
                .defaultValue(true).build();

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                PLACEMENT_PROTOCOL, USE_CLIENT_PREDICTION, WAIT_SERVER_HEAD,
                SHIFT_PASSTHROUGH, REJECT_FLASH
        );
    }

    // ---------------- 渲染 ----------------
    public static class Render {
        /** 朝向幽灵预览 */
        public static final ConfigBoolean GHOST_PREVIEW = bool("ghostPreview")
                .defaultValue(true).build();
        /** 预览样式 */
        public static final ConfigOptionList PREVIEW_STYLE = optionList("previewStyle")
                .defaultValue(PreviewStyle.ARROW).build();
        /** 准星可达性指示（绿/黄/红） */
        public static final ConfigBoolean REACH_HUD = bool("reachHudIndicator")
                .defaultValue(true).build();
        /** 出窍时显示本体 */
        public static final ConfigBoolean SHOW_BODY = bool("showBodyInSoul")
                .defaultValue(true).build();

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                GHOST_PREVIEW, PREVIEW_STYLE, REACH_HUD, SHOW_BODY
        );
    }

    static {
        LinkedHashSet<IConfigBase> optionSet = new LinkedHashSet<>();
        optionSet.addAll(Generic.OPTIONS);
        optionSet.addAll(Reach.OPTIONS);
        optionSet.addAll(Placement.OPTIONS);
        optionSet.addAll(Render.OPTIONS);
        optionSet.addAll(Hotkeys.OPTIONS);
        OPTIONS = ImmutableList.copyOf(optionSet);

        List<IHotkey> hotkeys = new ArrayList<>();
        for (IConfigBase option : optionSet) {
            if (option instanceof IHotkey hotkey) {
                hotkeys.add(hotkey);
            }
        }
        HOTKEYS = ImmutableList.copyOf(hotkeys);
    }

    // ---------------- 读写盘 ----------------
    @Override
    public void load() {
        // JsonUtils 经 MalilibCompat 反射隔离（0.27.2 与 0.27.20 包路径不同，直接引用会在另一版本 NoClassDefFoundError）
        JsonElement jsonElement = com.souloutplus.compat.MalilibCompat.parseJsonFile(new File(FILE_PATH).toPath());
        if (jsonElement != null && jsonElement.isJsonObject()) {
            JsonObject obj = jsonElement.getAsJsonObject();
            fi.dy.masa.malilib.config.ConfigUtils.readConfigBase(obj, SoulOutPlus.MOD_ID, OPTIONS);
        }
    }

    @Override
    public void save() {
        if ((CONFIG_DIR.exists() && CONFIG_DIR.isDirectory()) || CONFIG_DIR.mkdirs()) {
            JsonObject configRoot = new JsonObject();
            fi.dy.masa.malilib.config.ConfigUtils.writeConfigBase(configRoot, SoulOutPlus.MOD_ID, OPTIONS);
            com.souloutplus.compat.MalilibCompat.writeJsonToFile(configRoot, new File(FILE_PATH).toPath());
        }
    }

    private static boolean inited = false;

    public static void init() {
        if (inited) {
            return;  // 幂等：entrypoint + malilib InitializationHandler 都可能触达
        }
        inited = true;
        Configs.INSTANCE.load();
        ConfigManager.getInstance().registerConfigHandler(SoulOutPlus.MOD_ID, Configs.INSTANCE);
        InputEventHandler.getKeybindManager().registerKeybindProvider(InputHandler.getInstance());
        InputEventHandler.getInputManager().registerKeyboardInputHandler(InputHandler.getInstance());
        fi.dy.masa.malilib.registry.Registry.CONFIG_SCREEN.registerConfigScreenFactory(
                new fi.dy.masa.malilib.util.data.ModInfo(SoulOutPlus.MOD_ID, SoulOutPlus.MOD_NAME, ConfigUi::new)
        );
        HotkeysCallback.initCallbacks();
    }

    /** 调试用 */
    public static Minecraft client() {
        return Minecraft.getInstance();
    }
}
