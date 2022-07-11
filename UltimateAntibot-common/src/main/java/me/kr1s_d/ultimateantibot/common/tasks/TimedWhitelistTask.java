package me.kr1s_d.ultimateantibot.common.tasks;

import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.UABRunnable;

import java.util.concurrent.TimeUnit;

public class TimedWhitelistTask implements UABRunnable {
    private final IAntiBotPlugin plugin;
    private final IAntiBotManager antiBotManager;
    private final String ip;

    public TimedWhitelistTask(IAntiBotPlugin plugin, String ip) {
        this.plugin = plugin;
        this.antiBotManager = plugin.getAntiBotManager();
        this.ip = ip;
    }

    @Override
    public void run() {
        antiBotManager.getWhitelistService().unWhitelist(ip);
        plugin.scheduleDelayedTask(new UABRunnable() {
            @Override
            public boolean isAsync() {
                return false;
            }

            @Override
            public long getPeriod() {
                return 1000L * 300L;
            }

            @Override
            public void run() {
                if(plugin.isConnected(ip)){
                    antiBotManager.getWhitelistService().whitelist(ip);
                }
            }
        });
    }

    @Override
    public boolean isAsync(){
        return false;
    }

    @Override
    public long getPeriod(){
        return 2000L;
    }
}
