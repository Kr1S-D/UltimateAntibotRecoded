package me.kr1s_d.ultimateantibot.core;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.ICore;
import me.kr1s_d.ultimateantibot.common.service.BlackListService;
import me.kr1s_d.ultimateantibot.common.service.WhitelistService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

public class UltimateAntiBotCore implements ICore {
    private final IAntiBotPlugin plugin;
    private final BlackListService blackListService;
    private final WhitelistService whitelistService;

    public UltimateAntiBotCore(IAntiBotPlugin plugin){
        this.plugin = plugin;
        this.blackListService = plugin.getAntiBotManager().getBlackListService();
        this.whitelistService = plugin.getAntiBotManager().getWhitelistService();
    }

    @Override
    public void load() {
        plugin.getLogHelper().info("&fLoading &cCore...");
        plugin.scheduleRepeatingTask(this::refresh, false, 1000L);
        plugin.scheduleRepeatingTask(plugin.getAntiBotManager().getQueueService()::clear, false, ConfigManger.taskManagerClearCache * 1000L);
        plugin.scheduleRepeatingTask(() -> {
            if(plugin.getAntiBotManager().isAntiBotModeEnabled()){
               return;
            }
            for(String blacklisted : blackListService.getBlackListedIPS()){
                whitelistService.unWhitelist(blacklisted);
            }
        }, false, 1000L * ConfigManger.taskManagerClearCache);
    }

    @Override
    public void refresh() {
        plugin.getAntiBotManager().updateTasks();
    }

    // TODO: 19/11/2021 AUTOPURGER 
}
