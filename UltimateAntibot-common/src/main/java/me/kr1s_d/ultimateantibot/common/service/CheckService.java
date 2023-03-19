package me.kr1s_d.ultimateantibot.common.service;

import me.kr1s_d.ultimateantibot.common.checks.Check;
import me.kr1s_d.ultimateantibot.common.checks.CheckType;
import me.kr1s_d.ultimateantibot.common.utils.ServerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CheckService {
    private static final List<Check> checkList = new ArrayList<>();

    public static void register(Check check) {
        checkList.add(check);
    }

    public static List<String> getInformationAsMessage() {
        List<String> sender = new ArrayList<>();
        sender.add("&8&l&n___________________________________________");
        sender.add("");
        sender.add("&F&LRunning &c&lULTIMATE &F| &LANTIBOT ");
        sender.add("");
        for (Check check : checkList) {
            if(check.getCacheSize() == -1) continue;
            sender.add("&c" + check.getType().name().toLowerCase(Locale.ROOT) + " &f--> &c" + check.getCacheSize());
        }
        sender.add("&8&l&n___________________________________________");
        return sender.stream().map(ServerUtil::colorize).collect(Collectors.toList());
    }

    public static void removeCached(String ip) {
        for (Check che : checkList) {
            che.removeCache(ip);
        }
    }

    public static void clearCheckCache() {
        checkList.forEach(Check::clearCache);
    }

    public static <T extends Check> T getCheck(CheckType type, Class<T> clazz) {
        Check check = checkList.stream().filter(s -> s.getType().equals(type)).findAny().orElse(null);
        if(check == null) return null;
        return clazz.cast(check);
    }
}
