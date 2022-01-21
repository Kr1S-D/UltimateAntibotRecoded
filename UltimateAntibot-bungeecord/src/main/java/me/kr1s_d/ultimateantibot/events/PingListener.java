package me.kr1s_d.ultimateantibot.events;

import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.service.BlackListService;
import me.kr1s_d.ultimateantibot.common.service.QueueService;
import me.kr1s_d.ultimateantibot.common.service.WhitelistService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.utils.Utils;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PingListener implements Listener {
    private IAntiBotPlugin plugin;
    private final IAntiBotManager antiBotManager;
    private final QueueService queueService;
    private final BlackListService blackListService;
    private final WhitelistService whitelistService;
    // TODO: 03/12/2021 superping check;

    public PingListener(IAntiBotPlugin plugin){
        this.plugin = plugin;
        this.antiBotManager = plugin.getAntiBotManager();
        this.queueService = antiBotManager.getQueueService();
        blackListService = antiBotManager.getBlackListService();
        whitelistService = antiBotManager.getWhitelistService();
    }

    @EventHandler
    public void onPing(ProxyPingEvent e){
        String ip = Utils.getIP(e.getConnection());
        antiBotManager.increasePingPerSecond();
        antiBotManager.increaseTotalPings();
        if(!blackListService.isBlackListed(ip) && antiBotManager.isPingModeEnabled()){
            antiBotManager.increaseChecksPerSecond();
        }
        //PingMode checks
        if(antiBotManager.isPingModeEnabled()){
            if(!ConfigManger.pingModeSendInfo){
                ServerPing ping = e.getResponse();
                ping.setFavicon("");
                e.setResponse(ping);
            }
        }
        //Enable ping mode
        if(antiBotManager.getPingPerSecond() > ConfigManger.pingModeTrigger && !antiBotManager.isAntiBotModeEnabled()){
            if(!antiBotManager.isPingModeEnabled()){
                antiBotManager.enablePingMode();
            }
        }
        //Whitelist protection
        if(whitelistService.isWhitelisted(ip)){
            queueService.removeQueue(ip);
        }
    }
}
