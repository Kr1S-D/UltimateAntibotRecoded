package me.kr1s_d.ultimateantibot.common.core;

import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.detectors.AttackTypeDetector;
import me.kr1s_d.ultimateantibot.common.service.BlackListService;
import me.kr1s_d.ultimateantibot.common.service.DetectorService;
import me.kr1s_d.ultimateantibot.common.service.WhitelistService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;

import javax.print.DocFlavor;

public class UltimateAntiBotCore {
    private final IAntiBotPlugin plugin;
    private final BlackListService blackListService;
    private final WhitelistService whitelistService;

    public UltimateAntiBotCore(IAntiBotPlugin plugin){
        this.plugin = plugin;
        this.blackListService = plugin.getAntiBotManager().getBlackListService();
        this.whitelistService = plugin.getAntiBotManager().getWhitelistService();
        new AttackTypeDetector(plugin);
    }

    public void load() {
        plugin.getLogHelper().info("&fLoading &cCore...");
        plugin.scheduleRepeatingTask(() -> {
            refresh();
            DetectorService.tickDetectors();
        }, false, 1000L);
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

    public void refresh() {
        if(ConfigManger.isConsoleAttackMessageDisabled) {
            return;
        }
        plugin.getAntiBotManager().dispatchConsoleAttackMessage();
    }
}
