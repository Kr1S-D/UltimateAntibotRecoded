package me.kr1s_d.ultimateantibot.common.utils;

import me.kr1s_d.ultimateantibot.common.IServerPlatform;
import me.kr1s_d.ultimateantibot.common.UABRunnable;
import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.io.File;

public class ServerUtil {
    private static IServerPlatform platform;
    public static long blacklistPercentage = 0;
    private static long lastAttack;
    public static long lastStartAttack;

    public static void setPlatform(IServerPlatform platform) {
        ServerUtil.platform = platform;
    }

    public static long getLastAttack() {
        return lastAttack;
    }

    public static void setLastAttack(long lastAttack) {
        ServerUtil.lastAttack = lastAttack;
    }

    public static void cancelTask(UABRunnable runnable){
        platform.cancelTask(runnable.getTaskID());
    }

    public static File getDataFolder(){
        return platform.getDFolder();
    }

    public static void broadcast(String message) {
        platform.broadcast(message);
    }

    public static String colorize(String text){
        return platform.colorize(text);
    }

    public static void log(LogHelper.LogType logType, String message) {
        platform.log(logType, message);
    }
}
