package me.kr1s_d.ultimateantibot.common.utils;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IConfiguration;

public class MessageManager {
    public static double version;
    public static String prefix;
    public static String commandNoPerms;
    public static String commandCleared;
    public static String commandAdded;
    public static String commandRemove;
    public static String actionbarOffline;
    public static String actionbarAntiBotMode;
    public static String actionbarPackets;

    public static void init(IConfiguration messages){
        version = messages.getDouble("version");
        prefix = messages.getString("prefix");
    }
}
