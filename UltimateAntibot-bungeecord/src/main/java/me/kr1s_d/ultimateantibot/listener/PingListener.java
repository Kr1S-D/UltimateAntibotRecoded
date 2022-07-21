package me.kr1s_d.ultimateantibot.listener;

import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.checks.NameChangerCheck;
import me.kr1s_d.ultimateantibot.common.objects.profile.BlackListReason;
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
    private final NameChangerCheck changerCheck;

    public PingListener(IAntiBotPlugin plugin){
        this.plugin = plugin;
        this.antiBotManager = plugin.getAntiBotManager();
        this.queueService = antiBotManager.getQueueService();
        blackListService = antiBotManager.getBlackListService();
        whitelistService = antiBotManager.getWhitelistService();
        this.changerCheck = new NameChangerCheck(plugin);
    }

    @EventHandler(priority = -128)
    public void onPing(ProxyPingEvent e){
        String ip = Utils.getIP(e.getConnection());
        String name = e.getConnection().getName();
        antiBotManager.increasePingPerSecond();
        if(!blackListService.isBlackListed(ip) && antiBotManager.isPingModeEnabled()){
            antiBotManager.increaseChecksPerSecond();
        }
        if(changerCheck.isDenied(ip, name)){
            blackListService.blacklist(ip, BlackListReason.TOO_MUCH_NAMES, name);
        }
        //PingMode checks
        if(antiBotManager.isSomeModeOnline()){
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
