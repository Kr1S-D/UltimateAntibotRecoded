package me.kr1s_d.ultimateantibot.common.utils;

import me.kr1s_d.ultimateantibot.common.IServerPlatform;
import me.kr1s_d.ultimateantibot.common.UABRunnable;

import java.io.File;

public class ServerUtil {
    private static IServerPlatform platform;

    public static void setPlatform(IServerPlatform platform) {
        ServerUtil.platform = platform;
    }

    public static void cancelTask(UABRunnable runnable){
        platform.cancelTask(runnable.getTaskID());
    }

    public static File getDataFolder(){
        return platform.getDFolder();
    }
}
