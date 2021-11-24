package me.kr1s_d.ultimateantibot.events;

import me.kr1s_d.ultimateantibot.common.checks.FirstJoinCheck;
import me.kr1s_d.ultimateantibot.common.checks.NameChangerCheck;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.service.BlackListService;
import me.kr1s_d.ultimateantibot.common.service.QueueService;
import me.kr1s_d.ultimateantibot.common.service.WhitelistService;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.ComponentBuilder;
import me.kr1s_d.ultimateantibot.utils.Utils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class MainEventListener implements Listener {
    private final IAntiBotManager antiBotManager;
    private final QueueService queueService;
    private final WhitelistService whitelistService;
    private final BlackListService blackListService;
    private final FirstJoinCheck firstJoinCheck;
    private final NameChangerCheck nameChangerCheck;

    public MainEventListener(IAntiBotPlugin antiBotPlugin){
        this.antiBotManager = antiBotPlugin.getAntiBotManager();
        this.queueService = antiBotManager.getQueueService();
        this.whitelistService = antiBotManager.getWhitelistService();
        this.blackListService = antiBotManager.getBlackListService();
        this.firstJoinCheck = new FirstJoinCheck(antiBotPlugin);
        this.nameChangerCheck = new NameChangerCheck(antiBotPlugin);
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
        if(whitelistService.isWhitelisted(ip)){
            return;
        }
        if(blackListService.isBlackListed(ip)){
            whitelistService.unWhitelist(ip);
            e.setCancelReason(blacklistMSG(ip));
            e.setCancelled(true);
            return;
        }
        //
        //FirstJoinCheck
        //
        if(firstJoinCheck.needToDeny(ip, name, uuid)){
            e.setCancelReason(ComponentBuilder.buildColorized(MessageManager.firstJoinMessage));
            e.setCancelled(true);
            return;
        }
        //
        //Queue Service
        //
        if(!queueService.isQueued(ip) && !blackListService.isBlackListed(ip) && !whitelistService.isWhitelisted(ip)){
            queueService.queue(ip);
        }
        //
        //Some Checks
        //
        if(antiBotManager.isAntiBotModeEnabled()) {
            //
            // NameChangerCheck
            //
            if (nameChangerCheck.needToDeny(ip, name, uuid)) {
                blackListService.blacklist(ip, MessageManager.reasonTooManyNicks);
                e.setCancelReason(blacklistMSG(ip));
                e.setCancelled(true);
                return;
            }
            //
            // SuperJoinCheck
            //
        }
    }

    private BaseComponent blacklistMSG(String ip){
        return ComponentBuilder.buildColorized(MessageManager.getBlacklistedMessage(blackListService.getProfile(ip)));
    }

}
