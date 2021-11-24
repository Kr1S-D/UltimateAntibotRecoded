package me.kr1s_d.ultimateantibot.common.utils;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IConfiguration;
import me.kr1s_d.ultimateantibot.common.objects.other.BlackListProfile;

import java.util.List;
import java.util.Locale;

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
    public static String helpMessage;
    public static String statsMessage;
    private static String antiBotModeMessage;
    public static String firstJoinMessage;
    private static String safeModeMessage;
    private static String accountOnlineMessage;
    private static String pingMessage;
    private static String timerMessage;
    private static String blacklistedMessage;
    public static String reasonTooManyNicks;
    public static String reasonTooManyJoins;
    public static String reasonTooManyPings;
    public static String reasonStrangePlayer;

    public static void init(IConfiguration messages){
        version = messages.getDouble("version");
        prefix = messages.getString("prefix");
        commandNoPerms = messages.getString("commands.perms");
        commandCleared = messages.getString("commands.cleared");
        commandAdded = messages.getString("commands.added");
        commandRemove = messages.getString("commands.removed");
        actionbarOffline = messages.getString("actionbar.offline");
        actionbarAntiBotMode = messages.getString("actionbar.antibot");
        actionbarPackets = messages.getString("actionbar.packets");
        helpMessage = convertToString(messages.getStringList("help"));
        statsMessage = convertToString(messages.getStringList("stats"));
        antiBotModeMessage = convertToString(messages.getStringList("antibotmode"));
        firstJoinMessage = convertToString(messages.getStringList("first_join"));
        safeModeMessage = convertToString(messages.getStringList("safe_mode"));
        accountOnlineMessage = convertToString(messages.getStringList("account-online"));
        pingMessage = convertToString(messages.getStringList("ping"));
        timerMessage = convertToString(messages.getStringList("timer"));
        blacklistedMessage = convertToString(messages.getStringList("blacklisted"));
        reasonTooManyNicks = messages.getString("reason.names");
        reasonTooManyJoins = messages.getString("reason.joins");
        reasonTooManyPings = messages.getString("reason.pings");
        reasonStrangePlayer = messages.getString("reason.strange");
    }

    public static String getCommandNoPerms() {
        return commandNoPerms;
    }

    public static String getCommandCleared(String what) {
        return commandCleared.replace("$1", what);
    }

    public static String getCommandAdded(String ip, String were) {
        return commandAdded.replace("$2", were).replace("$1", ip);
    }

    public static String getCommandRemove(String ip, String were) {
        return commandRemove.replace("$2", were).replace("$1", ip);
    }

    public static String getAntiBotModeMessage(String checkPercent, String blackListPercentage) {
        return antiBotModeMessage.replace("$2", blackListPercentage).replace("$1", checkPercent);
    }

    public static String getSafeModeMessage() {
        return safeModeMessage;
    }

    public static String getAccountOnlineMessage() {
        return accountOnlineMessage;
    }

    public static String getPingMessage(String times) {
        return pingMessage.replace("$1", times);
    }

    public static String getTimerMessage(String times) {
        return timerMessage.replace("$1", times);
    }

    public static String getBlacklistedMessage(BlackListProfile profile) {
        return blacklistedMessage.replace("$2", profile.getId()).replace("$1", profile.getReason());
    }

    private static String convertToString(List<String> stringList) {
        return String.join(System.lineSeparator(), stringList);
    }
}
