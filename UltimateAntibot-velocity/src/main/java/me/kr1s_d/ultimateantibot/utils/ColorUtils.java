package me.kr1s_d.ultimateantibot.utils;

import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {
    private static final Pattern hexColorPattern = Pattern.compile("#[a-fA-F0-9]{6}");

    private static final Map<String, String> colorMap = new HashMap<>();

    static {
        colorMap.put("&0", "<black>");
        colorMap.put("&1", "<dark_blue>");
        colorMap.put("&2", "<dark_green>");
        colorMap.put("&3", "<dark_aqua>");
        colorMap.put("&4", "<dark_red>");
        colorMap.put("&5", "<dark_purple>");
        colorMap.put("&6", "<gold>");
        colorMap.put("&7", "<gray>");
        colorMap.put("&8", "<dark_gray>");
        colorMap.put("&9", "<blue>");
        colorMap.put("&a", "<green>");
        colorMap.put("&b", "<aqua>");
        colorMap.put("&c", "<red>");
        colorMap.put("&d", "<light_purple>");
        colorMap.put("&e", "<yellow>");
        colorMap.put("&f", "<white>");
        colorMap.put("&k", "<obfuscated>");
        colorMap.put("&l", "<bold>");
        colorMap.put("&m", "<strikethrough>");
        colorMap.put("&n", "<underlined>");
        colorMap.put("&o", "<italic>");
        colorMap.put("&r", "<reset>");
        colorMap.put("&A", "<green>");
        colorMap.put("&B", "<aqua>");
        colorMap.put("&C", "<red>");
        colorMap.put("&D", "<light_purple>");
        colorMap.put("&E", "<yellow>");
        colorMap.put("&F", "<white>");
        colorMap.put("&K", "<obfuscated>");
        colorMap.put("&L", "<bold>");
        colorMap.put("&M", "<strikethrough>");
        colorMap.put("&N", "<underlined>");
        colorMap.put("&O", "<italic>");
        colorMap.put("&R", "<reset>");
    }

    public static Component format(String text) {
        for (Map.Entry<String, String> entry : colorMap.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }

        return Component.text(text);
    }

    public static  <T> T format(String text, Function<String, T> computeAs) {
        for (Map.Entry<String, String> entry : colorMap.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }


        return computeAs.apply(text);
    }
}
