package me.kr1s_d.ultimateantibot.events;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.service.BlackListService;
import me.kr1s_d.ultimateantibot.common.service.QueueService;
import me.kr1s_d.ultimateantibot.common.service.WhitelistService;
import me.kr1s_d.ultimateantibot.utils.Utils;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class MainEventListener implements Listener {
    private IAntiBotPlugin antiBotPlugin;
    private final IAntiBotManager antiBotManager;
    private final QueueService queueService;
    private final WhitelistService whitelistService;
    private final BlackListService blackListService;

    public MainEventListener(IAntiBotPlugin antiBotPlugin){
        this.antiBotPlugin = antiBotPlugin;
        this.antiBotManager = antiBotPlugin.getAntiBotManager();
        this.queueService = antiBotManager.getQueueService();
        this.whitelistService = antiBotManager.getWhitelistService();
        this.blackListService = antiBotManager.getBlackListService();
    }

    @EventHandler(priority = -128)
    public void onPreLoginEvent(PreLoginEvent e){
        String ip = Utils.getIP(e.getConnection());
        String name = e.getConnection().getName();
        String uuid = e.getConnection().getUniqueId().toString();
        int blacklistedPercentage = 0;
        int totals = blackListService.size() + queueService.size();
        if(blackListService.size() != 0 && totals != 0) {
            blacklistedPercentage = Math.round((float) blackListService.size() / totals * 100);
        }
        //
        //AntiBotInfo Update & Queue clear
        //
        if(whitelistService.isWhitelisted(ip) || blackListService.isBlackListed(ip)){
            queueService.removeQueue(ip);
        }
        if(!whitelistService.isWhitelisted(ip)){
            antiBotManager.increaseJoinPerSecond();
            if(!blackListService.isBlackListed(ip)){
                antiBotManager.increaseChecksPerSecond();
            }
        }
        if(antiBotManager.isAntiBotModeEnabled()){
            antiBotManager.increaseTotalBots();
        }
        //
        //BlackList & Whitelist Checks
        //
        if(blackListService.isBlackListed(ip)){
            whitelistService.unWhitelist(ip);

        }
    }

}
