package me.kr1s_d.ultimateantibot.common.core;

import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.service.BlackListService;
import me.kr1s_d.ultimateantibot.common.service.DetectorService;
import me.kr1s_d.ultimateantibot.common.service.UserDataService;
import me.kr1s_d.ultimateantibot.common.service.WhitelistService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.ServerUtil;

import java.util.concurrent.TimeUnit;

public class UltimateAntiBotCore {
    private final IAntiBotPlugin plugin;
    private final BlackListService blackListService;
    private final WhitelistService whitelistService;
    private final UserDataService userDataService;

    public UltimateAntiBotCore(IAntiBotPlugin plugin){
        this.plugin = plugin;
        this.blackListService = plugin.getAntiBotManager().getBlackListService();
        this.whitelistService = plugin.getAntiBotManager().getWhitelistService();
        this.userDataService = plugin.getUserDataService();
    }

    public void load() {
        plugin.getLogHelper().info("&fLoading &cCore...");

        plugin.scheduleRepeatingTask(() -> {
            refresh();
            DetectorService.tickDetectors();
        }, false, 1000L);

        plugin.scheduleRepeatingTask(() -> {
            if(plugin.getAntiBotManager().isAntiBotModeEnabled()) {
               return;
            }

            for(String blacklisted : blackListService.getBlackListedIPS()) {
                whitelistService.unWhitelist(blacklisted);
            }

            whitelistService.checkWhitelisted();
            checkAutoPurger();
        }, false, 1000L * 300); //5m
    }

    public void refresh() {
        if(ConfigManger.isConsoleAttackMessageDisabled) {
            return;
        }
        plugin.getAntiBotManager().dispatchConsoleAttackMessage();
    }

    private void checkAutoPurger() {
        long currentTimeMillis = System.currentTimeMillis();
        long elapsedTimeMillis = currentTimeMillis - ServerUtil.getLastAttack();
        long elapsedTimeMinutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTimeMillis);
        //if the server is under attack or the last attack was less than 10 minutes ago it returns
        if(elapsedTimeMinutes < 10 || plugin.getAntiBotManager().isSomeModeOnline()) return;

        //check for blacklist autopurge
        String blacklistType = ConfigManger.getAutoPurgerParam("blacklist.type");
        boolean blacklistEnabled = ConfigManger.getAutoPurgerBoolean("blacklist.enabled");

        if (blacklistType != null && blacklistEnabled) {
            switch (blacklistType) {
                case "LIMIT":
                    int required = ConfigManger.getAutoPurgerValue("blacklist.value");
                    if(plugin.getAntiBotManager().getBlackListService().size() >= required) {
                        plugin.getAntiBotManager().getBlackListService().clear();
                    }
                    break;
                case "AFTER_ATTACK":
                    plugin.getAntiBotManager().getBlackListService().clear();
                    break;
            }
        }

        //check for whitelist autopurge
        String whitelistType = ConfigManger.getAutoPurgerParam("whitelist.type");
        boolean whitelistEnabled = ConfigManger.getAutoPurgerBoolean("whitelist.enabled");

        if (whitelistType != null && whitelistEnabled) {
            switch (whitelistType) {
                case "LIMIT":
                    int required = ConfigManger.getAutoPurgerValue("whitelist.value");
                    if(plugin.getAntiBotManager().getWhitelistService().size() >= required) {
                        plugin.getAntiBotManager().getWhitelistService().clear();
                    }
                    break;
                case "AFTER_ATTACK":
                    plugin.getAntiBotManager().getWhitelistService().clear();
                    break;
            }
        }
    }
}
