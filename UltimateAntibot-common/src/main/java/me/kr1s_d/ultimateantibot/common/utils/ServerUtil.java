package me.kr1s_d.ultimateantibot.common.utils;

import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.UABRunnable;
import me.kr1s_d.ultimateantibot.common.helper.LogHelper;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class ServerUtil {
    private static IAntiBotPlugin instance;
    public static long blacklistPercentage = 0;
    private static long lastAttack = -1;
    public static long lastStartAttack;

    public static void setInstance(IAntiBotPlugin instance) {
        ServerUtil.instance = instance;
    }

    public static long getLastAttack() {
        return lastAttack;
    }

    public static long getSecondsFromLastAttack() {
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastAttack);
    }

    public static void setLastAttack(long lastAttack) {
        ServerUtil.lastAttack = lastAttack;
    }

    public static void cancelTask(UABRunnable runnable){
        instance.cancelTask(runnable.getTaskID());
    }

    public static File getDataFolder(){
        return instance.getDFolder();
    }

    public static void broadcast(String message) {
        instance.broadcast(message);
    }

    public static String colorize(String text){
        return instance.colorize(text);
    }

    public static void log(LogHelper.LogType logType, String message) {
        instance.log(logType, message);
    }

    public static void debug(String s) {
        instance.getLogHelper().debug(s);
    }

    public static IAntiBotPlugin getInstance() {
        return instance;
    }
}
