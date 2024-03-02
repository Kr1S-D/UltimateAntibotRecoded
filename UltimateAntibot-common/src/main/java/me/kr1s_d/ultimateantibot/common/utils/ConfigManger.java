package me.kr1s_d.ultimateantibot.common.utils;

import me.kr1s_d.ultimateantibot.common.IConfiguration;
import me.kr1s_d.ultimateantibot.common.objects.config.ProxyCheckConfig;
import me.kr1s_d.ultimateantibot.common.objects.config.SlowCheckConfig;

import java.util.List;
import java.util.SplittableRandom;

public class ConfigManger {
    public static int joinCacheJoinMinutes = 2;
    private static IConfiguration CONFIG;

    public static double version;
    public static boolean isDebugModeOnline;
    public static boolean detectServerPerformance;
    public static boolean enableLatencyThread;
    public static boolean enableBossBarAutomaticNotification;
    public static boolean disableNotificationAfterAttack;
    public static boolean isConsoleAttackMessageDisabled;
    public static int antiBotModeKeep;
    public static int antiBotModeTrigger;
    public static boolean antibotDisconnect;
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
    public static boolean isLegalNameCheckEnabled;
    public static String legalNameCheckRegex;
    public static List<String> invalidNamesBlockedEntries;
    public static boolean isInvalidNameCheckBlacklist;
    public static boolean isInvalidNameCheckEnabled;
    public static List<String> registerCheckCommandListeners;
    public static int registerCheckLimit;
    public static boolean isRegisterCheckBlacklist;
    public static boolean isRegisterCheckAntiBotMode;
    public static boolean isRegisterCheckEnabled;
    public static boolean isIPApiVerificationEnabled;
    private static SlowCheckConfig packetSlowCheckConfig;
    private static SlowCheckConfig accountCheckConfig;
    private static ProxyCheckConfig proxyCheckConfig;


    public static void init(IConfiguration cfg){
        CONFIG = cfg;

        version = cfg.getDouble("version");
        isDebugModeOnline = cfg.getBoolean("debug");
        enableLatencyThread = cfg.getBoolean("enable-latency-thread");
        enableBossBarAutomaticNotification = cfg.getBoolean("enable-bossbar-automatic-notification");
        disableNotificationAfterAttack = cfg.getBoolean("disable-notifications-after-attack");
        isIPApiVerificationEnabled = cfg.getBoolean("ip-api.enabled");
        isConsoleAttackMessageDisabled = cfg.getBoolean("disable-console-attack-message");
        detectServerPerformance = cfg.getBoolean("detect-server-performance");
        antiBotModeKeep = cfg.getInt("antibotmode.keep");
        antiBotModeTrigger = cfg.getInt("antibotmode.trigger");
        antibotDisconnect = cfg.getBoolean("antibotmode.disconnect");
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
        isLegalNameCheckEnabled = cfg.getBoolean("checks.legalname.enabled");
        legalNameCheckRegex = cfg.getString("checks.legalname.name-regex");
        invalidNamesBlockedEntries = cfg.getStringList("checks.invalidname.invalid");
        isInvalidNameCheckBlacklist = cfg.getBoolean("checks.invalidname.blacklist");
        isInvalidNameCheckEnabled = cfg.getBoolean("checks.invalidname.enabled");
        registerCheckCommandListeners = cfg.getStringList("checks.strange-register.listen");
        registerCheckLimit = cfg.getInt("checks.strange-register.limit");
        isRegisterCheckBlacklist = cfg.getBoolean("checks.strange-register.blacklist");
        isRegisterCheckAntiBotMode = cfg.getBoolean("checks.strange-register.antibotmode");
        isRegisterCheckEnabled = cfg.getBoolean("checks.strange-register.enabled");
        packetSlowCheckConfig = new SlowCheckConfig(cfg, "checks.slowjoin.packet");
        accountCheckConfig = new SlowCheckConfig(cfg, "checks.slowjoin.account");
        proxyCheckConfig = new ProxyCheckConfig(cfg);
    }

    public static SlowCheckConfig getAccountCheckConfig() {
        return accountCheckConfig;
    }

    public static SlowCheckConfig getPacketCheckConfig() {
        return packetSlowCheckConfig;
    }

    public static ProxyCheckConfig getProxyCheckConfig() {
        return proxyCheckConfig;
    }

    public static String getAutoPurgerParam(String path) {
        return CONFIG.getString("auto-purger." + path);
    }

    public static boolean getAutoPurgerBoolean(String path) {
        return CONFIG.getBoolean("auto-purger." + path);
    }

    public static int getAutoPurgerValue(String path) {
        return CONFIG.getInt("auto-purger." + path);
    }

    public static String getStringParam(String path) {
        return CONFIG.getString(path);
    }

    public static boolean getBooleanParam(String path) {
        return CONFIG.getBoolean(path);
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

        authMinMaxPing = fix(a);
        authMinMaxTimer = fix(b);
    }

    public static void restoreAuthCheck() {
        authMinMaxPing = Parser.toIntArray(Parser.toArray(CONFIG.getString("checks.auth.ping"), "-"));
        authMinMaxTimer = Parser.toIntArray(Parser.toArray(CONFIG.getString("checks.auth.ping"), "-"));
    }

    /**
     * Facciamo in modo che non ci siano problemi con i numeri
     * In caso di numeri uguali, o maggiori / minori invertiti
     *
     * @param i int array to fix
     * @return fixed int array
     */
    private static int[] fix(int[] i){
        if(i[0] == i[1]){
            i[1] += 2;
            return i;
        }

        int min = Math.min(i[0], i[1]);
        int max = Math.max(i[0], i[1]);
        return new int[] {min, max};
    }
}
