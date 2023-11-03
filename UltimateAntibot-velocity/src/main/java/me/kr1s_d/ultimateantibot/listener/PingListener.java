package me.kr1s_d.ultimateantibot.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.service.QueueService;
import me.kr1s_d.ultimateantibot.common.service.WhitelistService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.utils.Utils;

public class PingListener {
    private final IAntiBotManager antiBotManager;
    private final QueueService queueService;
    private final WhitelistService whitelistService;

    public PingListener(IAntiBotPlugin plugin) {
        this.antiBotManager = plugin.getAntiBotManager();
        this.queueService = antiBotManager.getQueueService();
        whitelistService = antiBotManager.getWhitelistService();
    }

    @Subscribe (order = PostOrder.NORMAL)
    public void onPing(ProxyPingEvent e) {
        String ip = Utils.getIP(e.getConnection().getRemoteAddress().getAddress());
        antiBotManager.increasePingPerSecond();

        // (!) USELESS IN VELOCITY (!)
        //PingMode checks
        //if(antiBotManager.isSomeModeOnline()){
        //    if(!ConfigManger.pingModeSendInfo){
        //        ServerPing ping = e.getResponse();
        //        ping.setFavicon("");
        //        e.setResponse(ping);
        //   }

        //Enable ping mode
        if(antiBotManager.getPingPerSecond()>ConfigManger.pingModeTrigger &&!antiBotManager.isAntiBotModeEnabled()) {
            if (!antiBotManager.isPingModeEnabled()) {
                antiBotManager.enablePingMode();
            }
        }

        //Whitelist protection
        if(whitelistService.isWhitelisted(ip)) {
            queueService.removeQueue(ip);
        }
    }
}
