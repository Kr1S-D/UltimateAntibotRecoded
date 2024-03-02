package me.kr1s_d.ultimateantibot.listener;

import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.service.QueueService;
import me.kr1s_d.ultimateantibot.common.service.WhitelistService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class PingListener implements Listener {
    private final IAntiBotManager antiBotManager;
    private final QueueService queueService;
    private final WhitelistService whitelistService;

    public PingListener(IAntiBotPlugin plugin){
        this.antiBotManager = plugin.getAntiBotManager();
        this.queueService = antiBotManager.getQueueService();
        whitelistService = antiBotManager.getWhitelistService();
    }

    @EventHandler
    public void onPing(ServerListPingEvent e){
        String ip = Utils.getInetAddressIP(e.getAddress());
        antiBotManager.increasePingPerSecond();

        //PingMode checks
        if(antiBotManager.isSomeModeOnline()){
            if(!ConfigManger.pingModeSendInfo){
                e.setServerIcon(null);
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
