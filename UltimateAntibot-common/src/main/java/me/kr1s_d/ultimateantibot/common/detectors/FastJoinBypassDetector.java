package me.kr1s_d.ultimateantibot.common.detectors;

import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

public class FastJoinBypassDetector extends AbstractDetector {
    private final IAntiBotPlugin plugin;
    private int count;

    public FastJoinBypassDetector(IAntiBotPlugin plugin) {
        this.plugin = plugin;
        this.count = 0;
    }

    public void registerJoin(){
        IAntiBotManager antiBotManager = plugin.getAntiBotManager();
        if(antiBotManager.isAntiBotModeEnabled() && antiBotManager.getPingPerSecond() > 50){
            count++;
        }

        if(count > 2){
            ConfigManger.incrementAuthCheckDifficulty();
            plugin.getLogHelper().debug("[BYPASS DETECTED] Incrementing auth check difficulty...");
        }
    }

    @Override
    public int getTickDelay() {
        return 5;
    }

    @Override
    public void tick() {
        count = 0;
    }
}
