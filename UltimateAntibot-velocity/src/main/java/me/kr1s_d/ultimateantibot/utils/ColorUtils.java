package me.kr1s_d.ultimateantibot.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {

    private static final Map<String, String> colorMap = new HashMap<>();

    static {
        colorMap.put("0", "<black>");
        colorMap.put("1", "<dark_blue>");
        colorMap.put("2", "<dark_green>");
        colorMap.put("3", "<dark_aqua>");
        colorMap.put("4", "<dark_red>");
        colorMap.put("5", "<dark_purple>");
        colorMap.put("6", "<gold>");
        colorMap.put("7", "<gray>");
        colorMap.put("8", "<dark_gray>");
        colorMap.put("9", "<blue>");
        colorMap.put("a", "<green>");
        colorMap.put("b", "<aqua>");
        colorMap.put("c", "<red>");
        colorMap.put("d", "<light_purple>");
        colorMap.put("e", "<yellow>");
        colorMap.put("f", "<white>");
        colorMap.put("k", "<obfuscated>");
        colorMap.put("l", "<bold>");
        colorMap.put("m", "<strikethrough>");
        colorMap.put("n", "<underlined>");
        colorMap.put("o", "<italic>");
        colorMap.put("r", "<reset>");
        colorMap.put("A", "<green>");
        colorMap.put("B", "<aqua>");
        colorMap.put("C", "<red>");
        colorMap.put("D", "<light_purple>");
        colorMap.put("E", "<yellow>");
        colorMap.put("F", "<white>");
        colorMap.put("K", "<obfuscated>");
        colorMap.put("L", "<bold>");
        colorMap.put("M", "<strikethrough>");
        colorMap.put("N", "<underlined>");
        colorMap.put("O", "<italic>");
        colorMap.put("R", "<reset>");
    }

    public static Component format(String text) {
        return deserialize(replaceLegacy(text));
    }

    public static String replaceLegacy(String text) {
        for (Map.Entry<String, String> entry : colorMap.entrySet()) {
            text = text.replace("&" + entry.getKey(), entry.getValue());
            text = text.replace("§" + entry.getKey(), entry.getValue());
        }

        return text;
    }

    public static String replaceOlderLegacy(String string) {
        return string.replace("&", "§");
    }

    public static String replaceSerialize(String text) {
        return MiniMessage.miniMessage().serialize(format(replaceLegacy(text)));
    }

    public static Component deserialize(String text) {
        return MiniMessage.miniMessage().deserialize(text);
    }

    public static String removeFormatting(String text) {
        for (Map.Entry<String, String> entry : colorMap.entrySet()) {
            text = text.replace("&" + entry.getKey(), "");
            text = text.replace("§" + entry.getKey(), "");
        }

        for (String value : colorMap.values()) {
            text = text.replace("</" + value.replace("<", "").replace("</", "").replace(">", "") + ">","");
            text = text.replace(value, "");
        }

        return text;
    }
}
