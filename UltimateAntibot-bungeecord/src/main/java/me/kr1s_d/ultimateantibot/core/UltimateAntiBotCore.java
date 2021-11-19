package me.kr1s_d.ultimateantibot.core;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;

public class UltimateAntiBotCore {
    private final IAntiBotPlugin plugin;

    public UltimateAntiBotCore(IAntiBotPlugin plugin){
        this.plugin = plugin;
    }

    public void coreRefresher(){
        plugin.scheduleRepeatingTask(() -> {
            plugin.getAntiBotManager().onCoreRefresh();
        }, true, 1000);
    }

    // TODO: 19/11/2021 AUTOPURGER 
}
