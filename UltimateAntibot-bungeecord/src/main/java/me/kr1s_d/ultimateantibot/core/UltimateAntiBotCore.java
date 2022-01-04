package me.kr1s_d.ultimateantibot.core;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.ICore;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

public class UltimateAntiBotCore implements ICore {
    private final IAntiBotPlugin plugin;

    public UltimateAntiBotCore(IAntiBotPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void load() {
        plugin.getLogHelper().info("&fLoading &dCore...");
        plugin.scheduleRepeatingTask(this::refresh, false, 1000L);
        plugin.scheduleRepeatingTask(plugin.getAntiBotManager().getQueueService()::clear, false, ConfigManger.taskManagerClearCache * 1000L);
    }

    @Override
    public void refresh() {
        plugin.getAntiBotManager().updateTasks();
    }

    // TODO: 19/11/2021 AUTOPURGER 
}
