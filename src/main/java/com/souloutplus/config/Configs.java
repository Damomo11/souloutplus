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

    public static class Hotkeys {
        public static final ConfigHotkey TOGGLE_SOUL = hotkey("toggleSoulOut").defaultStorageString("").build();
        public static final ConfigHotkey OPEN_CONFIG = hotkey("openConfigGui").defaultStorageString("").build();
        public static final ConfigHotkey RETURN_TO_BODY = hotkey("returnToBody").defaultStorageString("").build();
        public static final ConfigHotkey SNAP_AIM = hotkey("snapAim")
                .defaultStorageString("")
                .keybindSettings(KeybindSettings.PRESS_ALLOWEXTRA)
                .build();

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                TOGGLE_SOUL, OPEN_CONFIG, RETURN_TO_BODY, SNAP_AIM
        );
    }

    public static class Generic {
        public static final ConfigOptionList CROSSHAIR_SOURCE = optionList("crosshairSource")
                .defaultValue(CrosshairSource.SOUL).build();
        public static final ConfigDouble CAMERA_SPEED = doubleValue("cameraSpeed")
                .defaultValue(0.7).range(0.05, 10).build();
        public static final ConfigBoolean CAMERA_INERTIA = bool("cameraInertia")
                .defaultValue(true).build();
        public static final ConfigBoolean REMEMBER_CAMERA_POS = bool("rememberCameraPos")
                .defaultValue(false).build();
        public static final ConfigBoolean ENTITY_INTERACT = bool("entityInteractInSoul")
                .defaultValue(true).build();
        public static final ConfigBoolean CONFLICT_GUARD = bool("conflictGuardNotice")
                .defaultValue(true).build();

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                CROSSHAIR_SOURCE, CAMERA_SPEED, CAMERA_INERTIA, REMEMBER_CAMERA_POS,
                ENTITY_INTERACT, CONFLICT_GUARD
        );
    }

    public static class Reach {
        public static final ConfigDouble PLACE_RANGE = doubleValue("placeRange")
                .defaultValue(4.5).range(0.5, 64).build();
        public static final ConfigDouble BREAK_RANGE = doubleValue("breakRange")
                .defaultValue(4.5).range(0.5, 64).build();
        public static final ConfigDouble ENTITY_RANGE = doubleValue("entityRange")
                .defaultValue(3.0).range(0.5, 64).build();
        public static final ConfigBoolean AUTO_RAISE_REACH = bool("autoRaiseReachSingleplayer")
                .defaultValue(true).build();
        public static final ConfigBoolean RESPECT_SERVER_CAP = bool("respectServerCap")
                .defaultValue(true).build();
        public static final ConfigBoolean VANILLA_RATE_LIMIT = bool("vanillaRateLimit")
                .defaultValue(true).build();

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                PLACE_RANGE, BREAK_RANGE, ENTITY_RANGE, AUTO_RAISE_REACH,
                RESPECT_SERVER_CAP, VANILLA_RATE_LIMIT
        );
    }

    public static class Placement {
        public static final ConfigOptionList PLACEMENT_PROTOCOL = optionList("placementProtocol")
                .defaultValue(PlacementProtocol.AUTO).build();
        public static final ConfigBoolean USE_CLIENT_PREDICTION = bool("useClientPrediction")
                .defaultValue(true).build();
        public static final ConfigBoolean WAIT_SERVER_HEAD = bool("waitServerHead")
                .defaultValue(true).build();
        public static final ConfigBoolean SHIFT_PASSTHROUGH = bool("shiftStatePassthrough")
                .defaultValue(true).build();
        public static final ConfigBoolean REJECT_FLASH = bool("rejectedBlockFlash")
                .defaultValue(true).build();

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                PLACEMENT_PROTOCOL, USE_CLIENT_PREDICTION, WAIT_SERVER_HEAD,
                SHIFT_PASSTHROUGH, REJECT_FLASH
        );
    }

    public static class Render {
        public static final ConfigBoolean GHOST_PREVIEW = bool("ghostPreview")
                .defaultValue(true).build();
        public static final ConfigOptionList PREVIEW_STYLE = optionList("previewStyle")
                .defaultValue(PreviewStyle.ARROW).build();
        public static final ConfigBoolean REACH_HUD = bool("reachHudIndicator")
                .defaultValue(true).build();
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

    @Override
    public void load() {
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
    public static Minecraft client() {
        return Minecraft.getInstance();
    }
}