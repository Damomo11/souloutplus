package com.souloutplus.compat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.souloutplus.SoulOutPlus;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
public final class MalilibCompat {
    private static Class<?> jsonUtils;
    private static Method parseJsonFile;
    private static Method writeJsonToFile;

    static {
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
                if (("parseJsonFile".equals(n) || "parseJsonFileAsPath".equals(n))
                        && m.getParameterCount() == 1 && parseJsonFile == null) {
                    parseJsonFile = m;
                }
                if (n.startsWith("writeJsonToFile") && m.getParameterCount() == 2 && writeJsonToFile == null) {
                    writeJsonToFile = m;
                }
            }
        }
    }

    private MalilibCompat() {
    }
    public static boolean available() {
        return parseJsonFile != null && writeJsonToFile != null;
    }
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
    private static Object adaptArg(Method m, int paramIdx, Path path) {
        int idx = paramIdx >= 0 ? paramIdx : 0;
        Class<?> pt = m.getParameterTypes()[idx];
        if (pt == File.class) {
            return path.toFile();
        }
        return path;
    }
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