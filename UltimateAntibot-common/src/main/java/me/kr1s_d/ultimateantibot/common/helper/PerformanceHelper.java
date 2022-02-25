package me.kr1s_d.ultimateantibot.common.helper;

import me.kr1s_d.ultimateantibot.common.helper.enums.PerformanceMode;
import me.kr1s_d.ultimateantibot.common.helper.enums.Running;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.PerformanceConfig;
import me.kr1s_d.ultimateantibot.common.utils.Version;

public class PerformanceHelper {
    private static PerformanceMode performanceMode;
    private static Running running;
    private static PerformanceConfig performanceConfig;

    public static void init(Running running){
        int cores = Version.getCores();
        PerformanceHelper.running = running;
        if(!ConfigManger.detectServerPerformance){
            performanceMode = PerformanceMode.CUSTOM;
            return;
        }
        if(cores <= 2){
            performanceMode = PerformanceMode.LOW;
            return;
        }
        if(cores <= 4 && running.equals(Running.BUNGEECORD) || running.equals(Running.VELOCITY)){
            performanceMode = PerformanceMode.AVERAGE;
            return;
        }
        if(cores <= 6 && running.equals(Running.SPIGOT)){
            performanceMode = PerformanceMode.AVERAGE;
            return;
        }
        if(cores > 16){
            performanceMode = PerformanceMode.MAX;
        }
        performanceConfig = new PerformanceConfig(performanceMode);
    }

    public static PerformanceConfig getConfig() {
        return performanceConfig;
    }

    public static PerformanceMode getPerformanceMode() {
        return performanceMode;
    }

    public static Running getRunning() {
        return running;
    }
}
