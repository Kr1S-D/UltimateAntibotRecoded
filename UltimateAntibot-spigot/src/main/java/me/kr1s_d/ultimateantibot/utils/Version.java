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
        if (versionStr.startsWith("v1_7")) return 17;
        if (versionStr.startsWith("v1_8")) return 18;
        if (versionStr.startsWith("v1_9")) return 19;
        if (versionStr.startsWith("v1_10")) return 110;
        if (versionStr.startsWith("v1_11")) return 111;
        if (versionStr.startsWith("v1_12")) return 112;
        if (versionStr.startsWith("v1_13")) return 113;
        if (versionStr.startsWith("v1_14")) return 114;
        if (versionStr.startsWith("v1_15")) return 115;
        if (versionStr.startsWith("v1_16")) return 116;
        if (versionStr.startsWith("v1_17")) return 117;
        if (versionStr.startsWith("v1_18")) return 118;
        if (versionStr.startsWith("v1_19")) return 119;
        if (versionStr.startsWith("v1_20")) return 120;
        return 999;
    }

    public static String getVersionStr() {
        return versionStr;
    }

    public static boolean isLegacyServer() {
        return getBukkitServerVersion() <= 112;
    }
}
