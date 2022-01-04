package me.kr1s_d.ultimateantibot.common.utils;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IConfiguration;
import me.kr1s_d.ultimateantibot.common.objects.other.SlowJoinCheckConfiguration;

public class ConfigManger {
    public static double version;
    public static boolean isDebugModeOnline;
    public static int antiBotModeKeep;
    public static int antiBotModeTrigger;
    public static int pingModeKeep;
    public static int pingModeTrigger;
    public static boolean pingModeSendInfo;
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
    public static boolean isSuperJoinEnabled;
    public static int superJoinTime;
    public static int superJoinLimit;
    public static int authResetTime;
    public static int[] authMinMaxPing;
    public static int[] authMinMaxTimer;
    public static long authBetween;
    public static int authPercent;
    public static boolean authPingInterface;
    public static boolean authEnabled;
    private static SlowJoinCheckConfiguration packetSlowJoinCheckConfiguration;

    public static void init(IConfiguration cfg){
        version = cfg.getDouble("version");
        isDebugModeOnline = cfg.getBoolean("debug");
        antiBotModeKeep = cfg.getInt("antibotmode.keep");
        antiBotModeTrigger = cfg.getInt("antibotmode.trigger");
        pingModeKeep = cfg.getInt("pingmode.keep");
        pingModeTrigger = cfg.getInt("pingmode.trigger");
        pingModeSendInfo = cfg.getBoolean("pingmode.send_info");
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
        isSuperJoinEnabled = cfg.getBoolean("checks.superjoin.enabled");
        superJoinTime = cfg.getInt("checks.superjoin.time");
        superJoinLimit = cfg.getInt("checks.superjoin.limit");
        authResetTime = cfg.getInt("checks.auth.time");
        authMinMaxPing = Parser.toIntArray(Parser.toArray(cfg.getString("checks.auth.ping"), "-"));
        authMinMaxTimer = Parser.toIntArray(Parser.toArray(cfg.getString("checks.auth.ping"), "-"));
        authBetween = cfg.getLong("checks.auth.between");
        authPercent = cfg.getInt("checks.auth.percent");
        authPingInterface = cfg.getBoolean("checks.auth.ping_interface");
        authEnabled = cfg.getBoolean("checks.auth.enabled");
        packetSlowJoinCheckConfiguration = new SlowJoinCheckConfiguration(cfg, "checks.slowjoin.packet");
    }

    public static SlowJoinCheckConfiguration getPacketCheckConfiguration() {
        return packetSlowJoinCheckConfiguration;
    }
}
