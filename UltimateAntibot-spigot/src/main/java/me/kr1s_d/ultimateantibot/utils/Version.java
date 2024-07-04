package me.kr1s_d.ultimateantibot.utils;

import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import org.bukkit.Bukkit;

public class Version {

    private static String versionStr;

    public static void init(IAntiBotPlugin plugin) {
        try {
            versionStr = Bukkit.getServer().getBukkitVersion();
        }catch (Exception ignored){

        }
    }


    public static int getBukkitServerVersion() {
        if (versionStr == null) return 0;
        if (versionStr.contains("1.7")) return 17;
        if (versionStr.contains("1.8")) return 18;
        if (versionStr.contains("1.9")) return 19;
        if (versionStr.contains("1.10")) return 110;
        if (versionStr.contains("1.11")) return 111;
        if (versionStr.contains("1.12")) return 112;
        if (versionStr.contains("1.13")) return 113;
        if (versionStr.contains("1.14")) return 114;
        if (versionStr.contains("1.15")) return 115;
        if (versionStr.contains("1.16")) return 116;
        if (versionStr.contains("1.17")) return 117;
        if (versionStr.contains("1.18")) return 118;
        if (versionStr.contains("1.19")) return 119;
        if (versionStr.contains("1.20")) return 120;
        if (versionStr.contains("1.21")) return 121;
        if (versionStr.contains("1.22")) return 122;
        if (versionStr.contains("1.23")) return 123;
        return 999;
    }

    public static String getVersionStr() {
        return versionStr;
    }

    public static boolean isLegacyServer() {
        return getBukkitServerVersion() <= 112;
    }
}
