package me.kr1s_d.ultimateantibot.core;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.ICore;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

public class UltimateAntiBotCore implements ICore {
    private final IAntiBotPlugin plugin;

    public UltimateAntiBotCore(IAntiBotPlugin plugin){
        this.plugin = plugin;
    }

    public void coreRefresher(){
        plugin.scheduleRepeatingTask(() -> {
            plugin.getAntiBotManager().onCoreRefresh();
        }, true, 1000);
    }

    public void initCoreCache(){
        plugin.scheduleRepeatingTask(() -> {
            plugin.getAntiBotManager().getQueueService().clear();
        }, true, 1000L * ConfigManger.taskManagerClearCache);
    }

    @Override
    public void load() {
        plugin.getLogHelper().info("&aLoading Core...");
        coreRefresher();
        initCoreCache();
        plugin.getLogHelper().info("&aCore Loaded!");
    }

    @Override
    public void addNewThread(Runnable runnable, long millis) {
        plugin.scheduleDelayedTask(runnable, false, millis);
    }

    // TODO: 19/11/2021 AUTOPURGER 
}
