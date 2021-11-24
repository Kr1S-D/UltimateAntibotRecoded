package me.kr1s_d.ultimateantibot.common.utils;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IConfiguration;

public class ConfigManger {
    public static double version;
    public static double isDebugModeOnline;
    public static int antiBotModeKeep;
    public static int antiBotModeTrigger;
    public static int pingModeKeep;
    public static int pingModeTrigger;
    public static boolean isPingProtectionEnabled;
    public static int packetModeKeep;
    public static int packetModeTrigger;
    public static boolean blacklistInvalidProtocol;
    public static int playtimeForWhitelist;
    public static int taskManagerClearCache;
    public static int taskManagerAuth;
    public static int taskManagerClearPacket;
    public static int taskManagerClearRegister;
    public static boolean isFirstJoinEnabled;
    public static boolean isNameChangerEnabled;
    public static int nameChangerTime;
    public static int nameChangerLimit;

    public static void init(IConfiguration cfg){
        version = cfg.getDouble("version");
        isDebugModeOnline = cfg.getDouble("debug");
        antiBotModeKeep = cfg.getInt("antibotmode.keep");
        antiBotModeTrigger = cfg.getInt("antibotmode.trigger");
        pingModeKeep = cfg.getInt("pingmode.keep");
        pingModeTrigger = cfg.getInt("pingmode.trigger");
        isPingProtectionEnabled = cfg.getBoolean("pingmode.send_info");
        packetModeKeep = cfg.getInt("packetmode.keep");
        packetModeTrigger = cfg.getInt("packetmode.trigger");
        blacklistInvalidProtocol = cfg.getBoolean("packetmode.blacklist-invalid-protocol");
        playtimeForWhitelist = cfg.getInt("playtime_for_whitelist");
        taskManagerClearCache = cfg.getInt("taskmanager.clearcache");
        taskManagerAuth = cfg.getInt("taskmanager.auth");
        taskManagerClearPacket = cfg.getInt("taskmanager.packet");
        taskManagerClearRegister = cfg.getInt("taskmanager.register");
        isFirstJoinEnabled = cfg.getBoolean("checks.firstjoin.enabled");
        isNameChangerEnabled = cfg.getBoolean("checks.namechanger.enabled");
        nameChangerTime = cfg.getInt("checks.namechanger.time");
        nameChangerLimit = cfg.getInt("checks.namechanger.limit");
    }
}
