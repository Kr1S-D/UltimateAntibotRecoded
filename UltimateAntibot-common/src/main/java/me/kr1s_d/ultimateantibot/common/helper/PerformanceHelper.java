package me.kr1s_d.ultimateantibot.common.helper;

import me.kr1s_d.ultimateantibot.common.IServerPlatform;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.ServerUtil;
import me.kr1s_d.ultimateantibot.common.utils.Version;

public class PerformanceHelper {
    private static PerformanceMode performanceMode;
    private static ServerType serverType;

    public static void init(ServerType serverType) {
        int cores = Version.getCores();
        PerformanceHelper.serverType = serverType;

        //return if disable
        if (!ConfigManger.detectServerPerformance) {
            performanceMode = PerformanceMode.CUSTOM;
            return;
        }

        //continue if enabled
        if (cores <= 2) {
            performanceMode = PerformanceMode.LOW;
        }

        if (cores > 2 && cores <= 8) {
            performanceMode = PerformanceMode.AVERAGE;
        }

        if (cores > 8) {
            performanceMode = PerformanceMode.MAX;
        }

        ConfigManger.antiBotModeTrigger = performanceMode.getAntiBotModeTrigger();
        ServerUtil.debug("Changed value of antibotmodetrigger to " + performanceMode.getAntiBotModeTrigger() + " due performancemode!");
    }

    public static PerformanceMode get() {
        return performanceMode;
    }

    public static boolean isEnabled() {
        return ConfigManger.detectServerPerformance;
    }

    public static ServerType getRunning() {
        return serverType;
    }
}
