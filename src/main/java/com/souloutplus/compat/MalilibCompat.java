package com.souloutplus.compat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.souloutplus.SoulOutPlus;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;

/**
 * malilib 版本兼容层。
 *
 * 背景（2026-11-15 真机踩坑）：malilib 0.27.2（Modrinth 正式版）和 0.27.20（maven 开发版）
 * 的 JsonUtils 包路径不同 —— 0.27.2 在 `fi.dy.masa.malilib.util.JsonUtils`，
 * 0.27.20 在 `fi.dy.masa.malilib.util.data.json.JsonUtils`。
 * 编译期锚定任何一个都会在另一个版本上 NoClassDefFoundError →
 * 配置保存全炸（表现为：配置界面 ESC 卡死、配置不落盘、热键绑定丢失）。
 *
 * 方案：mod 内唯一的版本敏感类（JsonUtils）通过反射隔离层访问，
 * 运行时两个路径依次探测，方法名差异（File 版/Path 版/AsPath 版）也一并兼容。
 * 其余我们用到的 malilib 类已逐一反编译比对 0.27.2 和 0.27.20，
 * 包路径与方法签名完全一致（ConfigUtils/TickHandler/InitializationHandler/IRenderer/
 * RenderUtils.renderBlockOutline/StringUtils/ModInfo/KeybindSettings/GUI 系），不需要隔离。
 */
public final class MalilibCompat {
    private static Class<?> jsonUtils;
    private static Method parseJsonFile;
    private static Method writeJsonToFile;

    static {
        // 两个版本路径按优先级探测（都存在时无副作用，先命中的就是真包）
        for (String fqn : new String[]{
                "fi.dy.masa.malilib.util.data.json.JsonUtils",   // 0.27.20+
                "fi.dy.masa.malilib.util.JsonUtils",             // ≤0.27.2
        }) {
            try {
                Class<?> c = Class.forName(fqn);
                jsonUtils = c;
                break;
            } catch (ClassNotFoundException ignored) {
            }
        }
        if (jsonUtils != null) {
            for (Method m : jsonUtils.getMethods()) {
                String n = m.getName();
                // 读: parseJsonFile(File) / parseJsonFile(Path) / parseJsonFileAsPath(Path) 三种形态都兼容
                if (("parseJsonFile".equals(n) || "parseJsonFileAsPath".equals(n))
                        && m.getParameterCount() == 1 && parseJsonFile == null) {
                    parseJsonFile = m;
                }
                // 写: writeJsonToFile(JsonObject, File) / writeJsonToFile(JsonElement, Path) / writeJsonToFileAsPath(JsonObject, Path)
                if (n.startsWith("writeJsonToFile") && m.getParameterCount() == 2 && writeJsonToFile == null) {
                    writeJsonToFile = m;
                }
            }
        }
    }

    private MalilibCompat() {
    }

    /** malilib JsonUtils 是否可用（不可用时配置读写走 mod 内兜底实现） */
    public static boolean available() {
        return parseJsonFile != null && writeJsonToFile != null;
    }

    /** 读 JSON 文件；反射失败返回 null（调用方按无配置文件处理） */
    public static JsonElement parseJsonFile(Path path) {
        if (!available()) return null;
        try {
            Object arg = adaptArg(parseJsonFile, path);
            return (JsonElement) parseJsonFile.invoke(null, arg);
        } catch (Exception e) {
            SoulOutPlus.LOGGER.warn("malilib JsonUtils parseJsonFile failed, fallback to null", e);
            return null;
        }
    }

    /** 写 JSON 文件；反射失败走 mod 内直接实现 */
    public static boolean writeJsonToFile(JsonObject root, Path path) {
        if (available()) {
            try {
                Object arg = adaptArg(writeJsonToFile, 1, path);
                return Boolean.TRUE.equals(writeJsonToFile.invoke(null, root, arg));
            } catch (Exception e) {
                SoulOutPlus.LOGGER.warn("malilib JsonUtils writeJsonToFile failed, fallback to own impl", e);
            }
        }
        return writeJsonFileFallback(root, path);
    }

    private static Object adaptArg(Method m, Path path) {
        return adaptArg(m, -1, path);
    }

    /** 参数类型适配：File 版方法喂 File，Path 版喂 Path */
    private static Object adaptArg(Method m, int paramIdx, Path path) {
        int idx = paramIdx >= 0 ? paramIdx : 0;
        Class<?> pt = m.getParameterTypes()[idx];
        if (pt == File.class) {
            return path.toFile();
        }
        return path;
    }

    /** 兜底实现：malilib 完全不可用时用原版 Gson 直接写（格式与 malilib 输出一致） */
    private static boolean writeJsonFileFallback(JsonObject root, Path path) {
        try {
            java.nio.file.Files.createDirectories(path.getParent());
            java.io.Writer w = java.nio.file.Files.newBufferedWriter(path, java.nio.charset.StandardCharsets.UTF_8);
            try (w) {
                com.google.gson.Gson g = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
                w.write(g.toJson(root));
            }
            return true;
        } catch (Exception e) {
            SoulOutPlus.LOGGER.error("config write fallback failed", e);
            return false;
        }
    }
}
