package me.kr1s_d.ultimateantibot.core;

import me.kr1s_d.ultimateantibot.AntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.ICore;
import me.kr1s_d.ultimateantibot.common.service.BlackListService;
import me.kr1s_d.ultimateantibot.common.service.WhitelistService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

import java.util.ArrayList;
import java.util.List;

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
        if(!ConfigManger.isConsoleAttackMessageDisabled) {
            plugin.scheduleRepeatingTask(this::refresh, false, 1000L);
        }
        plugin.scheduleRepeatingTask(plugin.getAntiBotManager().getQueueService()::clear, false, ConfigManger.taskManagerClearCache * 1000L);
        plugin.scheduleRepeatingTask(() -> {
            if(plugin.getAntiBotManager().isAntiBotModeEnabled()){
               return;
            }
            for(String blacklisted : blackListService.getBlackListedIPS()){
                whitelistService.unWhitelist(blacklisted);
            }
        }, false, 1000L * ConfigManger.taskManagerClearCache);
        plugin.scheduleRepeatingTask(() -> {
            IAntiBotManager antiBotManager = plugin.getAntiBotManager();
            List<String> used = new ArrayList<>(antiBotManager.getBlackListService().getBlackListedIPS());
            used.addAll(antiBotManager.getWhitelistService().getWhitelistedIPS());

            for(String usd : used){
                if(antiBotManager.getWhitelistService().isWhitelisted(usd)){
                    antiBotManager.getQueueService().removeQueue(usd);
                }
                if(antiBotManager.getBlackListService().isBlackListed(usd)){
                    antiBotManager.getQueueService().removeQueue(usd);
                }
            }
        }, false, 1000L * ConfigManger.taskManagerUpdate);
    }

    @Override
    public void refresh() {
        plugin.getAntiBotManager().updateTasks();
    }

    // TODO: 19/11/2021 AUTOPURGER 
}
