package me.kr1s_d.ultimateantibot.common.utils;

import me.kr1s_d.ultimateantibot.common.objects.config.ProxyCheckConfig;
import me.kr1s_d.ultimateantibot.common.IConfiguration;
import me.kr1s_d.ultimateantibot.common.objects.config.SlowJoinCheckConfiguration;

import java.util.SplittableRandom;

public class ConfigManger {

    private static IConfiguration CONFIG;

    public static double version;
    public static boolean isDebugModeOnline;
    public static boolean detectServerPerformance;
    public static boolean enableLatencyThread;
    public static boolean enableBossBarAutomaticNotification;
    public static boolean isConsoleAttackMessageDisabled;
    public static int antiBotModeKeep;
    public static int antiBotModeTrigger;
    public static long slowAntibotModeKeep;
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
    public static int authMaxFails;
    public static boolean authPingInterface;
    public static long taskManagerUpdate;
    public static boolean isIPApiVerificationEnabled;
    private static SlowJoinCheckConfiguration packetSlowJoinCheckConfiguration;
    private static SlowJoinCheckConfiguration similarNameCheck;
    private static SlowJoinCheckConfiguration lenghtCheck;
    private static SlowJoinCheckConfiguration accountCheck;
    private static ProxyCheckConfig proxyCheckConfig;

    public static void init(IConfiguration cfg){
        CONFIG = cfg;

        version = cfg.getDouble("version");
        isDebugModeOnline = cfg.getBoolean("debug");
        enableLatencyThread = cfg.getBoolean("enable-latency-thread");
        enableBossBarAutomaticNotification = cfg.getBoolean("enable-bossbar-automatic-notification");
        isIPApiVerificationEnabled = cfg.getBoolean("ip-api.enabled");
        isConsoleAttackMessageDisabled = cfg.getBoolean("disable-console-attack-message");
        detectServerPerformance = cfg.getBoolean("detect-server-performance");
        antiBotModeKeep = cfg.getInt("antibotmode.keep");
        antiBotModeTrigger = cfg.getInt("antibotmode.trigger");
        slowAntibotModeKeep = cfg.getInt("antibotmode.keep-slow");
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
        authMaxFails = cfg.getInt("checks.auth.maxfails");
        authPingInterface = cfg.getBoolean("checks.auth.ping_interface");
        taskManagerUpdate = cfg.getLong("taskmanager.update");
        packetSlowJoinCheckConfiguration = new SlowJoinCheckConfiguration(cfg, "checks.slowjoin.packet");
        similarNameCheck = new SlowJoinCheckConfiguration(cfg, "checks.slowjoin.similar");
        lenghtCheck = new SlowJoinCheckConfiguration(cfg, "checks.slowjoin.lenght");
        accountCheck = new SlowJoinCheckConfiguration(cfg, "checks.slowjoin.account");
        proxyCheckConfig = new ProxyCheckConfig(cfg);
    }

    public static SlowJoinCheckConfiguration getAccountCheckConfig() {
        return accountCheck;
    }

    public static SlowJoinCheckConfiguration getLenghtCheckConfig() {
        return lenghtCheck;
    }

    public static SlowJoinCheckConfiguration getSimilarNameCheckConfig() {
        return similarNameCheck;
    }

    public static SlowJoinCheckConfiguration getPacketCheckConfig() {
        return packetSlowJoinCheckConfiguration;
    }

    public static ProxyCheckConfig getProxyCheckConfig() {
        return proxyCheckConfig;
    }

    public static void incrementAuthCheckDifficulty(){
        SplittableRandom d = new SplittableRandom();

        int[] a = authMinMaxPing;
        int[] b = authMinMaxTimer;

        for(int j = 0; j < a.length; j++){
            int c = d.nextInt(1, 5);
            a[j] += c;
        }

        for(int j = 0; j < b.length; j++){
            int f = d.nextInt(1, 5);
            a[j] += f;
        }

        authMinMaxPing = a;
        authMinMaxTimer = b;
    }

    public static void restoreAuthCheck() {
        authMinMaxPing = Parser.toIntArray(Parser.toArray(CONFIG.getString("checks.auth.ping"), "-"));
        authMinMaxTimer = Parser.toIntArray(Parser.toArray(CONFIG.getString("checks.auth.ping"), "-"));
    }
}
